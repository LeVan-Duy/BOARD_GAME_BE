package org.example.board_game.entity.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.board_game.entity.base.PrimaryEntity;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.enums.OrderStatus;

@Getter
@Setter
@Table(name = "order_history")
@Entity
public class OrderHistory extends PrimaryEntity {

    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @Column(name = "action_status")
    private OrderStatus actionStatus;

    @Column(name = "note", length = EntityProperties.LENGTH_DESCRIPTION)
    private String note;

}
