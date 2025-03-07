package org.example.board_game.core.auth.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.mapper.customer.AdminCustomerMapper;
import org.example.board_game.core.auth.dto.request.ChangePasswordRequest;
import org.example.board_game.core.auth.dto.request.LoginRequest;
import org.example.board_game.core.auth.dto.request.RefreshTokenRequest;
import org.example.board_game.core.auth.dto.request.RegisterCustomerRequest;
import org.example.board_game.core.auth.dto.response.TokenResponse;
import org.example.board_game.core.auth.service.AuthenticationService;
import org.example.board_game.core.auth.service.JwtService;
import org.example.board_game.core.auth.service.UserDetailService;
import org.example.board_game.core.auth.utils.AuthHelper;
import org.example.board_game.core.common.base.BaseRedisService;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.entity.employee.Employee;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.enums.CustomerStatus;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.repository.customer.CustomerRepository;
import org.example.board_game.repository.employee.EmployeeRepository;
import org.example.board_game.utils.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    CustomerRepository customerRepository;
    EmployeeRepository employeeRepository;
    EntityService entityService;
    AuthenticationManager authenticationManager;
    PasswordEncoder passwordEncoder;
    UserDetailService userDetailService;
    JwtService jwtService;
    AdminCustomerMapper customerMapper = AdminCustomerMapper.INSTANCE;
    BaseRedisService redisService;

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
        String messageError = "Thông tin tài khoản hoặc mật khẩu không chính xác. Vui lòng thử lại.";
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException exception) {
            throw new ApiException(messageError);
        }
        Customer customer = customerRepository.findByEmailAndDeletedFalse(email).orElse(null);
        if (customer == null) {
            Employee employee = employeeRepository.findByEmailAndDeletedFalse(email).orElse(null);
            if (employee == null) {
                throw new ApiException(messageError);
            }
            if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
                throw new ApiException(messageError);
            }
            accessToken = jwtService.generateToken(employee);
            refreshToken = jwtService.createRefreshToken(employee);

        } else {
            if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
                throw new ApiException(messageError);
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
        String username = (String) redisService.get("refresh_mapping:" + refreshToken);
        if (username == null || !jwtService.validRefreshToken(refreshToken)) {
            throw new ApiException("Refresh token không hợp lệ hoặc đã hết hạn.");
        }
        UserDetails userDetails = userDetailService.loadUserByUsername(username);
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
        String username = AuthHelper.getUsername();
        String refreshToken = (String) redisService.get("refresh_token:" + username);
        if (refreshToken != null) {
            jwtService.invalidateRefreshToken(refreshToken, username);
            return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
        }
        return Response.ok().success("Người dùng không hợp lệ.", HttpStatus.UNAUTHORIZED.value());
    }

    @Override
    public Response<Object> changePassword(ChangePasswordRequest request) {
        String username;
        Customer customer = entityService.getCustomerByAuth();
        if (customer != null) {
            passwordCompare(request, customer.getPassword());
            customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
            customerRepository.save(customer);
            username = customer.getUsername();
        } else {
            Employee employee = entityService.getEmployeeByAuth();
            if (employee != null) {
                passwordCompare(request, employee.getPassword());
                employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
                employeeRepository.save(employee);
                username = employee.getUsername();
            } else {
                throw new ResourceNotFoundException("Bạn chưa đăng nhập vào hệ thống.");
            }
        }
        String refreshToken = (String) redisService.get("refresh_token:" + username);
        jwtService.invalidateRefreshToken(refreshToken, username);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }


    private void passwordCompare(ChangePasswordRequest request, String oldPassword) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), oldPassword)) {
            throw new ApiException("Mật khẩu hiện tại không chính xác.");
        }
        if (!request.getConfirmPassword().equals(request.getNewPassword())) {
            throw new ApiException("Mật khẩu xác nhận không trùng khớp mật khẩu mới.");
        }
    }
}
