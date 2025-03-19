package org.example.board_game.core.client.domain.dto.response.customer;

import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientAddressResponse {

    String id;

    String phoneNumber;

    Boolean isDefault;

    Integer districtId;

    Integer provinceId;

    Integer wardCode;

    String detailAddress;

    public ClientAddressResponse(Tuple tuple) {
        this.id = tuple.get("id",String.class);
        this.phoneNumber = tuple.get("phoneNumber",String.class);
        this.isDefault = tuple.get("isDefault",Boolean.class);
        this.districtId = tuple.get("districtId",Integer.class);
        this.provinceId = tuple.get("provinceId",Integer.class);
        this.wardCode = tuple.get("wardCode",Integer.class);
        this.detailAddress = tuple.get("detailAddress",String.class);

    }

}
