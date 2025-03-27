package org.example.board_game.core.client.domain.dto.response.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.infrastructure.enums.PaymentMethod;
import org.example.board_game.infrastructure.enums.PaymentStatus;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientPaymentResponse {

    PaymentMethod method;

    String transactionCode;

    float totalMoney;

    String description;

    PaymentStatus status;
}
