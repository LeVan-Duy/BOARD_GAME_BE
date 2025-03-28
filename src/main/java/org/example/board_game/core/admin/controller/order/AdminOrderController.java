package org.example.board_game.core.admin.controller.order;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.order.AdminOrderRequest;
import org.example.board_game.core.admin.domain.dto.response.order.AdminOrderResponse;
import org.example.board_game.core.admin.service.order.AdminOrderService;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.utils.Response;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminOrderController {

    AdminOrderService adminOrderService;

    @GetMapping("/get-all")
    public Response<PageableObject<AdminOrderResponse>> getAll(AdminOrderRequest request) {
        return adminOrderService.getAll(request);
    }

    @PostMapping("/confirm/{id}")
    public Response<Object> confirm(@PathVariable String id, @Valid @RequestBody AdminOrderRequest request) {
        return adminOrderService.confirm(id, request);
    }
}
