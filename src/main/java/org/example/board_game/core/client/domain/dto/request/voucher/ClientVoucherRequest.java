package org.example.board_game.core.client.domain.dto.request.voucher;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.common.base.BaseRequest;
import org.example.board_game.infrastructure.enums.VoucherType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientVoucherRequest extends BaseRequest {

    String code;

    VoucherType type;

    float value;

    Float constraint;

    Long startDate;

    Long endDate;

    Float constraintMin;
    Float constraintMax;

    Float valueMin;
    Float valueMax;

    Integer quantityMin;
    Integer quantityMax;
}
