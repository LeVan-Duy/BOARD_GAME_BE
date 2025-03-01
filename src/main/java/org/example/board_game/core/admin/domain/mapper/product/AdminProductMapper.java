package org.example.board_game.core.admin.domain.mapper.product;


import org.example.board_game.core.admin.domain.dto.request.product.AdminProductRequest;
import org.example.board_game.core.admin.domain.dto.response.product.AdminProductResponse;
import org.example.board_game.core.common.base.BaseMapper;
import org.example.board_game.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AdminProductMapper extends BaseMapper<AdminProductResponse, Product, AdminProductRequest> {

    AdminProductMapper INSTANCE = Mappers.getMapper(AdminProductMapper.class);

    @Override
    Product toEntity(AdminProductRequest request);

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Override
    AdminProductResponse toResponse(Product entity);

    @Override
    List<Product> toEntityList(List<AdminProductRequest> requests);

    @Override
    List<AdminProductResponse> toResponseList(List<Product> entities);

    @Mapping(target = "id", ignore = true)
    void updateProduct(AdminProductRequest request, @MappingTarget Product product);

}
