package org.example.board_game.core.client.domain.mapper.customer;

import org.example.board_game.core.client.domain.dto.request.customer.ClientAddressRequest;
import org.example.board_game.core.client.domain.dto.response.customer.ClientAddressResponse;
import org.example.board_game.core.common.base.BaseMapper;
import org.example.board_game.entity.customer.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientAddressMapper extends BaseMapper<ClientAddressResponse, Address, ClientAddressRequest> {

    ClientAddressMapper INSTANCE = Mappers.getMapper(ClientAddressMapper.class);

    @Mapping(target = "id", ignore = true)
    void updateAddress(ClientAddressRequest request, @MappingTarget Address address);

}
