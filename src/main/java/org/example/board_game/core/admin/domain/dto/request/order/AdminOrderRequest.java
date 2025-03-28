package org.example.board_game.core.admin.domain.dto.request.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.common.PageableRequest;
import org.example.board_game.infrastructure.enums.OrderStatus;
import org.example.board_game.infrastructure.enums.OrderType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminOrderRequest extends PageableRequest {

    String orderId;

    String customerId;

    String voucherId;

    String note;

    OrderStatus status;

    OrderType type;

    Float priceMin;

    Float priceMax;

}
