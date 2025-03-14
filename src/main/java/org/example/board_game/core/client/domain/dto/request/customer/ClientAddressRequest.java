package org.example.board_game.core.client.domain.dto.request.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientAddressRequest {

    String id;

    @NotBlank(message = "Số điện thoại không được bỏ trống.")
    @NotNull(message = "Số điện thoại không được bỏ trống.")
    String phoneNumber;

    Boolean isDefault;

    @NotNull(message = "Vui lòng chọn tỉnh/thành phố.")
    Integer districtId;

    @NotNull(message = "Vui lòng chọn quận/huyện.")
    Integer provinceId;

    @NotNull(message = "Vui lòng chọn xã/phường.")
    Integer wardCode;

    String detailAddress;
}
