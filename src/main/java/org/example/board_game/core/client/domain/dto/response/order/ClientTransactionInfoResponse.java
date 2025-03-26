package org.example.board_game.core.client.domain.dto.response.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientTransactionInfoResponse {

    String orderInfo;

    String paymentTime;

    String transactionCode;

    String totalPrice;

    Integer paymentStatus;
}
