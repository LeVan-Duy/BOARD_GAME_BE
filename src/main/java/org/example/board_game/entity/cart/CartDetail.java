package org.example.board_game.entity.cart;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.base.PrimaryEntity;
import org.example.board_game.entity.product.Product;

@Getter
@Setter
@Table(name = "cart_detail")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartDetail extends PrimaryEntity {

    @JoinColumn(name = "product_detail_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    Product product;

    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    Cart cart;

    @Column(name = "quantity")
    int quantity;

}
