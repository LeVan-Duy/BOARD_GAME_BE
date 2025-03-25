package org.example.board_game.core.client.service.voucher;

import org.example.board_game.core.client.domain.dto.request.voucher.ClientVoucherRequest;
import org.example.board_game.core.client.domain.dto.response.voucher.ClientVoucherResponse;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.utils.Response;

public interface ClientVoucherService {

    Response<PageableObject<ClientVoucherResponse>> findAll(ClientVoucherRequest request);
}
