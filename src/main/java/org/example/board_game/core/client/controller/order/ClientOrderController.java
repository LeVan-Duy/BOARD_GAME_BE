package org.example.board_game.core.client.controller.order;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.client.domain.dto.request.order.ClientOrderRequest;
import org.example.board_game.core.client.domain.dto.response.order.ClientOrderResponse;
import org.example.board_game.core.client.service.order.ClientOrderService;
import org.example.board_game.utils.Response;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/client/order")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClientOrderController {

    ClientOrderService orderService;

    @GetMapping("/tracking/{code}")
    public Response<ClientOrderResponse> findByCode(@PathVariable String code) {
        return orderService.findByCode(code);
    }

    @GetMapping("/detail/{id}")
    public Response<ClientOrderResponse> findById(@PathVariable String id) {
        return orderService.findByIdAndCustomerId(id);
    }

    @PostMapping("/cancel/{id}")
    public Response<Object> cancel(@PathVariable String id) {
        return orderService.cancelOrder(id);
    }

    @PostMapping("/create")
    public Response<Object> create(@Valid @RequestBody ClientOrderRequest request) {
        return orderService.createOrder(request);
    }

    @PostMapping("/update/{id}")
    public Response<Object> update(@PathVariable String id, @Valid @RequestBody ClientOrderRequest request) {
        return orderService.updateOrder(id, request);
    }
}
