package org.example.board_game.core.auth.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.auth.dto.request.LoginRequest;
import org.example.board_game.core.auth.dto.request.RefreshTokenRequest;
import org.example.board_game.core.auth.dto.request.RegisterCustomerRequest;
import org.example.board_game.core.auth.dto.response.TokenResponse;
import org.example.board_game.core.auth.service.AuthenticationService;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.utils.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/register")
    public Response<TokenResponse> register(@RequestBody @Valid RegisterCustomerRequest request) {
        TokenResponse response = authenticationService.registerCustomer(request).getData();
        return Response.of(response).success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @PostMapping("/login")
    public Response<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        TokenResponse response = authenticationService.authenticate(request).getData();
        return Response.of(response).success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @PostMapping("/refresh")
    public Response<TokenResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        TokenResponse response = authenticationService.refreshToken(request).getData();
        return Response.of(response).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @PostMapping("/logout")
    public Response<Object> logout() {
        return authenticationService.logout();
    }
}
