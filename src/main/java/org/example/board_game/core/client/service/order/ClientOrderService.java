package org.example.board_game.core.client.service.order;

import org.example.board_game.core.client.domain.dto.request.order.ClientOrderRequest;
import org.example.board_game.core.client.domain.dto.response.order.ClientOrderResponse;
import org.example.board_game.utils.Response;

public interface ClientOrderService {

    Response<Object> createOrder(ClientOrderRequest request);

    Response<Object> updateOrder(String id,ClientOrderRequest request);

    Response<ClientOrderResponse> findByCode(String code);

    Response<ClientOrderResponse> findByIdAndCustomerId(String orderId);

    Response<Object> cancelOrder(String orderId);
}
