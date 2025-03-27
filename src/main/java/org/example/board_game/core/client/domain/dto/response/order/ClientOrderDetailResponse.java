package org.example.board_game.core.client.domain.dto.response.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.client.domain.dto.response.product.ClientProductResponse;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientOrderDetailResponse {

    String id;

    ClientProductResponse product;

    int quantity;

    float price;

    float totalPrice;
}
