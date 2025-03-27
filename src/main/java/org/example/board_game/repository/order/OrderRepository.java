package org.example.board_game.repository.order;

import org.example.board_game.entity.order.Order;
import org.example.board_game.entity.voucher.Voucher;
import org.example.board_game.infrastructure.enums.OrderStatus;
import org.example.board_game.infrastructure.enums.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    Integer countAllByStatusAndTypeAndDeletedFalse(OrderStatus status, OrderType type);

    Optional<Order> findByCodeAndDeletedFalse(String code);

    Optional<Order> findByIdAndCustomer_Id(String id, String customerId);
}
