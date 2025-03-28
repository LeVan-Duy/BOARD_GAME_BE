package org.example.board_game.core.client.service.order;

import jakarta.servlet.http.HttpServletRequest;
import org.example.board_game.core.client.domain.dto.response.order.ClientUrlResponse;
import org.example.board_game.utils.Response;

public interface VNPayService {

    Response<ClientUrlResponse> createOrder(Float total, String orderId);

    Response<Object> authenticateVnPay(HttpServletRequest request);

}
