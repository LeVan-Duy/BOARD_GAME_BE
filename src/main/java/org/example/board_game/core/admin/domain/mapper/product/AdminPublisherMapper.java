package org.example.board_game.core.admin.domain.mapper.product;


import org.example.board_game.core.admin.domain.dto.request.product.AdminPublisherRequest;
import org.example.board_game.core.admin.domain.dto.response.product.AdminPublisherResponse;
import org.example.board_game.core.common.base.BaseMapper;
import org.example.board_game.entity.product.Publisher;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminPublisherMapper extends BaseMapper<AdminPublisherResponse, Publisher, AdminPublisherRequest> {

    AdminPublisherMapper INSTANCE = Mappers.getMapper(AdminPublisherMapper.class);

}
