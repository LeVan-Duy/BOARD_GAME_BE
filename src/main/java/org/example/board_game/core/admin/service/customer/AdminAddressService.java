package org.example.board_game.core.admin.service.customer;

import org.example.board_game.core.admin.domain.dto.request.customer.AdminAddressRequest;
import org.example.board_game.core.admin.domain.dto.response.customer.AdminAddressResponse;
import org.example.board_game.core.common.base.BaseService;
import org.example.board_game.utils.Response;

public interface AdminAddressService extends BaseService<AdminAddressResponse, String, AdminAddressRequest> {

    Response<Object> updateDefaultAddress(String addressId);
}
