package org.example.board_game.core.admin.domain.mapper.voucher;

import org.example.board_game.core.admin.domain.dto.request.voucher.AdminVoucherRequest;
import org.example.board_game.core.admin.domain.dto.response.voucher.AdminVoucherResponse;
import org.example.board_game.core.common.base.BaseMapper;
import org.example.board_game.entity.voucher.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminVoucherMapper extends BaseMapper<AdminVoucherResponse, Voucher, AdminVoucherRequest> {

    AdminVoucherMapper INSTANCE = Mappers.getMapper(AdminVoucherMapper.class);

    @Mapping(target = "id", ignore = true)
    void updateVoucher(AdminVoucherRequest request, @MappingTarget Voucher voucher);
}
