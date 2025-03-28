package org.example.board_game.core.admin.service.impl.scheduled;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.service.scheduled.ScheduledService;
import org.example.board_game.entity.order.Order;
import org.example.board_game.entity.order.OrderDetail;
import org.example.board_game.entity.payment.Payment;
import org.example.board_game.entity.product.Product;
import org.example.board_game.entity.voucher.Voucher;
import org.example.board_game.infrastructure.enums.OrderStatus;
import org.example.board_game.infrastructure.enums.OrderType;
import org.example.board_game.repository.order.OrderRepository;
import org.example.board_game.repository.order.PaymentRepository;
import org.example.board_game.repository.product.ProductRepository;
import org.example.board_game.repository.voucher.VoucherRepository;
import org.example.board_game.utils.CollectionUtils;
import org.example.board_game.utils.ConvertUtil;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class ScheduledServiceImpl implements ScheduledService {

    VoucherRepository voucherRepository;
    OrderRepository orderRepository;
    ProductRepository productRepository;
    PaymentRepository paymentRepository;

    @Override
    public void updateVoucherStatusAutomatically() {
        Long currentDate = ConvertUtil.convertDateToLong(new Date());
        voucherRepository.updateStatusAutomatically(currentDate);
    }

    @Transactional
    @Override
    public void removeOrderPendingAndOnline() {
        long currentMillis = Instant.now().toEpochMilli();
        long fifteenMinutesAgo = currentMillis - (15 * 60 * 1000); // 15 phút * 60 giây * 1000 ms
        List<Product> products = new ArrayList<>();
        List<Order> expiredOrders = orderRepository
                .findAllByStatusAndCreatedAtBeforeAndDeletedFalseAndType(OrderStatus.PENDING, fifteenMinutesAgo, OrderType.ONLINE);
        if (CollectionUtils.isListEmpty(expiredOrders)) {
            return;
        }
        List<Voucher> vouchers = new ArrayList<>();
        List<Payment> payments = new ArrayList<>();
        for (Order order : expiredOrders) {
            List<OrderDetail> orderDetails = order.getOrderDetails();
            for (OrderDetail orderDetail : orderDetails) {
                Product product = orderDetail.getProduct();
                product.setQuantity(product.getQuantity() + orderDetail.getQuantity());
                products.add(product);
            }
            if (order.getVoucher() != null) {
                Voucher voucher = order.getVoucher();
                voucher.setQuantity(order.getVoucher().getQuantity() + 1);
                vouchers.add(order.getVoucher());
            }
            if (order.getPayment() != null) {
                Payment payment = order.getPayment();
                payment.setDeleted(true);
                payments.add(payment);
            }
            order.setDeleted(true);
        }
        paymentRepository.saveAll(payments);
        voucherRepository.saveAll(vouchers);
        productRepository.saveAll(products);
        orderRepository.saveAll(expiredOrders);
    }

}
