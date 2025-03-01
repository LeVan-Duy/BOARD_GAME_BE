package org.example.board_game.core.auth.service;

import org.example.board_game.core.auth.dto.request.LoginRequest;
import org.example.board_game.core.auth.dto.request.RefreshTokenRequest;
import org.example.board_game.core.auth.dto.request.RegisterCustomerRequest;
import org.example.board_game.core.auth.dto.response.TokenResponse;
import org.example.board_game.utils.Response;

public interface AuthenticationService {

    Response<TokenResponse> registerCustomer(RegisterCustomerRequest request);

    Response<TokenResponse> authenticate(LoginRequest request);

    Response<TokenResponse> refreshToken(RefreshTokenRequest request);

    Response<Object> logout();
}
