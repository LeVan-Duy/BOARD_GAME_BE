package org.example.board_game.core.admin.service.impl.order;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.order.AdminOrderDetailRequest;
import org.example.board_game.core.admin.service.order.AdminOrderDetailService;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.employee.Employee;
import org.example.board_game.entity.order.Order;
import org.example.board_game.entity.order.OrderDetail;
import org.example.board_game.entity.product.Product;
import org.example.board_game.entity.voucher.Voucher;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.constants.MessageConstant;
import org.example.board_game.infrastructure.enums.ProductStatus;
import org.example.board_game.infrastructure.enums.VoucherType;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.infrastructure.exception.UnauthorizedException;
import org.example.board_game.repository.order.OrderDetailRepository;
import org.example.board_game.repository.order.OrderRepository;
import org.example.board_game.repository.product.ProductRepository;
import org.example.board_game.utils.Response;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminOrderDetailServiceImpl implements AdminOrderDetailService {

    OrderDetailRepository orderDetailRepository;
    EntityService entityService;
    ProductRepository productRepository;
    OrderRepository orderRepository;
    AdminOrderServiceImpl adminOrderServiceImpl;

    @Transactional
    @Override
    public Response<Object> addProductToOrder(AdminOrderDetailRequest request) {

        Employee employee = entityService.getEmployeeByAuth();
        if (employee == null) {
            throw new UnauthorizedException(MessageConstant.UNAUTHORIZED);
        }
        Order order = entityService.getOrder(request.getOrderId());

        Product product = entityService.getProduct(request.getProductId());
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new ApiException("Sản phẩm này hiện tại không còn hoạt động.");
        }
        int quantityProduct = product.getQuantity();
        if (quantityProduct == 0) {
            throw new ApiException("Sản phẩm này đã hết hàng.");
        }
        if (request.getQuantity() > quantityProduct) {
            throw new ApiException("Số lượng tồn kho sản phẩm này không đủ.");
        }
        product.setQuantity(quantityProduct - request.getQuantity());
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setPrice(product.getPrice());
        orderDetail.setProduct(product);
        orderDetail.setQuantity(request.getQuantity());
        orderDetail.setTotalPrice(product.getPrice() * request.getQuantity());

        productRepository.save(product);
        orderDetailRepository.save(orderDetail);

        //update total money by voucher
        List<OrderDetail> orderDetails = order.getOrderDetails();
        float totalOriginPriceInOrder = entityService.totalMoneyOrderDetail(orderDetails);

        if (order.getVoucher() == null) {
            order.setReduceMoney(null);
            order.setTotalMoney(totalOriginPriceInOrder);
            order.setOriginMoney(totalOriginPriceInOrder);
        }else {
            Voucher voucher = order.getVoucher();
            float discount = voucher.getType() == VoucherType.CASH ? voucher.getValue() : (voucher.getValue() / 100) * totalOriginPriceInOrder;
            float finalTotalPrice = Math.max(0, totalOriginPriceInOrder - discount);
            order.setReduceMoney(discount);
            order.setTotalMoney(finalTotalPrice);
            order.setOriginMoney(totalOriginPriceInOrder);
        }
        orderRepository.save(order);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }
}
