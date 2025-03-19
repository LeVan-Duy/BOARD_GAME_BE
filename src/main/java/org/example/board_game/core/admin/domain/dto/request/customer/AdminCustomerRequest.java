package org.example.board_game.core.admin.domain.dto.request.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.common.PageableRequest;
import org.example.board_game.infrastructure.enums.CustomerStatus;
import org.example.board_game.infrastructure.enums.Gender;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCustomerRequest extends PageableRequest {

    String id;

    @NotBlank(message = "Vui lòng nhập đầy đủ họ tên.")
    @NotNull(message = "Vui lòng nhập đầy đủ họ tên.")
    String fullName;

    @NotBlank(message = "Email khách hàng không được bỏ trống.")
    @NotNull(message = "Email khách hàng không được bỏ trống.")
    String email;

    @NotNull(message = "Ngày sinh không được bỏ trống.")
    Long dateOfBirth;

    String password;

    CustomerStatus status;

    @NotNull(message = "Vui lòng chọn giới tính cho khách hàng.")
    Gender gender;

    String image;

    AdminAddressRequest address;

}
