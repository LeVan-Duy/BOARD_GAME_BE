package org.example.board_game.core.client.service.customer;

import org.example.board_game.core.client.domain.dto.request.customer.ClientAddressRequest;
import org.example.board_game.core.client.domain.dto.request.customer.ClientCustomerRequest;
import org.example.board_game.core.client.domain.dto.response.customer.ClientCustomerResponse;
import org.example.board_game.utils.Response;

public interface ClientCustomerService {

    Response<ClientCustomerResponse> getProfile();

    Response<Object> updateProfile(ClientCustomerRequest request);

    Response<Object> addAddress(ClientAddressRequest request);

    Response<Object> updateAddress(ClientAddressRequest request, String id);

    Response<Object> deleteAddress(String addressId);

    Response<Object> updateDefaultAddress(String addressId);

}
