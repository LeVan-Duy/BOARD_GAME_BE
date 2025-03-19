package org.example.board_game.core.admin.domain.mapper.customer;

import org.example.board_game.core.admin.domain.dto.request.customer.AdminCustomerRequest;
import org.example.board_game.core.admin.domain.dto.response.customer.AdminCustomerResponse;
import org.example.board_game.core.auth.dto.request.RegisterCustomerRequest;
import org.example.board_game.core.common.base.BaseMapper;
import org.example.board_game.entity.customer.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminCustomerMapper extends BaseMapper<AdminCustomerResponse, Customer, AdminCustomerRequest> {

    AdminCustomerMapper INSTANCE = Mappers.getMapper(AdminCustomerMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateCustomer(AdminCustomerRequest request, @MappingTarget Customer employee);

    Customer byRegisterRequest(RegisterCustomerRequest request);
}
