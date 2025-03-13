package org.example.board_game.core.auth.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.response.employee.AdminEmployeeResponse;
import org.example.board_game.core.auth.dto.request.*;
import org.example.board_game.core.auth.dto.response.TokenResponse;
import org.example.board_game.core.auth.service.AuthenticationService;
import org.example.board_game.core.auth.service.PasswordResetTokenService;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.utils.Response;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationController {

    AuthenticationService authenticationService;
    PasswordResetTokenService passwordResetTokenService;

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

    @PostMapping("/change-password")
    public Response<Object> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        return authenticationService.changePassword(request);
    }

    @PostMapping("/forgot-password")
    public Response<Object> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        return passwordResetTokenService.sendMailReset(request.getEmail());
    }

    @PostMapping("/reset-password")
    public Response<Object> resetPassword(@RequestBody @Valid PasswordResetRequest request) {
        return passwordResetTokenService.resetPassword(request);
    }

    @GetMapping("/admin-profile")
    public Response<AdminEmployeeResponse> getProfile() {
        return authenticationService.adminProfile();
    }
}
