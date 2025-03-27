package org.example.board_game.entity.order;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.base.PrimaryEntity;
import org.example.board_game.entity.customer.Address;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.entity.employee.Employee;
import org.example.board_game.entity.payment.Payment;
import org.example.board_game.entity.voucher.Voucher;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.enums.OrderStatus;
import org.example.board_game.infrastructure.enums.OrderType;
import org.example.board_game.utils.RandomStringUtil;
import java.util.List;

@Getter
@Setter
@Table(name = "shop_order")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order extends PrimaryEntity {

    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    Customer customer;

    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    Employee employee;

    @JoinColumn(name = "voucher_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    Voucher voucher;

    @JoinColumn(name = "address_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    Address address;

    @Column(name = "phone_number", length = EntityProperties.LENGTH_PHONE)
    String phoneNumber;

    @Column(name = "full_name", length = EntityProperties.LENGTH_NAME)
    String fullName;

    @Column(name = "email", length = EntityProperties.LENGTH_EMAIL, unique = true)
    String email;

    @Column(name = "origin_money")
    Float originMoney;

    @Column(name = "reducel_money")
    Float reduceMoney;

    @Column(name = "total_money")
    Float totalMoney;

    @Column(name = "shipping_money")
    Float shippingMoney;

    @Column(name = "confirmation_date")
    Long confirmationDate;

    @Column(name = "expected_delivery_date")
    Long expectedDeliveryDate;

    @Column(name = "delivery_start_date")
    Long deliveryStartDate;

    @Column(name = "received_date")
    Long receivedDate;

    @Column(name = "type")
    OrderType type;

    @Column(name = "note", length = EntityProperties.LENGTH_DESCRIPTION)
    String note;

    @Column(name = "status")
    OrderStatus status;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    Payment payment;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    List<OrderDetail> orderDetails;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    List<OrderHistory> orderHistories;

    @Column(name = "code", updatable = false, length = EntityProperties.LENGTH_CODE, unique = true)
    String code;

    @PrePersist
    private void generateCode() {
        int codeLength = 5;
        String randomPart = RandomStringUtil.randomAlphaNumeric(codeLength);
        this.code = "BOARD_GAME" + "-" + randomPart;
    }

}
