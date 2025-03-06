package org.example.board_game.core.admin.domain.dto.request.voucher;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.common.base.BaseRequest;
import org.example.board_game.infrastructure.enums.VoucherStatus;
import org.example.board_game.infrastructure.enums.VoucherType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminVoucherRequest extends BaseRequest {

    String code;

    VoucherStatus status;

    @NotNull(message = "Vui lòng chọn loại voucher.")
    VoucherType type;

    float value;

    float constraint;

    int quantity;

    @NotNull(message = "Vui lòng nhập ngày bắt đầu voucher.")
    Long startDate;

    @NotNull(message = "Vui lòng nhập ngày kết thúc voucher.")
    Long endDate;

    String image;

    // filter
    Float constraintMin;
    Float constraintMax;

    Float valueMin;
    Float valueMax;

    Integer quantityMin;
    Integer quantityMax;
}
