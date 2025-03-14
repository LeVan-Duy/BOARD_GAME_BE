package org.example.board_game.core.admin.service.impl.employee;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.employee.AdminEmployeeRequest;
import org.example.board_game.core.admin.domain.dto.response.employee.AdminEmployeeResponse;
import org.example.board_game.core.admin.domain.dto.response.product.AdminAuthorResponse;
import org.example.board_game.core.admin.domain.mapper.employee.AdminEmployeeMapper;
import org.example.board_game.core.admin.service.employee.AdminEmployeeService;
import org.example.board_game.core.admin.service.order.AdminOrderService;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.employee.Employee;
import org.example.board_game.entity.product.Author;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.constants.MessageConstant;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.repository.customer.CustomerRepository;
import org.example.board_game.repository.employee.EmployeeRepository;
import org.example.board_game.utils.PaginationUtil;
import org.example.board_game.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminEmployeeServiceImpl implements AdminEmployeeService {

    PasswordEncoder passwordEncoder;
    EmployeeRepository employeeRepository;
    CustomerRepository customerRepository;
    EntityService entityService;
    AdminEmployeeMapper employeeMapper = AdminEmployeeMapper.INSTANCE;

    @Override
    public Response<PageableObject<AdminEmployeeResponse>> findAll(AdminEmployeeRequest request) {
        Pageable pageable = PaginationUtil.pageable(request);
        Page<Employee> page = employeeRepository.findAllEmployee(request, request.getStatus(), pageable);
        Page<AdminEmployeeResponse> resPage = page.map(employeeMapper::toResponse);
        PageableObject<AdminEmployeeResponse> pageableObject = new PageableObject<>(resPage);
        return Response.of(pageableObject).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<Object> create(AdminEmployeeRequest request) {

        boolean isExistEmailCustomer = customerRepository.existsByEmailAndDeletedFalse(request.getEmail());
        if (isExistEmailCustomer) {
            throw new ApiException(MessageConstant.EMAIL_IS_EXISTS);
        }
        boolean isExistEmailEmp = employeeRepository.existsByEmailAndDeletedFalse(request.getEmail());
        if (isExistEmailEmp) {
            throw new ApiException(MessageConstant.EMAIL_IS_EXISTS);
        }
        String password = passwordEncoder.encode(request.getPassword());
        request.setPassword(password);
        Employee employee = employeeMapper.toEntity(request);
        employeeRepository.save(employee);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> update(AdminEmployeeRequest request, String id) {

        boolean isExistEmailCustomer = customerRepository.existsByEmailAndDeletedFalse(request.getEmail());
        if (isExistEmailCustomer) {
            throw new ApiException(MessageConstant.EMAIL_IS_EXISTS);
        }
        boolean isExistEmailEmp = employeeRepository.existsByEmailAndDeletedFalseAndIdNotLike(request.getEmail(), id);
        if (isExistEmailEmp) {
            throw new ApiException(MessageConstant.EMAIL_IS_EXISTS);
        }
        Employee employee = entityService.getEmployee(id);
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        employeeMapper.updateEmployee(request, employee);
        employeeRepository.save(employee);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<AdminEmployeeResponse> findById(String id) {
        Employee employee = entityService.getEmployee(id);
        AdminEmployeeResponse response = employeeMapper.toResponse(employee);
        return Response.of(response);
    }

    @Override
    public Response<Object> delete(String id) {
        Employee employee = entityService.getEmployee(id);
        employee.setDeleted(true);
        employeeRepository.save(employee);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }
}
