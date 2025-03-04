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
public class ChangePasswordRequest {

    @NotNull(message = "Vui lòng nhập mật khẩu hiện tại của bạn.")
    @NotBlank(message =  "Vui lòng nhập mật khẩu hiện tại của bạn.")
    String currentPassword;

    @NotNull(message = "Vui lòng nhập mật khẩu mới.")
    @NotBlank(message = "Vui lòng nhập mật khẩu mới")
    String newPassword;

    @NotNull(message = "Vui lòng nhập lại mật khẩu xác nhận.")
    @NotBlank(message = "Vui lòng nhập lại mật khẩu xác nhận.")
    String confirmPassword;
}
