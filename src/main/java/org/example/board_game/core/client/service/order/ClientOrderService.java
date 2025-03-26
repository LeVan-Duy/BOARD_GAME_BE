package org.example.board_game.core.client.service.order;

import org.example.board_game.core.client.domain.dto.request.order.ClientOrderRequest;
import org.example.board_game.utils.Response;

public interface ClientOrderService {

    Response<Object> createOrder(ClientOrderRequest request);

    Response<Object> updateOrder(ClientOrderRequest request);

}
