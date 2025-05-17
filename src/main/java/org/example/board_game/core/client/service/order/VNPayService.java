package org.example.board_game.core.client.service.order;

import jakarta.servlet.http.HttpServletRequest;
import org.example.board_game.core.client.domain.dto.response.order.ClientUrlResponse;
import org.example.board_game.utils.Response;
import org.springframework.web.servlet.view.RedirectView;

public interface VNPayService {

    Response<ClientUrlResponse> createOrder(Float total, String orderId,int minute);

    RedirectView authenticateVnPay(HttpServletRequest request);

}
