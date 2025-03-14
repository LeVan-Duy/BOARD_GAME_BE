package org.example.board_game.core.client.domain.dto.response.voucher;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.common.base.BaseResponse;
import org.example.board_game.infrastructure.enums.VoucherStatus;
import org.example.board_game.infrastructure.enums.VoucherType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientVoucherResponse extends BaseResponse {

    String code;

    VoucherType type;

    float value;

    float constraint;

    int quantity;

    Long startDate;

    Long endDate;

    String image;

    VoucherStatus status;
}
