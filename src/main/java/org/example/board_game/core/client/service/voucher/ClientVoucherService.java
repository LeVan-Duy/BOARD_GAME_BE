package org.example.board_game.core.client.service.voucher;

import org.example.board_game.core.admin.domain.dto.request.voucher.AdminVoucherRequest;
import org.example.board_game.core.admin.domain.dto.response.voucher.AdminVoucherResponse;
import org.example.board_game.core.common.base.BaseService;
import org.example.board_game.utils.Response;

public interface ClientVoucherService extends BaseService<AdminVoucherResponse, String, AdminVoucherRequest> {

    Response<Object> deactivateVoucher(String voucherId);
}
