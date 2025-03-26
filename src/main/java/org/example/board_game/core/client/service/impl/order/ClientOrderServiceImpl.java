package org.example.board_game.core.client.service.impl.order;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.client.domain.dto.request.order.ClientCartItemRequest;
import org.example.board_game.core.client.domain.dto.request.order.ClientOrderRequest;
import org.example.board_game.core.client.domain.mapper.customer.ClientAddressMapper;
import org.example.board_game.core.client.domain.mapper.order.ClientOrderMapper;
import org.example.board_game.core.client.service.order.ClientOrderService;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.entity.order.Order;
import org.example.board_game.entity.product.Product;
import org.example.board_game.entity.voucher.Voucher;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.enums.OrderStatus;
import org.example.board_game.infrastructure.enums.OrderType;
import org.example.board_game.infrastructure.enums.PaymentMethod;
import org.example.board_game.infrastructure.enums.VoucherType;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.repository.customer.CustomerRepository;
import org.example.board_game.repository.order.OrderRepository;
import org.example.board_game.repository.product.ProductRepository;
import org.example.board_game.repository.voucher.VoucherRepository;
import org.example.board_game.utils.CollectionUtils;
import org.example.board_game.utils.Response;
import org.example.board_game.utils.StrUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClientOrderServiceImpl implements ClientOrderService {

    OrderRepository orderRepository;
    EntityService entityService;
    ProductRepository productRepository;
    CustomerRepository customerRepository;
    VoucherRepository voucherRepository;
    ClientOrderMapper orderMapper = ClientOrderMapper.INSTANCE;
    ClientAddressMapper addressMapper = ClientAddressMapper.INSTANCE;

    @Transactional
    @Override
    public Response<Object> createOrder(ClientOrderRequest request) {

        List<ClientCartItemRequest> cartItems = request.getCartItems();
        if (CollectionUtils.isListEmpty(cartItems)) {
            throw new ResourceNotFoundException("Vui lòng chọn sản phẩm để thanh toán.");
        }
        List<String> productIds = CollectionUtils.extractField(cartItems, ClientCartItemRequest::getProductId);
        List<Product> products = productRepository.findAllByIds(productIds);
        Map<String, Product> productMap = CollectionUtils.collectToMap(products, Product::getId);

        List<String> exceededProducts = cartItems.stream()
                .filter(item -> {
                    Product product = productMap.get(item.getProductId());
                    return product == null || product.getQuantity() < item.getQuantity();
                })
                .map(item -> {
                    Product product = productMap.get(item.getProductId());
                    return product != null ? product.getName() : "Sản phẩm không tồn tại";
                }).toList();

        if (!exceededProducts.isEmpty()) {
            throw new ApiException(String
                    .format("Các sản phẩm sau vượt quá số lượng tồn kho: %s", String.join(", ", exceededProducts)));
        }
        OrderStatus status = request.getPaymentMethod() == PaymentMethod.CASH ? OrderStatus.WAIT_FOR_CONFIRMATION : OrderStatus.PENDING;
        float totalMoney = totalCartItem(cartItems, productMap);

        Order order = orderMapper.toEntity(request);
        order.setType(OrderType.ONLINE);
        order.setStatus(status);
        order.setCustomer(entityService.getCustomerByAuth());
        order.setEmployee(null);
        order.setShippingMoney(request.getShippingMoney());
        order.setOriginMoney(totalMoney);
        applyVoucherToOrder(order, request.getVoucherId(), totalMoney, "add");
        order.setTotalMoney(order.getTotalMoney() + order.getShippingMoney());
        order.setExpectedDeliveryDate(new Date().getTime() + EntityProperties.DELIVERY_TIME_IN_MILLIS);


        return null;
    }

    @Override
    public Response<Object> updateOrder(ClientOrderRequest request) {
        return null;
    }


    public void applyVoucherToOrder(Order order, String voucherId, float totalOrderPrice, String action) {

        if (StrUtils.isNotBlank(voucherId)) {
            Voucher voucher = entityService.getVoucher(voucherId);
            if (voucher.getConstraint() > totalOrderPrice) {
                throw new ApiException("Đơn hàng của bạn không đủ điều kiện để áp dụng voucher này.");
            }
            if (action.equals("add")) {
                if (voucher.getQuantity() < 1) {
                    throw new ApiException("Voucher này hiện tại đã hết.");
                }
                updateQuantityVoucher(voucher, voucher.getQuantity() - 1);
            } else {
                if (order.getVoucher() != null) {
                    if (!voucherId.equals(order.getVoucher().getId())) {
                        if (voucher.getQuantity() < 1) {
                            throw new ApiException("Voucher này hiện tại đã hết.");
                        }
                        Voucher voucherOld = order.getVoucher();
                        updateQuantityVoucher(voucherOld, voucherOld.getQuantity() + 1);
                        updateQuantityVoucher(voucher, voucher.getQuantity() - 1);
                    }
                } else {
                    if (voucher.getQuantity() < 1) {
                        throw new ApiException("Voucher này hiện tại đã hết.");
                    }
                    updateQuantityVoucher(voucher, voucher.getQuantity() - 1);
                }
            }
            order.setVoucher(voucher);
            float discount = voucher.getType() == VoucherType.CASH ? voucher.getValue() : (voucher.getValue() / 100) * totalOrderPrice;
            float finalTotalPrice = Math.max(0, totalOrderPrice - discount);
            order.setReduceMoney(discount);
            order.setTotalMoney(finalTotalPrice);

        } else {
            order.setReduceMoney(0.0f);
            order.setTotalMoney(totalOrderPrice);
            if (order.getVoucher() != null) {
                Voucher voucher = order.getVoucher();
                updateQuantityVoucher(voucher, voucher.getQuantity() + 1);
            }
            order.setVoucher(null);
        }
    }

    private void updateQuantityVoucher(Voucher voucher, int quantity) {
        voucher.setQuantity(quantity);
        voucherRepository.save(voucher);
    }

    public float totalCartItem(List<ClientCartItemRequest> cartItems, Map<String, Product> productMap) {
        List<Product> products = new ArrayList<>();
        float total = 0.0f;

        for (ClientCartItemRequest request : cartItems) {
            String productId = request.getProductId();
            Product product = productMap.get(productId);

            float price = product.getPrice();
            total += price * request.getQuantity();
            product.setQuantity(product.getQuantity() - request.getQuantity());
            products.add(product);
        }
        productRepository.saveAll(products);
        return total;
    }

}
