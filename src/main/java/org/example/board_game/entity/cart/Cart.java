package org.example.board_game.entity.cart;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.board_game.entity.base.PrimaryEntity;
import org.example.board_game.entity.customer.Customer;

import java.util.List;

@Getter
@Setter
@Table(name = "cart")
@Entity
public class Cart extends PrimaryEntity {

    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<CartDetail> cartDetails;
}
