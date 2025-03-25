package org.example.board_game.core.admin.service.order;

import org.example.board_game.core.admin.domain.dto.request.order.AdminCheckoutRequest;
import org.example.board_game.core.admin.domain.dto.request.order.AdminOrderRequest;
import org.example.board_game.utils.Response;

public interface AdminOrderService {

    Response<Object> create();

    Response<Object> checkoutAtStore(AdminCheckoutRequest request);

    Response<Object> applyCustomerToOrder(AdminOrderRequest request);

    Response<Object> applyVoucherToOrder(AdminOrderRequest request);

    Response<Object> applyNoteToOrder(AdminOrderRequest request);
}
