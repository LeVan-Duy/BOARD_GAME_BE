package org.example.board_game.core.admin.domain.mapper.order;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminOrderMapper  {
    AdminOrderMapper INSTANCE = Mappers.getMapper(AdminOrderMapper.class);

}
