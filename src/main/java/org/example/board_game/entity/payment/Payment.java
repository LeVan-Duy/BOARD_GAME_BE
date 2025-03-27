package org.example.board_game.entity.payment;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.base.PrimaryEntity;
import org.example.board_game.entity.order.Order;
import org.example.board_game.infrastructure.enums.PaymentMethod;
import org.example.board_game.infrastructure.enums.PaymentStatus;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Table(name = "payment")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment extends PrimaryEntity {

    @JoinColumn(name = "shop_order_id", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY)
    Order order;

    @Column(name = "payment_method")
    PaymentMethod method;

    @Column(name = "transaction_code")
    String transactionCode;

    @Column(name = "total_money")
    float totalMoney;

    @Column(name = "description")
    @Nationalized
    String description;

    @Column(name = "status")
    PaymentStatus status;

}