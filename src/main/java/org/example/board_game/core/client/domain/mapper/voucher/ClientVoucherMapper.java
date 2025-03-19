package org.example.board_game.core.client.domain.mapper.voucher;

import org.example.board_game.core.client.domain.dto.request.voucher.ClientVoucherRequest;
import org.example.board_game.core.client.domain.dto.response.voucher.ClientVoucherResponse;
import org.example.board_game.core.common.base.BaseMapper;
import org.example.board_game.entity.voucher.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientVoucherMapper extends BaseMapper<ClientVoucherResponse, Voucher, ClientVoucherRequest> {

    ClientVoucherMapper INSTANCE = Mappers.getMapper(ClientVoucherMapper.class);

    @Mapping(target = "id", ignore = true)
    void updateVoucher(ClientVoucherRequest request, @MappingTarget Voucher voucher);
}
