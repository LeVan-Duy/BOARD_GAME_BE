package org.example.board_game.core.client.controller.voucher;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.client.domain.dto.request.voucher.ClientVoucherRequest;
import org.example.board_game.core.client.domain.dto.response.voucher.ClientVoucherResponse;
import org.example.board_game.core.client.service.voucher.ClientVoucherService;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.utils.Response;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client/voucher")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClientVoucherController {

    ClientVoucherService voucherService;

    @GetMapping("/get-all-by-constraint")
    public Response<PageableObject<ClientVoucherResponse>> getAllByConstraint(ClientVoucherRequest request) {
        return voucherService.findAll(request);
    }
}
