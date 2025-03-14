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
        return authenticationService.registerCustomer(request);
    }

    @PostMapping("/login")
    public Response<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return authenticationService.authenticate(request);
    }

    @PostMapping("/refresh")
    public Response<TokenResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return authenticationService.refreshToken(request);
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
