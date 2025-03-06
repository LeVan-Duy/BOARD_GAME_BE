package org.example.board_game.core.admin.domain.dto.request.employee;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.common.PageableRequest;
import org.example.board_game.infrastructure.enums.EmployeeStatus;
import org.example.board_game.infrastructure.enums.Gender;
import org.example.board_game.infrastructure.enums.Role;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminEmployeeRequest extends PageableRequest {

    String id;

    @NotBlank(message = "Vui lòng nhập đầy đủ học tên.")
    String fullName;

    @NotBlank(message = "Email không được bỏ trống.")
    String email;

    @NotBlank(message = "Password không được bỏ trống.")
    String password;

    EmployeeStatus status;

    @NotBlank(message = "Password không được bỏ trống.")
    String address;

    @NotNull(message = "Vui lòng chọn giới tính.")
    Gender gender;

    @NotBlank(message = "Số điện thoại không được bỏ trống.")
    String phoneNumber;

    String image;

    @NotNull(message = "Vui lòng chọn chức vụ.")
    Role role;
}
