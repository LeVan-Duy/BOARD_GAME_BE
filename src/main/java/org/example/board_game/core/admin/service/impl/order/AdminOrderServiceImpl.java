package org.example.board_game.core.admin.service.impl.order;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.order.AdminCheckoutRequest;
import org.example.board_game.core.admin.domain.dto.request.order.AdminOrderRequest;
import org.example.board_game.core.admin.domain.dto.response.customer.AdminCustomerResponse;
import org.example.board_game.core.admin.domain.dto.response.order.AdminOrderResponse;
import org.example.board_game.core.admin.domain.mapper.order.AdminOrderMapper;
import org.example.board_game.core.admin.service.order.AdminOrderService;
import org.example.board_game.core.client.domain.dto.response.order.ClientOrderDetailResponse;
import org.example.board_game.core.client.domain.dto.response.order.ClientOrderHistoryResponse;
import org.example.board_game.core.client.domain.dto.response.order.ClientPaymentResponse;
import org.example.board_game.core.client.domain.dto.response.voucher.ClientVoucherResponse;
import org.example.board_game.core.client.domain.mapper.order.ClientOrderMapper;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.core.common.base.BaseResponse;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.core.common.dto.AddressResponse;
import org.example.board_game.core.common.dto.ProductResponse;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.entity.employee.Employee;
import org.example.board_game.entity.order.Order;
import org.example.board_game.entity.order.OrderDetail;
import org.example.board_game.entity.order.OrderHistory;
import org.example.board_game.entity.payment.Payment;
import org.example.board_game.entity.product.Product;
import org.example.board_game.entity.product.ProductMedia;
import org.example.board_game.entity.voucher.Voucher;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.constants.MessageConstant;
import org.example.board_game.infrastructure.enums.*;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.infrastructure.exception.UnauthorizedException;
import org.example.board_game.repository.order.OrderHistoryRepository;
import org.example.board_game.repository.order.OrderRepository;
import org.example.board_game.repository.order.PaymentRepository;
import org.example.board_game.repository.voucher.VoucherRepository;
import org.example.board_game.utils.CollectionUtils;
import org.example.board_game.utils.PaginationUtil;
import org.example.board_game.utils.Response;
import org.example.board_game.utils.StrUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    OrderRepository orderRepository;
    VoucherRepository voucherRepository;
    EntityService entityService;
    AdminOrderMapper orderMapper = AdminOrderMapper.INSTANCE;
    PaymentRepository paymentRepository;

    @Transactional
    @Override
    public Response<PageableObject<AdminOrderResponse>> getAll(AdminOrderRequest request) {

        String employeeId = null;
        Employee employee = entityService.getEmployeeByAuth();
        if (employee == null) {
            throw new UnauthorizedException(MessageConstant.UNAUTHORIZED);
        }
        if (employee.getRole() == Role.EMPLOYEE) {
            employeeId = employee.getId();
        }
        Pageable pageable = PaginationUtil.pageable(request);
        Page<Order> orderPage = orderRepository.findAllOrder(request, request.getStatus(), request.getType(), employeeId, pageable);

        List<AdminOrderResponse> responses = orderPage.getContent().stream().map(order -> {
            AdminOrderResponse response = orderMapper.toRes(order);
            convertOrderToRes(order, response);
            return response;
        }).toList();
        Page<AdminOrderResponse> resPage = new PageImpl<>(responses, pageable, orderPage.getTotalElements());
        PageableObject<AdminOrderResponse> pageableObject = new PageableObject<>(resPage);
        return Response.of(pageableObject).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Transactional
    @Override
    public Response<Object> confirm(String id, AdminOrderRequest request) {

        Employee employee = entityService.getEmployeeByAuth();
        if (employee == null) {
            throw new UnauthorizedException(MessageConstant.UNAUTHORIZED);
        }
        Order order = entityService.getOrder(id);

        if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.PENDING) {
            throw new ApiException("Không thể cập nhật đơn hàng đã hoàn thành hoặc đã hủy hoặc đang xử lí.");
        }
        OrderStatus status = request.getStatus();
        if (status == null) {
            throw new ResourceNotFoundException("Vui lòng chọn trạng thái xác nhận đơn hàng.");
        }
        order.setEmployee(employee);
        order.setStatus(status);
        orderRepository.save(order);
        if (status == OrderStatus.CANCELED) {
            if (StrUtils.isBlank(request.getNote())) {
                throw new ApiException("Vui lòng nhập lí do từ chối.");
            }
            entityService.createOrderHistory(order, status, request.getNote());
        } else {
            entityService.createOrderHistory(order, status);
        }

        if (status == OrderStatus.CANCELED) {
            entityService.revertQuantityProductWhenCancelOrder(order);
            Voucher voucher = order.getVoucher();
            if (voucher != null) {
                voucher.setQuantity(voucher.getQuantity() + 1);
                voucherRepository.save(voucher);
            }
            Payment payment = order.getPayment();
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTotalMoney(0);
            payment.setDescription("Đơn hàng đã được hủy.");
            payment.setTransactionCode("CANCEL");
            paymentRepository.save(payment);
        }
        if (status == OrderStatus.COMPLETED) {
            Payment payment = order.getPayment();
            if (payment.getStatus() != PaymentStatus.COMPLETED) {
                payment.setStatus(PaymentStatus.COMPLETED);
                paymentRepository.save(payment);
            }
        }
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

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
        entityService.createOrderHistory(orderResult, OrderStatus.PENDING);
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

    private void convertOrderToRes(Order order, AdminOrderResponse response) {

        if (order.getCustomer() != null) {
            AdminCustomerResponse customerRes = orderMapper.toCustomerRes(order.getCustomer());
            response.setCustomerRes(customerRes);
        }
        if (order.getAddress() != null) {
            AddressResponse address = orderMapper.toAddressRes(order.getAddress());
            response.setAddress(address);
        }
        if (order.getVoucher() != null) {
            BaseResponse voucher = new BaseResponse();
            voucher.setId(order.getVoucher().getId());
            voucher.setName(order.getVoucher().getName());
            response.setVoucher(voucher);
        }
        if (order.getPayment() != null) {
            ClientPaymentResponse payment = orderMapper.toPaymentRes(order.getPayment());
            response.setPayment(payment);
        }

        List<ClientOrderDetailResponse> orderDetailsRes = new ArrayList<>();
        order.getOrderDetails().forEach(orderDetail -> {
            ClientOrderDetailResponse orderDetailResponse = orderMapper.toOrderDetailRes(orderDetail);
            Product product = orderDetail.getProduct();
            ProductResponse productRes = orderMapper.toProductRes(product);

            ProductMedia productMedia = product.getProductMediaList()
                    .stream()
                    .filter(ProductMedia::isMainImg)
                    .findFirst()
                    .orElse(null);

            if (productMedia != null) {
                BaseResponse imgRes = entityService.baseResponse(productMedia.getId(), productMedia.getUrl());
                productRes.setImage(imgRes);
            }
            orderDetailResponse.setProduct(productRes);
            orderDetailsRes.add(orderDetailResponse);
        });
        response.setOrderDetails(orderDetailsRes);

        List<ClientOrderHistoryResponse> orderHistoryResponses = orderMapper.toOrderHistoriesRes(order.getOrderHistories());
        response.setOrderHistories(orderHistoryResponses);
    }
}
