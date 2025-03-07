package org.example.board_game.core.admin.domain.dto.response.customer;

import jakarta.persistence.Tuple;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminAddressResponse {

    String id;

    String phoneNumber;

    Boolean isDefault;

    Integer districtId;

    Integer provinceId;

    Integer wardCode;

    String detailAddress;

    public AdminAddressResponse(Tuple tuple) {
        this.id = tuple.get("id",String.class);
        this.phoneNumber = tuple.get("phoneNumber",String.class);
        this.isDefault = tuple.get("isDefault",Boolean.class);
        this.districtId = tuple.get("districtId",Integer.class);
        this.provinceId = tuple.get("provinceId",Integer.class);
        this.wardCode = tuple.get("wardCode",Integer.class);
        this.detailAddress = tuple.get("detailAddress",String.class);

    }

}
