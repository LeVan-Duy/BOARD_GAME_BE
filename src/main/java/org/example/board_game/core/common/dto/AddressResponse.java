package org.example.board_game.core.common.dto;

import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {

    String id;

    String phoneNumber;

    Integer districtId;

    Integer provinceId;

    Integer wardCode;

    String detailAddress;

}
