package org.example.board_game.core.client.controller.order;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.client.domain.dto.response.order.ClientUrlResponse;
import org.example.board_game.core.client.service.order.VNPayService;
import org.example.board_game.utils.Response;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client/transaction")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClientVNPayController {

    VNPayService vnPayService;

    @PostMapping("/checkout")
    public Response<ClientUrlResponse> submitOrder(@RequestParam("amount") float orderTotal,
                                                   @RequestParam("orderInfo") String orderInfo) {
        return vnPayService.createOrder(orderTotal, orderInfo);
    }

    @GetMapping("/authenticate")
    public Response<Object> getApiSuccess(HttpServletRequest request) {
        return vnPayService.authenticateVnPay(request);
    }

}
