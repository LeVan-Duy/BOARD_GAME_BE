package org.example.board_game.core.admin.domain.mapper.employee;


import org.example.board_game.core.admin.domain.dto.request.employee.AdminEmployeeRequest;
import org.example.board_game.core.admin.domain.dto.response.employee.AdminEmployeeResponse;
import org.example.board_game.core.common.base.BaseMapper;
import org.example.board_game.entity.employee.Employee;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminEmployeeMapper extends BaseMapper<AdminEmployeeResponse, Employee, AdminEmployeeRequest> {

    @Override
    AdminEmployeeResponse toResponse(Employee entity);

    AdminEmployeeMapper INSTANCE = Mappers.getMapper(AdminEmployeeMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEmployee(AdminEmployeeRequest request, @MappingTarget Employee employee);

    @AfterMapping
    default void setPasswordToEmpty(@MappingTarget AdminEmployeeResponse response) {
        response.setPassword("");
    }
}
