package org.example.board_game.core.admin.domain.mapper.employee;


import org.example.board_game.core.admin.domain.dto.request.employee.AdminEmployeeRequest;
import org.example.board_game.core.admin.domain.dto.request.product.AdminProductRequest;
import org.example.board_game.core.admin.domain.dto.response.employee.AdminEmployeeResponse;
import org.example.board_game.core.common.base.BaseMapper;
import org.example.board_game.entity.employee.Employee;
import org.example.board_game.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminEmployeeMapper extends BaseMapper<AdminEmployeeResponse, Employee, AdminEmployeeRequest> {
    AdminEmployeeMapper INSTANCE = Mappers.getMapper(AdminEmployeeMapper.class);

    @Mapping(target = "id", ignore = true)
    void updateEmployee(AdminEmployeeRequest request, @MappingTarget Employee employee);
}
