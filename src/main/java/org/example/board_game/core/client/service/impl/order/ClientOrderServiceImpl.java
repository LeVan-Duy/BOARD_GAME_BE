package org.example.board_game.core.client.service.impl.order;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.client.domain.dto.request.order.ClientCartItemRequest;
import org.example.board_game.core.client.domain.dto.request.order.ClientOrderRequest;
import org.example.board_game.core.client.domain.dto.response.customer.ClientAddressResponse;
import org.example.board_game.core.client.domain.dto.response.customer.ClientCustomerResponse;
import org.example.board_game.core.client.domain.dto.response.order.*;
import org.example.board_game.core.client.domain.dto.response.product.ClientProductResponse;
import org.example.board_game.core.client.domain.dto.response.voucher.ClientVoucherResponse;
import org.example.board_game.core.client.domain.mapper.customer.ClientAddressMapper;
import org.example.board_game.core.client.domain.mapper.customer.ClientCustomerMapper;
import org.example.board_game.core.client.domain.mapper.order.ClientOrderMapper;
import org.example.board_game.core.client.domain.mapper.product.ClientProductMapper;
import org.example.board_game.core.client.domain.mapper.voucher.ClientVoucherMapper;
import org.example.board_game.core.client.service.order.ClientOrderService;
import org.example.board_game.core.client.service.order.VNPayService;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.customer.Address;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.entity.order.Order;
import org.example.board_game.entity.order.OrderDetail;
import org.example.board_game.entity.payment.Payment;
import org.example.board_game.entity.product.Product;
import org.example.board_game.entity.voucher.Voucher;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.constants.MessageConstant;
import org.example.board_game.infrastructure.enums.*;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.repository.customer.AddressRepository;
import org.example.board_game.repository.order.OrderDetailRepository;
import org.example.board_game.repository.order.OrderRepository;
import org.example.board_game.repository.order.PaymentRepository;
import org.example.board_game.repository.product.ProductRepository;
import org.example.board_game.repository.voucher.VoucherRepository;
import org.example.board_game.utils.CollectionUtils;
import org.example.board_game.utils.Response;
import org.example.board_game.utils.StrUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClientOrderServiceImpl implements ClientOrderService {

    OrderDetailRepository orderDetailRepository;
    OrderRepository orderRepository;
    ProductRepository productRepository;
    VoucherRepository voucherRepository;
    AddressRepository addressRepository;
    PaymentRepository paymentRepository;
    VNPayService vnPayService;
    EntityService entityService;

    ClientOrderMapper orderMapper = ClientOrderMapper.INSTANCE;
    ClientAddressMapper addressMapper = ClientAddressMapper.INSTANCE;
    ClientVoucherMapper voucherMapper = ClientVoucherMapper.INSTANCE;
    ClientCustomerMapper customerMapper = ClientCustomerMapper.INSTANCE;
    ClientProductMapper productMapper = ClientProductMapper.INSTANCE;

    @Transactional
    @Override
    public Response<Object> createOrder(ClientOrderRequest request) {

        List<ClientCartItemRequest> cartItems = request.getCartItems();
        if (CollectionUtils.isListEmpty(cartItems)) {
            throw new ResourceNotFoundException("Vui lòng chọn sản phẩm để thanh toán.");
        }
        Order order = orderMapper.toEntity(request);
        order = orderRepository.save(order);

        List<String> productIds = CollectionUtils.extractField(cartItems, ClientCartItemRequest::getProductId);
        List<Product> products = productRepository.findAllByIds(productIds);
        Map<String, Product> productMap = CollectionUtils.collectToMap(products, Product::getId);

        List<Product> productAfterUpdate = new ArrayList<>();
        List<OrderDetail> orderDetails = new ArrayList<>();
        float totalPrice = 0.0f;

        for (ClientCartItemRequest item : cartItems) {
            String productId = item.getProductId();
            Product product = productMap.get(productId);
            if (product == null) {
                throw new ApiException("Một số sản phẩm không tồn tại.");
            }
            if (product.getQuantity() < item.getQuantity()) {
                throw new ApiException("Số lượng tồn kho của sản phẩm: " + product.getName() + " không đủ.");
            }
            OrderDetail orderDetail = new OrderDetail();
            float price = product.getPrice();
            totalPrice += price * item.getQuantity();

            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setPrice(price);
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setTotalPrice(price * item.getQuantity());

            product.setQuantity(product.getQuantity() - item.getQuantity());
            productAfterUpdate.add(product);
            orderDetails.add(orderDetail);
        }
        Address address = addressMapper.toEntity(request.getAddress());
        address = addressRepository.save(address);
        OrderStatus status = request.getPaymentMethod() == PaymentMethod.CASH ? OrderStatus.WAIT_FOR_CONFIRMATION : OrderStatus.PENDING;

        order.setType(OrderType.ONLINE);
        order.setStatus(status);
        order.setCustomer(entityService.getCustomerByAuth());
        order.setOriginMoney(totalPrice);
        applyVoucherToOrder(order, request.getVoucherId(), totalPrice, "add");
        order.setTotalMoney(order.getTotalMoney() + order.getShippingMoney());
        order.setAddress(address);
        order.setExpectedDeliveryDate(new Date().getTime() + EntityProperties.DELIVERY_TIME_IN_MILLIS);

        Order orderSaved = orderRepository.save(order);
        orderDetailRepository.saveAll(orderDetails);
        productRepository.saveAll(productAfterUpdate);

        if (request.getPaymentMethod() == PaymentMethod.TRANSFER) {
            entityService.createOrderHistory(orderSaved, OrderStatus.PENDING);
            Response<ClientUrlResponse> urlVnp = vnPayService.createOrder(orderSaved.getTotalMoney(), orderSaved.getId());
            ClientUrlResponse url = urlVnp.getData();
            return Response.of((Object) url).success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
        }
        entityService.createOrderHistory(orderSaved, OrderStatus.WAIT_FOR_CONFIRMATION);
        if (orderSaved.getPayment() == null) {
            createPaymentByCash(order);
        }
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Transactional
    @Override
    public Response<Object> updateOrder(String id, ClientOrderRequest request) {

        Order order = entityService.getOrder(id);
        OrderStatus status = order.getStatus();

        if (status != OrderStatus.WAIT_FOR_DELIVERY && status != OrderStatus.WAIT_FOR_CONFIRMATION) {
            throw new ApiException("Không thể hủy đơn hàng khi " + status.name());
        }
        List<ClientCartItemRequest> cartItems = request.getCartItems();
        if (CollectionUtils.isListEmpty(cartItems)) {
            throw new ResourceNotFoundException("Vui lòng chọn sản phẩm để thanh toán.");
        }
        Address address = order.getAddress();
        addressMapper.updateAddress(request.getAddress(), address);

        List<OrderDetail> orderDetailsUpdate = new ArrayList<>();
        List<OrderDetail> orderDetailsRemove = new ArrayList<>();
        List<Product> productNews = new ArrayList<>();

        List<String> productIds = CollectionUtils.extractField(cartItems, ClientCartItemRequest::getProductId);
        List<Product> products = productRepository.findAllByIds(productIds);
        Map<String, Product> productMap = CollectionUtils.collectToMap(products, Product::getId);

        List<OrderDetail> existingOrderDetails = order.getOrderDetails();
        Map<String, OrderDetail> existingOrderDetailMap = CollectionUtils.collectToMap(existingOrderDetails, item -> item.getProduct().getId());
        float totalPrice = 0f;
        for (ClientCartItemRequest item : cartItems) {

            String productId = item.getProductId();
            int newQuantity = item.getQuantity();
            if (productId == null || newQuantity <= 0) continue;

            Product product = productMap.get(productId);
            if (product == null) {
                throw new ResourceNotFoundException(MessageConstant.PRODUCT_NOT_FOUND);
            }
            OrderDetail orderDetail;
            if (existingOrderDetailMap.containsKey(productId)) {

                orderDetail = existingOrderDetailMap.get(productId);
                int quantityChange = newQuantity - orderDetail.getQuantity();

                if (product.getQuantity() < quantityChange) {
                    throw new ApiException("Số lượng tồn sản phẩm: " + product.getName() + " trong kho không đủ.");
                }
                product.setQuantity(product.getQuantity() - quantityChange);
            } else {
                if (product.getQuantity() < newQuantity) {
                    throw new ApiException("Số lượng tồn sản phẩm: " + product.getName() + " trong kho không đủ.");
                }
                orderDetail = new OrderDetail();
                orderDetail.setOrder(order);
                orderDetail.setProduct(product);
                orderDetail.setPrice(product.getPrice());
                product.setQuantity(product.getQuantity() - newQuantity);
            }
            orderDetail.setTotalPrice(newQuantity * orderDetail.getPrice());
            orderDetail.setQuantity(newQuantity);
            orderDetailsUpdate.add(orderDetail);
            productNews.add(product);
            totalPrice += product.getPrice() * item.getQuantity();
        }

        for (OrderDetail existingOrderDetail : existingOrderDetails) {
            String productId = existingOrderDetail.getProduct().getId();
            if (productIds.contains(productId)) {
                continue;
            }
            Product product = existingOrderDetail.getProduct();
            product.setQuantity(product.getQuantity() + existingOrderDetail.getQuantity());
            productNews.add(product);
            orderDetailsRemove.add(existingOrderDetail);
        }
        addressRepository.save(address);
        orderDetailRepository.saveAll(orderDetailsUpdate);
        orderDetailRepository.deleteAll(orderDetailsRemove);
        productRepository.saveAll(productNews);

        orderMapper.updateOrder(request, order);
        order.setOriginMoney(totalPrice);
        applyVoucherToOrder(order, request.getVoucherId(), totalPrice, "update");
        order.setTotalMoney(order.getTotalMoney() + request.getShippingMoney());

        Payment payment = order.getPayment();
        payment.setTotalMoney(order.getTotalMoney());
        paymentRepository.save(payment);
        orderRepository.save(order);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Transactional
    @Override
    public Response<ClientOrderResponse> findByCode(String code) {

        Order order = orderRepository
                .findByCodeAndDeletedFalse(code)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.ORDER_NOT_FOUND));
        ClientOrderResponse response = orderMapper.toResponse(order);
        convertOrderToResponse(order, response);
        return Response.of(response).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Transactional
    @Override
    public Response<ClientOrderResponse> findByIdAndCustomerId(String orderId) {

        Customer customer = entityService.getCustomerByAuth();
        Order order;
        if (customer == null) {
            order = entityService.getOrder(orderId);
        } else {
            order = orderRepository
                    .findByIdAndCustomer_Id(orderId, customer.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(MessageConstant.ORDER_NOT_FOUND));
        }
        ClientOrderResponse response = orderMapper.toResponse(order);
        convertOrderToResponse(order, response);
        return Response.of(response).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Transactional
    @Override
    public Response<Object> cancelOrder(String orderId) {

        Order order = entityService.getOrder(orderId);
        OrderStatus status = order.getStatus();
        if (status != OrderStatus.WAIT_FOR_DELIVERY && status != OrderStatus.WAIT_FOR_CONFIRMATION) {
            throw new ApiException("Không thể hủy đơn hàng khi " + status.name());
        }
        if (order.getVoucher() != null) {
            Voucher voucher = order.getVoucher();
            voucher.setQuantity(voucher.getQuantity() + 1);
            voucherRepository.save(voucher);
        }
        entityService.revertQuantityProductWhenCancelOrder(order);
        order.setStatus(OrderStatus.CANCELED);
        entityService.createOrderHistory(order, OrderStatus.CANCELED);
        orderRepository.save(order);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
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

    private void createPaymentByCash(Order order) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setTotalMoney(order.getTotalMoney());
        payment.setTransactionCode(null);
        payment.setMethod(PaymentMethod.CASH);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setDescription(order.getNote());
        paymentRepository.save(payment);
    }

    private void convertOrderToResponse(Order order, ClientOrderResponse response) {

        if (order.getCustomer() != null) {
            ClientCustomerResponse customer = customerMapper.toResponse(order.getCustomer());
            response.setCustomer(customer);
        }
        if (order.getPayment() != null) {
            ClientPaymentResponse payment = orderMapper.toPaymentRes(order.getPayment());
            response.setPayment(payment);
        }
        if (order.getAddress() != null) {
            ClientAddressResponse address = addressMapper.toResponse(order.getAddress());
            response.setAddress(address);
        }
        if (order.getVoucher() != null) {
            ClientVoucherResponse voucher = voucherMapper.toResponse(order.getVoucher());
            response.setVoucher(voucher);
        }
        if (!CollectionUtils.isListEmpty(order.getOrderDetails())) {
            List<ClientOrderDetailResponse> orderDetailsRes = new ArrayList<>();
            order.getOrderDetails().forEach(orderDetail -> {
                ClientOrderDetailResponse orderDetailResponse = orderMapper.toOrderDetailRes(orderDetail);
                ClientProductResponse product = productMapper.toResponse(orderDetail.getProduct());
                orderDetailResponse.setProduct(product);
                orderDetailsRes.add(orderDetailResponse);

            });
            response.setOrderDetails(orderDetailsRes);
        }
        if (!CollectionUtils.isListEmpty(order.getOrderHistories())) {
            List<ClientOrderHistoryResponse> orderHistoryResponses = orderMapper.toOrderHistoriesRes(order.getOrderHistories());
            response.setOrderHistories(orderHistoryResponses);
        }
    }

}
