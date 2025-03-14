package org.example.board_game.core.client.domain.mapper.cart;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientCartMapper {

    ClientCartMapper INSTANCE = Mappers.getMapper(ClientCartMapper.class);

}
