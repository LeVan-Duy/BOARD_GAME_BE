package org.example.board_game.core.client.domain.mapper.customer;

import org.example.board_game.core.auth.dto.request.RegisterCustomerRequest;
import org.example.board_game.core.client.domain.dto.request.customer.ClientCustomerRequest;
import org.example.board_game.core.client.domain.dto.response.customer.ClientCustomerResponse;
import org.example.board_game.core.common.base.BaseMapper;
import org.example.board_game.entity.customer.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ClientCustomerMapper extends BaseMapper<ClientCustomerResponse, Customer, ClientCustomerRequest> {

    ClientCustomerMapper INSTANCE = Mappers.getMapper(ClientCustomerMapper.class);

    @Mapping(target = "id", ignore = true)
    void updateCustomer(ClientCustomerRequest request, @MappingTarget Customer customer);
}
