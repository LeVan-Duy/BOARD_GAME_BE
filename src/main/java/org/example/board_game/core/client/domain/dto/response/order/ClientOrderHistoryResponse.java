package org.example.board_game.core.client.domain.dto.response.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.infrastructure.enums.OrderStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientOrderHistoryResponse {

    String id;

    OrderStatus actionStatus;

    String note;

    Long createdAt;
}
