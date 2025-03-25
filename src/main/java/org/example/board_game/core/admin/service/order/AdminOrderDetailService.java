package org.example.board_game.core.admin.service.order;


import org.example.board_game.core.admin.domain.dto.request.order.AdminOrderDetailRequest;
import org.example.board_game.utils.Response;

public interface AdminOrderDetailService {

    Response<Object> addProductToOrder(AdminOrderDetailRequest request);

}
