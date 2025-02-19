package org.example.board_game.core.admin.domain.mapper.product;


import org.example.board_game.core.admin.domain.dto.request.product.AdminAuthorRequest;
import org.example.board_game.core.admin.domain.dto.response.product.AdminAuthorResponse;
import org.example.board_game.core.common.base.BaseMapper;
import org.example.board_game.entity.product.Author;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminAuthorMapper extends BaseMapper<AdminAuthorResponse, Author, AdminAuthorRequest> {

    AdminAuthorMapper INSTANCE = Mappers.getMapper(AdminAuthorMapper.class);

}
