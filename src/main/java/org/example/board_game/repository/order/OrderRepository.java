package org.example.board_game.repository.order;

import org.example.board_game.entity.order.Order;
import org.example.board_game.entity.voucher.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

}
