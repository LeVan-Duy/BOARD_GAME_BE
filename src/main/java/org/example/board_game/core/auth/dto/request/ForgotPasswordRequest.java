package org.example.board_game.core.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForgotPasswordRequest {

    @NotBlank(message = "Vui lòng nhập email đăng nhập của bạn để quên mật khẩu.")
    @NotNull(message = "Vui lòng nhập email đăng nhập của bạn để quên mật khẩu.")
    String email;
}
