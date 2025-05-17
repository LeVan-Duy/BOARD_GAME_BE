package org.example.board_game.core.client.service.impl.order;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.email.EmailRequest;
import org.example.board_game.core.admin.service.email.EmailService;
import org.example.board_game.core.client.domain.dto.request.order.ClientCartItemRequest;
import org.example.board_game.core.client.domain.dto.request.order.ClientOrderRequest;
import org.example.board_game.core.client.domain.dto.response.order.*;
import org.example.board_game.core.client.domain.mapper.customer.ClientAddressMapper;
import org.example.board_game.core.client.domain.mapper.order.ClientOrderMapper;
import org.example.board_game.core.client.domain.mapper.product.ClientProductMapper;
import org.example.board_game.core.client.service.order.ClientOrderService;
import org.example.board_game.core.client.service.order.VNPayService;
import org.example.board_game.core.common.base.BaseResponse;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.core.common.dto.AddressResponse;
import org.example.board_game.core.common.dto.ProductResponse;
import org.example.board_game.entity.customer.Address;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.entity.order.Order;
import org.example.board_game.entity.order.OrderDetail;
import org.example.board_game.entity.payment.Payment;
import org.example.board_game.entity.product.Product;
import org.example.board_game.entity.product.ProductMedia;
import org.example.board_game.entity.voucher.Voucher;
import org.example.board_game.infrastructure.constants.ApiProperties;
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
import org.example.board_game.utils.ConvertUtil;
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
    EmailService emailService;

    ClientOrderMapper orderMapper = ClientOrderMapper.INSTANCE;
    ClientAddressMapper addressMapper = ClientAddressMapper.INSTANCE;
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
        templateSendMailOrder(orderSaved, orderDetails);
        if (request.getPaymentMethod() == PaymentMethod.TRANSFER) {
            entityService.createOrderHistory(orderSaved, OrderStatus.PENDING);
            Response<ClientUrlResponse> urlVnp = vnPayService.createOrder(orderSaved.getTotalMoney(), orderSaved.getId(), 14);
            ClientUrlResponse url = urlVnp.getData();
            return Response.of((Object) url).success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
        }
        entityService.createOrderHistory(orderSaved, OrderStatus.WAIT_FOR_CONFIRMATION);
        if (orderSaved.getPayment() == null) {
            createPaymentByCash(order);
        }
        ClientOrderReturnResponse response = new ClientOrderReturnResponse();
        response.setOrderId(orderSaved.getId());
        response.setMessage("Thành công!");
        return Response.of((Object) response).success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Transactional
    @Override
    public Response<Object> updateOrder(String id, ClientOrderRequest request) {

        Order order = entityService.getOrder(id);
        OrderStatus status = order.getStatus();

        if (status != OrderStatus.WAIT_FOR_CONFIRMATION) {
            throw new ApiException("Không thể hiểu chỉnh đơn hàng khi ở trạng thái: " + status.name());
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
        if (order.getStatus() == OrderStatus.PENDING) {
            Long now = new Date().getTime();
            Long createdTime = order.getCreatedAt();
            response.setUrlRepayment(getUrlRepayment(createdTime, now, order));
        }
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
        if (order.getStatus() == OrderStatus.PENDING) {
            Long now = new Date().getTime();
            Long createdTime = order.getCreatedAt();
            response.setUrlRepayment(getUrlRepayment(createdTime, now, order));
        }
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
                validTimeVoucher(voucher);
                updateQuantityVoucher(voucher, voucher.getQuantity() - 1);
            } else {
                if (order.getVoucher() != null) {
                    if (!voucherId.equals(order.getVoucher().getId())) {
                        validTimeVoucher(voucher);
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

    private void validTimeVoucher(Voucher voucher) {

        long currentDate = new Date().getTime();
        if (currentDate > voucher.getEndDate()) {
            throw new ApiException("Voucher này đã hết hạn.");
        }
        if (currentDate < voucher.getStartDate()) {
            throw new ApiException("Voucher này chưa diễn ra.");
        }
        if (voucher.getQuantity() < 1) {
            throw new ApiException("Voucher này hiện tại đã hết.");
        }
    }

    public String getUrlRepayment(Long orderCreatedTime, Long now, Order order) {
        long expiredAfterMillis = 14 * 60 * 1000;
        long timePassed = now - orderCreatedTime;
        long remainingMillis = expiredAfterMillis - timePassed;
        int result = (int)(Math.max(remainingMillis / (60 * 1000), 0));
        Response<ClientUrlResponse> urlVnp = vnPayService.createOrder(order.getTotalMoney(), order.getId(), result);
        return urlVnp.getData().getUrl();
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

        if (order.getPayment() != null) {
            ClientPaymentResponse payment = orderMapper.toPaymentRes(order.getPayment());
            response.setPayment(payment);
        }
        if (order.getAddress() != null) {
            AddressResponse address = addressMapper.toAddressResponse(order.getAddress());
            response.setAddress(address);
        }
        if (order.getVoucher() != null) {
            BaseResponse voucher = entityService.baseResponse(order.getVoucher().getId(), order.getVoucher().getName());
            response.setVoucher(voucher);
        }
        if (!CollectionUtils.isListEmpty(order.getOrderDetails())) {
            List<ClientOrderDetailResponse> orderDetailsRes = new ArrayList<>();
            order.getOrderDetails().forEach(orderDetail -> {
                ClientOrderDetailResponse orderDetailResponse = orderMapper.toOrderDetailRes(orderDetail);
                Product product = orderDetail.getProduct();
                ProductResponse productRes = productMapper.toProductRes(orderDetail.getProduct());

                Optional<ProductMedia> optionalMedia = product.getProductMediaList()
                        .stream()
                        .filter(ProductMedia::isMainImg)
                        .findFirst();

                optionalMedia.ifPresent(media -> {
                    BaseResponse mediaRes = entityService.baseResponse(media.getId(), media.getUrl());
                    productRes.setImage(mediaRes);
                });
                orderDetailResponse.setProduct(productRes);
                orderDetailsRes.add(orderDetailResponse);

            });
            response.setOrderDetails(orderDetailsRes);
        }
        if (!CollectionUtils.isListEmpty(order.getOrderHistories())) {
            List<ClientOrderHistoryResponse> orderHistoryResponses = orderMapper.toOrderHistoriesRes(order.getOrderHistories());
            response.setOrderHistories(orderHistoryResponses);
        }
    }

    private void templateSendMailOrder(Order order, List<OrderDetail> orderDetails) {
        String[] toEmail = new String[1];
        EmailRequest email = new EmailRequest();
        email.setSubject("Thông tin đơn hàng của bạn từ BOARD_GAME");
        String emailTitle = "<table style='width: 100%; border-collapse: collapse; font-family: Arial, sans-serif;'>";
        emailTitle += "<tr><td colspan='4' style='text-align: center;'><strong>Thông tin đơn hàng</strong></td></tr>";
        emailTitle += "<tr><td colspan='4'>&nbsp;</td></tr>";
        email.setTitleEmail(emailTitle);
        StringBuilder emailBody = new StringBuilder("<table style='width: 100%; border-collapse: collapse; font-family: Arial, sans-serif;'>");
        emailBody.append("<tr><td colspan='4'>&nbsp;</td></tr>");
        emailBody.append("<tr><td colspan='4'>");
        emailBody.append("<table style='width: 100%; border-collapse: collapse; font-family: Arial, sans-serif;'>");
        emailBody.append("<thead style='background-color: #f0f0f0;'>");
        emailBody.append("<tr>");
        emailBody.append("<th style='border: 1px solid #ddd; padding: 8px;'>Tên sản phẩm</th>");
        emailBody.append("<th style='border: 1px solid #ddd; padding: 8px;'>Số lượng</th>");
        emailBody.append("<th style='border: 1px solid #ddd; padding: 8px;'>Giá sản phẩm</th>");
        emailBody.append("<th style='border: 1px solid #ddd; padding: 8px;'>Tổng tiền</th>");
        emailBody.append("</tr>");
        emailBody.append("</thead>");
        emailBody.append("<tbody>");

        float totalOrderDetail = 0.0f;
        if (orderDetails != null && !orderDetails.isEmpty()) {
            for (OrderDetail orderDetail : orderDetails) {
                Product product = orderDetail.getProduct();
                if (product != null) {
                    float totalPrice = orderDetail.getQuantity() * product.getPrice();
                    totalOrderDetail += totalPrice;
                    emailBody.append("<tr>");
                    emailBody.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(product.getName()).append("</td>");
                    emailBody.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(orderDetail.getQuantity()).append("</td>");
                    emailBody.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(ConvertUtil.convertFloatToVnd(product.getPrice())).append("</td>");
                    emailBody.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(ConvertUtil.convertFloatToVnd(totalPrice)).append("</td>");
                    emailBody.append("</tr>");
                }
            }
        }
        float totalVoucher = 0.0f;
        emailBody.append("</tbody>");
        emailBody.append("</table>");
        emailBody.append("</td></tr>");
        emailBody.append("<tr><td colspan='4'>&nbsp;</td></tr>");
        emailBody.append("<tr><td colspan='4'><strong>Họ và tên:</strong> ").append(order.getFullName()).append("</td></tr>");
        emailBody.append("<tr><td colspan='4'><span style='font-size: 12px;margin-right:10px;'>Bấm vào đây để theo dõi đơn hàng của bạn:</span><a href='" + ApiProperties.URL_FE_TRACKING+order.getId()).append("' style='background-color: #4CAF50; color: white; padding: 5px 10px; text-decoration: none; border-radius: 3px; font-size: 12px;'>Theo dõi</a></td></tr>");
        emailBody.append("<tr><td colspan='4'>&nbsp;</td></tr>");
        emailBody.append("<tr><td><strong>Tiền vận chuyển :</strong></td><td colspan='3' style='text-align: right;'><strong>").append(ConvertUtil.convertFloatToVnd(order.getShippingMoney())).append("</strong></td></tr>");
        emailBody.append("<tr><td><strong>Tiền được giảm       :</strong></td><td colspan='3' style='text-align: right;'><strong>").append(ConvertUtil.convertFloatToVnd(totalVoucher)).append("</strong></td></tr>");
        emailBody.append("<tr><td><strong>Tổng tiền đơn hàng  :</strong></td><td colspan='3' style='text-align: right;'><strong>").append(ConvertUtil.convertFloatToVnd(order.getTotalMoney())).append("</strong></td></tr>");
        emailBody.append("<tr><td><strong>Ngày tạo    :</strong></td><td colspan='3'>").append(ConvertUtil.convertLongToLocalDateTime(order.getCreatedAt())).append("</td></tr>");
        emailBody.append("</table>");
        email.setBody(emailBody.toString());
        if (order.getEmail() != null) {
            toEmail[0] = order.getEmail();
        } else {
            throw new ApiException("Vui lòng nhập email đơn hàng của bạn.");
        }
        email.setToEmail(toEmail);
        emailService.sendEmail(email);
    }

}
