package org.example.board_game.core.auth.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.mapper.customer.AdminCustomerMapper;
import org.example.board_game.core.auth.dto.request.LoginRequest;
import org.example.board_game.core.auth.dto.request.RefreshTokenRequest;
import org.example.board_game.core.auth.dto.request.RegisterCustomerRequest;
import org.example.board_game.core.auth.dto.response.TokenResponse;
import org.example.board_game.core.auth.service.AuthenticationService;
import org.example.board_game.core.auth.service.JwtService;
import org.example.board_game.core.auth.service.UserDetailService;
import org.example.board_game.entity.auth.BlacklistToken;
import org.example.board_game.entity.auth.RefreshToken;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.entity.employee.Employee;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.enums.CustomerStatus;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.repository.auth.BlacklistTokenRepository;
import org.example.board_game.repository.auth.RefreshTokenRepository;
import org.example.board_game.repository.customer.CustomerRepository;
import org.example.board_game.repository.employee.EmployeeRepository;
import org.example.board_game.utils.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    CustomerRepository customerRepository;
    EmployeeRepository employeeRepository;
    AuthenticationManager authenticationManager;
    PasswordEncoder passwordEncoder;
    UserDetailService userDetailService;
    JwtService jwtService;
    AdminCustomerMapper customerMapper = AdminCustomerMapper.INSTANCE;
    RefreshTokenRepository refreshTokenRepository;
    BlacklistTokenRepository blacklistTokenRepository;

    @Override
    public Response<TokenResponse> registerCustomer(RegisterCustomerRequest request) {

        String email = request.getEmail();
        boolean isMailExistByCustomer = customerRepository.existsByEmailAndDeletedFalse(email);
        if (isMailExistByCustomer) {
            throw new ApiException("Email này đã tồn tại trong hệ thống.");
        }
        boolean isMailExistByEmployee = customerRepository.existsByEmailAndDeletedFalse(email);
        if (isMailExistByEmployee) {
            throw new ApiException("Email này đã tồn tại trong hệ thống.");
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        Customer customer = customerMapper.byRegisterRequest(request);
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setCreatedBy("system");
        Customer savedCustomer = customerRepository.save(customer);
        String accessToken = jwtService.generateToken(savedCustomer);
        String refreshToken = jwtService.createRefreshToken(savedCustomer);
        return Response.of(TokenResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
    }

    @Override
    public Response<TokenResponse> authenticate(LoginRequest request) {

        String email = request.getEmail();
        String accessToken;
        String refreshToken;

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException exception) {
            throw new ApiException("Thông tin tài khoản hoặc mật khẩu không chính xác. Vui lòng thử lại");
        }
        Customer customer = customerRepository.findByEmailAndDeletedFalse(email).orElse(null);
        if (customer == null) {
            Employee employee = employeeRepository.findByEmailAndDeletedFalse(email).orElse(null);
            if (employee == null) {
                throw new ApiException("Thông tin tài khoản hoặc mật khẩu không chính xác. Vui lòng thử lại");
            }
            if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
                throw new ApiException("Thông tin tài khoản hoặc mật khẩu không chính xác. Vui lòng thử lại");
            }
            accessToken = jwtService.generateToken(employee);
            refreshToken = jwtService.createRefreshToken(employee);

        } else {
            if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
                throw new ApiException("Thông tin tài khoản hoặc mật khẩu không chính xác. Vui lòng thử lại");
            }
            accessToken = jwtService.generateToken(customer);
            refreshToken = jwtService.createRefreshToken(customer);
        }
        return Response.of(TokenResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build());
    }

    @Override
    public Response<TokenResponse> refreshToken(RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();
        if (!jwtService.validRefreshToken(refreshToken)) {
            throw new ApiException("Refresh token không hợp lệ hoặc đã hết hạn.");
        }
        RefreshToken storedToken = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token không tồn tại."));

        UserDetails userDetails = userDetailService.loadUserByUsername(storedToken.getUsername());
        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.createRefreshToken(userDetails);

        return Response.of(TokenResponse
                .builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build());
    }

    @Override
    public Response<Object> logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByUsername(username);
        if (refreshToken.isPresent()) {
            BlacklistToken blacklistToken = new BlacklistToken();
            blacklistToken.setToken(refreshToken.get().getToken());
            refreshTokenRepository.delete(refreshToken.get());
            blacklistTokenRepository.save(blacklistToken);
            return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
        }
        return Response.ok().success("Người dùng không hợp lệ.", HttpStatus.UNAUTHORIZED.value());
    }
}
