package org.example.board_game.core.client.domain.mapper.product;


import org.example.board_game.core.client.domain.dto.request.product.ClientProductRequest;
import org.example.board_game.core.client.domain.dto.response.product.ClientProductResponse;
import org.example.board_game.core.common.base.BaseMapper;
import org.example.board_game.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ClientProductMapper extends BaseMapper<ClientProductResponse, Product, ClientProductRequest> {

    ClientProductMapper INSTANCE = Mappers.getMapper(ClientProductMapper.class);

    @Override
    Product toEntity(ClientProductRequest request);

    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "publisher", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Override
    ClientProductResponse toResponse(Product entity);

    @Override
    List<Product> toEntityList(List<ClientProductRequest> requests);

    @Override
    List<ClientProductResponse> toResponseList(List<Product> entities);

}
