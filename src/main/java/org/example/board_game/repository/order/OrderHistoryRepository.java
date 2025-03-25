package org.example.board_game.repository.order;

import org.example.board_game.entity.order.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, String> {

}
