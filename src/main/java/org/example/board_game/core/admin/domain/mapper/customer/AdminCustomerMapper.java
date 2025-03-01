package org.example.board_game.core.admin.domain.mapper.customer;

import org.example.board_game.core.auth.dto.request.RegisterCustomerRequest;
import org.example.board_game.entity.customer.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminCustomerMapper {

    AdminCustomerMapper INSTANCE = Mappers.getMapper(AdminCustomerMapper.class);

    Customer byRegisterRequest(RegisterCustomerRequest request);
}
