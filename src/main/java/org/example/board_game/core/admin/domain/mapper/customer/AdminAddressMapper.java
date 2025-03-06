package org.example.board_game.core.admin.domain.mapper.customer;

import org.example.board_game.core.admin.domain.dto.request.customer.AdminAddressRequest;
import org.example.board_game.core.admin.domain.dto.request.customer.AdminCustomerRequest;
import org.example.board_game.core.admin.domain.dto.response.customer.AdminAddressResponse;
import org.example.board_game.core.admin.domain.dto.response.customer.AdminCustomerResponse;
import org.example.board_game.core.auth.dto.request.RegisterCustomerRequest;
import org.example.board_game.core.common.base.BaseMapper;
import org.example.board_game.entity.customer.Address;
import org.example.board_game.entity.customer.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminAddressMapper extends BaseMapper<AdminAddressResponse, Address, AdminAddressRequest> {

    AdminAddressMapper INSTANCE = Mappers.getMapper(AdminAddressMapper.class);

    @Mapping(target = "id", ignore = true)
    void updateAddress(AdminAddressRequest request, @MappingTarget Address address);

}
