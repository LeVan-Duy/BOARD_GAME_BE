package org.example.board_game.core.client.domain.dto.request.customer;

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
public class ClientCustomerRequest extends PageableRequest {

    String id;

    @NotBlank(message = "Vui lòng nhập đầy đủ họ tên.")
    @NotNull(message = "Vui lòng nhập đầy đủ họ tên.")
    String fullName;

    @NotBlank(message = "Email khách hàng không được bỏ trống.")
    @NotNull(message = "Email khách hàng không được bỏ trống.")
    String email;

    @NotNull(message = "Ngày sinh không được bỏ trống.")
    Long dateOfBirth;

    @NotBlank(message = "Password không được bỏ trống.")
    @NotNull(message = "Password không được bỏ trống.")
    String password;

    CustomerStatus status;

    @NotNull(message = "Vui lòng chọn giới tính cho khách hàng.")
    Gender gender;

    String image;

    ClientAddressRequest address;

}
