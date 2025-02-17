package org.example.board_game.entity.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.board_game.entity.base.PrimaryEntity;
import org.example.board_game.entity.product.Product;
import org.example.board_game.infrastructure.enums.OrderStatus;

@Getter
@Setter
@Table(name = "order_detail")
@Entity
public class OrderDetail extends PrimaryEntity {

    @JoinColumn(name = "product_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private float price;

    @Column(name = "total_price")
    private float totalPrice;

    @Column(name = "status")
    private OrderStatus status;
}
