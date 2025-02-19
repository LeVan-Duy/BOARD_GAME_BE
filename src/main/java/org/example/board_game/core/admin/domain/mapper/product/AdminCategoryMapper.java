package org.example.board_game.core.admin.domain.mapper.product;


import org.example.board_game.core.admin.domain.dto.request.product.AdminCategoryRequest;
import org.example.board_game.core.admin.domain.dto.response.product.AdminCategoryResponse;
import org.example.board_game.core.common.base.BaseMapper;
import org.example.board_game.entity.product.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminCategoryMapper extends BaseMapper<AdminCategoryResponse, Category, AdminCategoryRequest> {

    AdminCategoryMapper INSTANCE = Mappers.getMapper(AdminCategoryMapper.class);

}
