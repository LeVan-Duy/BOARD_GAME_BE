package org.example.board_game.core.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.infrastructure.enums.Gender;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterCustomerRequest {

    @NotNull(message = "Vui lòng nhập ngày sinh của bạn.")
    Long dateOfBirth;

    @NotNull(message = "Vui lòng nhập họ tên đầy đủ.")
    @NotBlank(message = "Vui lòng nhập họ tên đầy đủ.")
    String fullName;

    @NotNull(message = "Vui lòng chọn giới tính của bạn.")
    Gender gender;

    @NotNull(message = "Vui lòng nhập email (email sẽ sử dụng tên đăng nhập).")
    @NotBlank(message = "Vui lòng nhập email (email sẽ sử dụng tên đăng nhập).")
    String email;

    @NotNull(message = "Vui lòng nhập password.")
    @NotBlank(message = "Vui lòng nhập password.")
    String password;
}
