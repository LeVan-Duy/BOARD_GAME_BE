package org.example.board_game.repository.cart;

import org.example.board_game.entity.cart.Cart;
import org.example.board_game.entity.voucher.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

}
