package org.example.board_game.core.admin.service.impl.customer;

import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.customer.AdminAddressRequest;
import org.example.board_game.core.admin.domain.dto.request.customer.AdminCustomerRequest;
import org.example.board_game.core.admin.domain.dto.response.customer.AdminAddressResponse;
import org.example.board_game.core.admin.domain.dto.response.customer.AdminCustomerResponse;
import org.example.board_game.core.admin.domain.mapper.customer.AdminAddressMapper;
import org.example.board_game.core.admin.domain.mapper.customer.AdminCustomerMapper;
import org.example.board_game.core.admin.service.customer.AdminCustomerService;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.customer.Address;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.constants.MessageConstant;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.repository.customer.AddressRepository;
import org.example.board_game.repository.customer.CustomerRepository;
import org.example.board_game.repository.employee.EmployeeRepository;
import org.example.board_game.utils.CollectionUtils;
import org.example.board_game.utils.PaginationUtil;
import org.example.board_game.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCustomerServiceImpl implements AdminCustomerService {

    EntityService entityService;
    PasswordEncoder passwordEncoder;
    CustomerRepository customerRepository;
    EmployeeRepository employeeRepository;
    AddressRepository addressRepository;
    AdminCustomerMapper customerMapper = AdminCustomerMapper.INSTANCE;
    AdminAddressMapper addressMapper = AdminAddressMapper.INSTANCE;

    @Override
    public Response<PageableObject<AdminCustomerResponse>> findAll(AdminCustomerRequest request) {

        Pageable pageable = PaginationUtil.pageable(request);
        Page<Tuple> page = customerRepository.findAllCustomer(request, request.getStatus(), pageable);
        List<String> customerIds = CollectionUtils.extractField(page.getContent(), tuple -> tuple.get("id", String.class));
        List<Tuple> addressList = addressRepository.getAllByCustomerIds(customerIds);
        Map<String, List<Tuple>> groupByCustomerId = CollectionUtils.group(addressList, tuple -> tuple.get("customerId", String.class));

        List<AdminCustomerResponse> responses = page.getContent().stream().map(tuple -> {
            AdminCustomerResponse response = new AdminCustomerResponse(tuple);
            String customerId = tuple.get("id", String.class);
            List<Tuple> address = groupByCustomerId.getOrDefault(customerId, Collections.emptyList());
            List<AdminAddressResponse> addressResponses = address.stream().map(AdminAddressResponse::new).toList();
            response.setAddressList(addressResponses);
            return response;
        }).toList();
        Page<AdminCustomerResponse> resPage = new PageImpl<>(responses, pageable, page.getTotalElements());
        PageableObject<AdminCustomerResponse> pageableObject = new PageableObject<>(resPage);
        return Response.of(pageableObject).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<Object> create(AdminCustomerRequest request) {

        boolean isExistEmailCustomer = customerRepository.existsByEmailAndDeletedFalse(request.getEmail());
        if (isExistEmailCustomer) {
            throw new ApiException(MessageConstant.EMAIL_IS_EXISTS);
        }
        boolean isExistEmailEmp = employeeRepository.existsByEmailAndDeletedFalse(request.getEmail());
        if (isExistEmailEmp) {
            throw new ApiException(MessageConstant.EMAIL_IS_EXISTS);
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        Customer customer = customerRepository.save(customerMapper.toEntity(request));
        AdminAddressRequest addressRequest = request.getAddress();
        if (addressRequest != null) {
            if (addressRequest.getProvinceId() == null || addressRequest.getDistrictId() == null || addressRequest.getWardCode() == null) {
                throw new ApiException("Vui lòng nhập đầy đủ thông tin địa chỉ.");
            }
            Address address = addressMapper.toEntity(request.getAddress());
            address.setCustomer(customer);
            address.setIsDefault(true);
            addressRepository.save(address);
        }
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> update(AdminCustomerRequest request, String id) {

        boolean isExistEmailCustomer = customerRepository.existsByEmailAndDeletedFalseAndIdNotLike(request.getEmail(),id);
        if (isExistEmailCustomer) {
            throw new ApiException(MessageConstant.EMAIL_IS_EXISTS);
        }
        boolean isExistEmailEmp = employeeRepository.existsByEmailAndDeletedFalse(request.getEmail());
        if (isExistEmailEmp) {
            throw new ApiException(MessageConstant.EMAIL_IS_EXISTS);
        }
        Customer customer = entityService.getCustomer(id);
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        customerMapper.updateCustomer(request, customer);
        customerRepository.save(customer);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<AdminCustomerResponse> findById(String id) {
        Customer customer = entityService.getCustomer(id);
        AdminCustomerResponse response = customerMapper.toResponse(customer);
        return Response.of(response).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<Object> delete(String id) {
        Customer customer = entityService.getCustomer(id);
        customer.setDeleted(true);
        customerRepository.save(customer);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }
}
