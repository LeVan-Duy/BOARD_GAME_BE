package org.example.board_game.core.client.service.impl.voucher;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.client.domain.dto.request.voucher.ClientVoucherRequest;
import org.example.board_game.core.client.domain.dto.response.voucher.ClientVoucherResponse;
import org.example.board_game.core.client.domain.mapper.voucher.ClientVoucherMapper;
import org.example.board_game.core.client.service.voucher.ClientVoucherService;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.entity.voucher.Voucher;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.repository.voucher.VoucherRepository;
import org.example.board_game.utils.PaginationUtil;
import org.example.board_game.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClientVoucherServiceImpl implements ClientVoucherService {

    VoucherRepository voucherRepository;
    ClientVoucherMapper voucherMapper = ClientVoucherMapper.INSTANCE;

    @Override
    public Response<PageableObject<ClientVoucherResponse>> findAll(ClientVoucherRequest request) {
        List<ClientVoucherResponse> responses;
        Pageable pageable = PaginationUtil.pageable(request);
        Page<Voucher> page = voucherRepository.findAllVoucherForClient(request, pageable, request.getType());
        Float constraint = request.getConstraint();
        if (constraint == null) {
            responses = new ArrayList<>();
        } else {
            responses = page.getContent().stream().map(voucher -> {
                ClientVoucherResponse response = voucherMapper.toResponse(voucher);
                response.setAllow(constraint >= voucher.getConstraint());
                return response;
            }).toList();
        }
        Page<ClientVoucherResponse> resPage = new PageImpl<>(responses, pageable, page.getTotalElements());
        PageableObject<ClientVoucherResponse> pageableObject = new PageableObject<>(resPage);
        return Response.of(pageableObject).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

}
