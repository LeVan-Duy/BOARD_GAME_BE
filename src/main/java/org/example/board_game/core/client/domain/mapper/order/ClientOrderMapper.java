package org.example.board_game.core.client.domain.mapper.order;


import org.example.board_game.core.client.domain.dto.request.order.ClientOrderRequest;
import org.example.board_game.entity.order.Order;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientOrderMapper {

    ClientOrderMapper INSTANCE = Mappers.getMapper(ClientOrderMapper.class);

    Order toEntity(ClientOrderRequest request);
}
