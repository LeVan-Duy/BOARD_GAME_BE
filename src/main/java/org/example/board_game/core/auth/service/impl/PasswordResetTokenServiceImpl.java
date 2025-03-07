package org.example.board_game.core.auth.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.email.EmailRequest;
import org.example.board_game.core.admin.service.email.EmailService;
import org.example.board_game.core.auth.dto.request.PasswordResetRequest;
import org.example.board_game.core.auth.service.PasswordResetTokenService;
import org.example.board_game.entity.auth.PasswordResetToken;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.entity.employee.Employee;
import org.example.board_game.infrastructure.constants.ApiProperties;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.repository.auth.PasswordResetTokenRepository;
import org.example.board_game.repository.customer.CustomerRepository;
import org.example.board_game.repository.employee.EmployeeRepository;
import org.example.board_game.utils.Response;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    final EmailService emailService;
    final EmployeeRepository employeeRepository;
    final CustomerRepository customerRepository;
    final PasswordResetTokenRepository passwordResetTokenRepository;
    final PasswordEncoder passwordEncoder;
    boolean isEmailSending = false;
    long timeRemaining = 0;

    @Override
    public Response<Object> sendMailReset(String email) {

        if (isEmailSending) {
            throw new ApiException("Yêu cầu đã được gửi. Vui lòng thử lại sau " + timeRemaining + "s.");
        }
        Customer customer = customerRepository.findByEmailAndDeletedFalse(email).orElse(null);
        if (customer == null) {
            Employee employee = employeeRepository.findByEmailAndDeletedFalse(email).orElse(null);
            if (employee == null) {
                throw new ResourceNotFoundException("Không tìm thấy người dùng với email này trong hệ thống.");
            }
        }
        String token = UUID.randomUUID().toString();
        if (sendMailUrl(email, token)) {
            isEmailSending = true;
            timeRemaining = 59;
            scheduleEmailReset();
            return Response.ok().success("Email đặt lại mật khẩu đã được gửi thành công.", EntityProperties.CODE_POST);
        }
        return Response.fail("Gửi email thất bại.", 400);
    }

    @Override
    public Response<Object> resetPassword(PasswordResetRequest request) {

        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByToken(request.getTokenReset())
                .orElseThrow(() -> new ResourceNotFoundException("Token không hợp lệ."));

        if (resetToken.getExpirationTime().before(new Date())) {
            throw new ApiException("Link chỉ hiệu lực trong vòng 5 phút kể từ khi bạn nhận được email này. Vui lòng thao tác lại quên mật khẩu.");
        }
        if (!request.getConfirmPassword().equals(request.getNewPassword())) {
            throw new ApiException("Mật khẩu xác nhận không khớp với mật khẩu bạn đã nhập.");
        }
        String username = resetToken.getUsername();
        Customer customer = customerRepository.findByEmailAndDeletedFalse(username).orElse(null);
        if (customer != null) {
            customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
            customerRepository.save(customer);
            passwordResetTokenRepository.delete(resetToken);
            return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
        }
        Employee employee = employeeRepository.findByEmailAndDeletedFalse(username).orElse(null);
        if (employee != null) {
            employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
            employeeRepository.save(employee);
            passwordResetTokenRepository.delete(resetToken);
            return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
        }
        return Response.fail("Không tìm thấy người dùng với email này.", 400);
    }


    public void templateResetPassword(String recipientEmail, String resetLink) {
        String[] toEmail = new String[1];
        EmailRequest email = new EmailRequest();
        email.setSubject("BOARD_GAME gửi bạn link thay đổi mật khẩu ...");
        email.setTitleEmail("<h3>Thay đổi mật khẩu</h3>");
        String emailBody = "<html><body>"
                + "<p>Để đặt lại mật khẩu của bạn, vui lòng nhấp vào liên kết bên dưới:</p>"
                + "<p><a href=\"" + resetLink + "\" target=\"_blank\">Link đặt lại mật khẩu</a></p>"
                + "<p>Nếu bạn không yêu cầu điều này, vui lòng bỏ qua email này.</p>"
                + "<p>Cảm ơn!</p>"
                + "</body></html>";
        email.setBody(emailBody);
        toEmail[0] = recipientEmail;
        email.setToEmail(toEmail);
        emailService.sendEmail(email);
    }

    private boolean sendMailUrl(String username, String token) {
        PasswordResetToken resetToken = createPasswordResetToken(username, token);
        String resetLink = ApiProperties.URL_RESET_PASSWORD_LOCAL + resetToken.getToken();
        templateResetPassword(username, resetLink);
        return true;
    }

    private PasswordResetToken createPasswordResetToken(String username, String passwordToken) {

        PasswordResetToken existingToken = passwordResetTokenRepository.findByUsername(username).orElse(null);

        if (existingToken != null && !existingToken.getExpirationTime().before(new Date())) {
            existingToken.setToken(passwordToken);
            existingToken.setExpirationTime(calculateExpiration());
            return passwordResetTokenRepository.save(existingToken);
        }
        if (existingToken != null) {
            passwordResetTokenRepository.delete(existingToken);
        }
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(passwordToken);
        passwordResetToken.setExpirationTime(calculateExpiration());
        passwordResetToken.setUsername(username);
        return passwordResetTokenRepository.save(passwordResetToken);
    }


    private void scheduleEmailReset() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            if (timeRemaining > 0) {
                timeRemaining--;
            }
            if (timeRemaining == 0) {
                isEmailSending = false;
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private Date calculateExpiration() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, EntityProperties.EXPIRATION);
        return calendar.getTime();
    }
}
