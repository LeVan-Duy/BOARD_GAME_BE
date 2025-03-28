package org.example.board_game.core.client.service.impl.order;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.client.domain.dto.response.order.ClientOrderIdResponse;
import org.example.board_game.core.client.domain.dto.response.order.ClientResultVnPayResponse;
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

import java.io.UnsupportedEncodingException;
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
        vnp_Params.put("vnp_Amount", String.valueOf(total.intValue() * 100));
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

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayUtil.hmacSHA512(VNPayUtil.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayUtil.vnp_PayUrl + "?" + queryUrl;

        return Response.of(ClientUrlResponse.builder().url(paymentUrl).build())
                .success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    private ClientResultVnPayResponse orderReturn(HttpServletRequest request) {

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
        ClientResultVnPayResponse result;
        if (signValue.equals(vnp_SecureHash)) {
            result = resultVnPay(request.getParameter("vnp_TransactionStatus"));
        } else {
            result = new ClientResultVnPayResponse();
            result.setMessage("GD thất bại.");
            result.setCode(400);
        }
        return result;
    }

    @Transactional
    @Override
    public Response<Object> authenticateVnPay(HttpServletRequest request) {

        ClientResultVnPayResponse result = orderReturn(request);
        String orderId = request.getParameter("vnp_OrderInfo");
        String transactionCode = request.getParameter("vnp_TransactionNo");

        Order order = entityService.getOrder(orderId);
        if (result.getCode() == 201) {
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

            ClientOrderIdResponse response = new ClientOrderIdResponse();
            response.setOrderId(orderId);
            return Response.of((Object) response).success(result.getMessage(), result.getCode());
        }
        revertForeignKeyConstraint(order);
        return Response.ok().success(result.getMessage(), result.getCode());
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

    private ClientResultVnPayResponse resultVnPay(String code) {

        ClientResultVnPayResponse result = new ClientResultVnPayResponse();
        switch (code) {
            case "00" -> {
                result.setCode(201);
                result.setMessage("Giao dịch thành công.");
            }
            case "01" -> {
                result.setCode(400);
                result.setMessage("Giao dịch chưa hoàn tất.");
            }
            case "02" -> {
                result.setCode(400);
                result.setMessage("Giao dịch bị lỗi.");
            }
            case "04" -> {
                result.setCode(400);
                result.setMessage("Giao dịch đảo (Khách hàng đã bị trừ tiền tại Ngân hàng nhưng GD chưa thành công ở VNPAY).");
            }
            case "05" -> {
                result.setCode(400);
                result.setMessage("VNPAY đang xử lý giao dịch này (GD hoàn tiền).");
            }
            case "06" -> {
                result.setCode(400);
                result.setMessage("VNPAY đã gửi yêu cầu hoàn tiền sang Ngân hàng (GD hoàn tiền).");
            }
            case "07" -> {
                result.setCode(400);
                result.setMessage("Giao dịch bị nghi ngờ gian lận.");
            }
            case "09" -> {
                result.setCode(400);
                result.setMessage("GD Hoàn trả bị từ chối.");
            }
            default -> {
                result.setCode(400);
                result.setMessage("GD thất bại.");
            }
        }
        return result;
    }

}
