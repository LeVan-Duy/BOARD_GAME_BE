package org.example.board_game.core.admin.domain.mapper.product;


import org.example.board_game.core.admin.domain.dto.request.product.AdminProductRequest;
import org.example.board_game.core.admin.domain.dto.response.product.AdminProductResponse;
import org.example.board_game.core.common.base.BaseMapper;
import org.example.board_game.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminProductMapper extends BaseMapper<AdminProductResponse, Product, AdminProductRequest> {

    AdminProductMapper INSTANCE = Mappers.getMapper(AdminProductMapper.class);

    void updateProduct(AdminProductRequest request, @MappingTarget Product product);

}
