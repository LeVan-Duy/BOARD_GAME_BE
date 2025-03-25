package org.example.board_game.core.admin.domain.dto.request.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminOrderRequest {

    String orderId;

    String customerId;

    String voucherId;

    String note;

}
