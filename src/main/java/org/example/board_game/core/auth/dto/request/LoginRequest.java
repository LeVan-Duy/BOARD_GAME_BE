package org.example.board_game.core.auth.dto.request;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {

    @NotNull(message = "Vui lòng nhập email của bạn.")
    @NotBlank(message = "Vui lòng nhập email của bạn.")
    String email;

    @NotNull(message = "Vui lòng nhập mật khẩu của bạn.")
    @NotBlank(message = "Vui lòng nhập mật khẩu của bạn.")
    String password;
}
