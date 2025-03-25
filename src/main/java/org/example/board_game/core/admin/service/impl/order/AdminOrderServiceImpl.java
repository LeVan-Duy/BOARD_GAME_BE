package org.example.board_game.core.admin.service.impl.order;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.order.AdminCheckoutRequest;
import org.example.board_game.core.admin.domain.dto.request.order.AdminOrderRequest;
import org.example.board_game.core.admin.service.order.AdminOrderService;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.entity.employee.Employee;
import org.example.board_game.entity.order.Order;
import org.example.board_game.entity.order.OrderDetail;
import org.example.board_game.entity.order.OrderHistory;
import org.example.board_game.entity.voucher.Voucher;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.constants.MessageConstant;
import org.example.board_game.infrastructure.enums.OrderStatus;
import org.example.board_game.infrastructure.enums.OrderType;
import org.example.board_game.infrastructure.enums.VoucherStatus;
import org.example.board_game.infrastructure.enums.VoucherType;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.infrastructure.exception.UnauthorizedException;
import org.example.board_game.repository.order.OrderHistoryRepository;
import org.example.board_game.repository.order.OrderRepository;
import org.example.board_game.repository.voucher.VoucherRepository;
import org.example.board_game.utils.CollectionUtils;
import org.example.board_game.utils.Response;
import org.example.board_game.utils.StrUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    OrderRepository orderRepository;
    VoucherRepository voucherRepository;
    EntityService entityService;
    OrderHistoryRepository orderHistoryRepository;

    @Transactional
    @Override
    public Response<Object> create() {
        Employee employee = entityService.getEmployeeByAuth();
        if (employee == null) {
            throw new UnauthorizedException(MessageConstant.UNAUTHORIZED);
        }
        Integer countOrderPending = orderRepository.countAllByStatusAndTypeAndDeletedFalse(OrderStatus.PENDING, OrderType.OFFLINE);
        if (countOrderPending >= EntityProperties.MAX_ORDER_PENDING) {
            throw new ApiException("Chỉ được tạo tối đa 5 đơn hàng chưa thanh toán.");
        }
        Order orderPending = new Order();
        orderPending.setType(OrderType.OFFLINE);
        orderPending.setStatus(OrderStatus.PENDING);
        orderPending.setEmployee(employee);
        Order orderResult = orderRepository.save(orderPending);
        OrderHistory orderHistory = createOrderHistory(orderResult, OrderStatus.PENDING);
        orderHistoryRepository.save(orderHistory);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Transactional
    @Override
    public Response<Object> checkoutAtStore(AdminCheckoutRequest request) {

        Employee employee = entityService.getEmployeeByAuth();
        if (employee == null) {
            throw new UnauthorizedException(MessageConstant.UNAUTHORIZED);
        }

        // không cần apply voucher => làm thằng vào đây, fe sẽ tự chọn rồi view lên màn hình tính giá tiền
        // address sẽ mặc nếu chọn khách hàng sẽ fill mặc định lên input select địa chỉ và có thể thay đổi trên input select đó sau đó sẽ gửi xuống BE lưu vào bảng địa chỉ dành cho order
        // không cần apply note
        // hỏi đạt cao xem chỗ này ko cần apply thì khi bị out vào lại lưu local có bị mất ko

        return null;
    }


    @Transactional
    @Override
    public Response<Object> applyCustomerToOrder(AdminOrderRequest request) {

        Employee employee = entityService.getEmployeeByAuth();
        if (employee == null) {
            throw new UnauthorizedException(MessageConstant.UNAUTHORIZED);
        }
        Order order = entityService.getOrder(request.getOrderId());
        String customerId = request.getCustomerId();
        if (StrUtils.isBlank(customerId)) {
            order.setCustomer(null);
        } else {
            Customer customer = entityService.getCustomer(customerId);
            order.setCustomer(customer);
        }
        orderRepository.save(order);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    @Transactional
    public Response<Object> applyVoucherToOrder(AdminOrderRequest request) {

        Employee employee = entityService.getEmployeeByAuth();
        if (employee == null) {
            throw new UnauthorizedException(MessageConstant.UNAUTHORIZED);
        }
        Order order = entityService.getOrder(request.getOrderId());
        String voucherId = request.getVoucherId();
        applyVoucherToOrderAtStore(order, voucherId);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> applyNoteToOrder(AdminOrderRequest request) {
        Employee employee = entityService.getEmployeeByAuth();
        if (employee == null) {
            throw new UnauthorizedException(MessageConstant.UNAUTHORIZED);
        }
        Order order = entityService.getOrder(request.getOrderId());
        order.setNote(request.getNote());
        orderRepository.save(order);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }


    private OrderHistory createOrderHistory(Order order, OrderStatus orderStatus) {
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setOrder(order);
        orderHistory.setActionStatus(orderStatus);
        orderHistory.setNote(orderStatus.name);
        return orderHistory;
    }


    public void applyVoucherToOrderAtStore(Order order, String voucherId) {

        List<OrderDetail> orderDetails = order.getOrderDetails();
        float totalOriginPriceInOrder = entityService.totalMoneyOrderDetail(orderDetails);

        if (StrUtils.isNotBlank(voucherId)) {

            Voucher oldVoucher = order.getVoucher();
            if (oldVoucher != null) {
                if (oldVoucher.getId().equals(voucherId)) {
                    return;
                }
            }
            if (CollectionUtils.isListEmpty(orderDetails)) {
                throw new ResourceNotFoundException("Vui lòng thêm sản phẩm trước khi áp dụng giảm giá.");
            }
            Voucher voucher = entityService.getVoucher(voucherId);

            if (voucher.getStatus() != VoucherStatus.ACTIVE) {
                throw new ApiException("Giảm giá này không hoạt động.");
            }
            if (voucher.getConstraint() > totalOriginPriceInOrder) {
                throw new ApiException("Đơn hàng của bạn không đủ điều kiện để áp dụng giảm giá này.");
            }
            if (voucher.getQuantity() < 1) {
                throw new ApiException("Giảm giá này đã hết số lượng.");
            }
            long currentDate = new Date().getTime();
            if (currentDate > voucher.getEndDate()) {
                throw new ApiException("Giảm giá này đã hết hạn.");
            }
            if (currentDate < voucher.getStartDate()) {
                throw new ApiException("Giảm giá này chưa bắt đầu.");
            }

            if (oldVoucher != null) {
                oldVoucher.setQuantity(oldVoucher.getQuantity() + 1);
                voucherRepository.save(oldVoucher);
            }
            voucher.setQuantity(voucher.getQuantity() - 1);
            order.setVoucher(voucher);

            float discount = voucher.getType() == VoucherType.CASH ? voucher.getValue() : (voucher.getValue() / 100) * totalOriginPriceInOrder;
            float finalTotalPrice = Math.max(0, totalOriginPriceInOrder - discount);
            order.setReduceMoney(discount);
            order.setOriginMoney(totalOriginPriceInOrder);
            order.setTotalMoney(finalTotalPrice);
            voucherRepository.save(voucher);

        } else {
            order.setReduceMoney(null);
            order.setTotalMoney(totalOriginPriceInOrder);
            order.setOriginMoney(totalOriginPriceInOrder);
            Voucher oldVoucher = order.getVoucher();
            if (oldVoucher != null) {
                oldVoucher.setQuantity(oldVoucher.getQuantity() + 1);
                voucherRepository.save(oldVoucher);
            }
            order.setVoucher(null);
        }
        orderRepository.save(order);
    }
}
