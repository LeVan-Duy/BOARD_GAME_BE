package org.example.board_game.core.client.service.impl.order;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.client.domain.dto.response.order.ClientTransactionInfoResponse;
import org.example.board_game.core.client.domain.dto.response.order.ClientUrlResponse;
import org.example.board_game.core.client.service.order.VNPayService;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.order.Order;
import org.example.board_game.entity.order.OrderDetail;
import org.example.board_game.entity.payment.Payment;
import org.example.board_game.entity.product.Product;
import org.example.board_game.entity.voucher.Voucher;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.enums.OrderStatus;
import org.example.board_game.infrastructure.enums.PaymentMethod;
import org.example.board_game.infrastructure.enums.PaymentStatus;
import org.example.board_game.repository.order.OrderRepository;
import org.example.board_game.repository.order.PaymentRepository;
import org.example.board_game.repository.product.ProductRepository;
import org.example.board_game.repository.voucher.VoucherRepository;
import org.example.board_game.utils.Response;
import org.example.board_game.utils.VNPayUtil;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayServiceImpl implements VNPayService {

    OrderRepository orderRepository;
    PaymentRepository paymentRepository;
    EntityService entityService;
    VoucherRepository voucherRepository;
    ProductRepository productRepository;

    @Override
    public Response<ClientUrlResponse> createOrder(Float total, String orderId) {

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = VNPayUtil.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VNPayUtil.vnp_TmnCode;
        String orderType = "order-type";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(total * 100));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderId);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);

        vnp_Params.put("vnp_ReturnUrl", VNPayUtil.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        String queryUrl = VNPayUtil.getQueryUrl(vnp_Params, VNPayUtil.vnp_HashSecret);
        String paymentUrl = VNPayUtil.vnp_PayUrl + "?" + queryUrl;

        return Response.of(ClientUrlResponse.builder().url(paymentUrl).build())
                .success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    private int orderReturn(HttpServletRequest request) {

        Map<String, String> fields = new HashMap<>();

        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName;
            String fieldValue;
            fieldName = URLEncoder.encode(params.nextElement(), StandardCharsets.US_ASCII);
            fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");
        String signValue = VNPayUtil.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }

    @Transactional
    @Override
    public Response<ClientUrlResponse> authenticateVnPay(HttpServletRequest request) {

        int result = orderReturn(request);
        String orderId = request.getParameter("vnp_OrderInfo");
        String transactionCode = request.getParameter("vnp_TransactionNo");

        ClientTransactionInfoResponse transaction = new ClientTransactionInfoResponse();
        transaction.setOrderInfo(orderId);
        transaction.setTransactionCode(transactionCode);
        transaction.setPaymentTime(request.getParameter("vnp_PayDate"));
        transaction.setTotalPrice(request.getParameter("vnp_Amount"));

        Order order = entityService.getOrder(orderId);
        String url;
        String message;
        int code;

        if (result == 1) {
            Payment payment = new Payment();
            payment.setOrder(order);
            payment.setMethod(PaymentMethod.TRANSFER);
            payment.setDescription(order.getNote());
            payment.setTotalMoney(order.getTotalMoney());
            payment.setTransactionCode(transactionCode);
            payment.setStatus(PaymentStatus.COMPLETED);
            order.setStatus(OrderStatus.WAIT_FOR_DELIVERY);
            entityService.createOrderHistory(order, OrderStatus.WAIT_FOR_DELIVERY);
            paymentRepository.save(payment);
            orderRepository.save(order);

            url = "Địt mẹ mày thành công quá đã :vvvv";
            message = "Thanh toán thành công.";
            code = 201;

        } else if (result == 11 || result == 15) {
            revertForeignKeyConstraint(order);
            url = "Địt mẹ mày thất bại rồi :(";
            message = "Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.";
            code = 400;

        } else {
            revertForeignKeyConstraint(order);
            url = "Địt mẹ mày thất bại rồi :(";
            message = "Thanh toán thất bại chúng tôi chưa tìm ra lí do.";
            code = 400;

        }
        return Response.of(ClientUrlResponse.builder().url(url).build()).success(message, code);
    }

    private void revertForeignKeyConstraint(Order order) {

        List<Product> products = new ArrayList<>();
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Product product = orderDetail.getProduct();
            product.setQuantity(product.getQuantity() + orderDetail.getQuantity());
            products.add(product);
        }
        Voucher voucher = order.getVoucher();
        if (voucher != null) {
            voucher.setQuantity(voucher.getQuantity() + 1);
            voucherRepository.save(voucher);
        }
        order.setDeleted(true);
        productRepository.saveAll(products);
        orderRepository.save(order);
    }

}
