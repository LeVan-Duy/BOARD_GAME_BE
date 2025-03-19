package org.example.board_game.core.admin.service.customer;

import org.example.board_game.core.admin.domain.dto.request.customer.AdminAddressRequest;
import org.example.board_game.core.admin.domain.dto.response.customer.AdminAddressResponse;
import org.example.board_game.core.common.base.BaseService;
import org.example.board_game.utils.Response;

import java.util.List;

public interface AdminAddressService extends BaseService<AdminAddressResponse, String, AdminAddressRequest> {

    Response<List<AdminAddressResponse>> getAllByCustomerId(String customerId);

    Response<Object> updateDefaultAddress(String addressId);
}
