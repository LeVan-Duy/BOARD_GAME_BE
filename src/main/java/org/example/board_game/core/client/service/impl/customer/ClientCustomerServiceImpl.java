package org.example.board_game.core.client.service.impl.customer;

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
import org.example.board_game.core.client.domain.dto.request.customer.ClientAddressRequest;
import org.example.board_game.core.client.domain.dto.request.customer.ClientCustomerRequest;
import org.example.board_game.core.client.domain.dto.response.customer.ClientAddressResponse;
import org.example.board_game.core.client.domain.dto.response.customer.ClientCustomerResponse;
import org.example.board_game.core.client.domain.mapper.customer.ClientAddressMapper;
import org.example.board_game.core.client.domain.mapper.customer.ClientCustomerMapper;
import org.example.board_game.core.client.service.customer.ClientCustomerService;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.customer.Address;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.constants.MessageConstant;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.infrastructure.exception.UnauthorizedException;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClientCustomerServiceImpl implements ClientCustomerService {

    EntityService entityService;
    CustomerRepository customerRepository;
    AddressRepository addressRepository;
    EmployeeRepository employeeRepository;
    ClientCustomerMapper customerMapper = ClientCustomerMapper.INSTANCE;
    ClientAddressMapper addressMapper = ClientAddressMapper.INSTANCE;

    @Override
    public Response<ClientCustomerResponse> getProfile() {

        Customer customer = entityService.getCustomerByAuth();
        if (customer == null) {
            throw new UnauthorizedException(MessageConstant.USER_NOT_FOUND);
        }
        List<Address> addresses = addressRepository.getAllByCustomer_IdAndDeletedFalse(customer.getId());
        List<ClientAddressResponse> addressResponses = addressMapper.toResponseList(addresses);
        ClientCustomerResponse response = customerMapper.toResponse(customer);
        response.setAddressList(addressResponses);
        return Response.of(response).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<Object> updateProfile(ClientCustomerRequest request) {

        Customer customer = entityService.getCustomerByAuth();
        if (customer == null) {
            throw new UnauthorizedException(MessageConstant.CUSTOMER_NOT_FOUND);
        }
        boolean isExistEmail = customerRepository.existsByEmailAndDeletedFalseAndIdNotLike(request.getEmail(), customer.getId());
        if (isExistEmail) {
            throw new ApiException(MessageConstant.EMAIL_IS_EXISTS);
        }
        boolean isExistEmailEmp = employeeRepository.existsByEmailAndDeletedFalse(request.getEmail());
        if (isExistEmailEmp) {
            throw new ApiException(MessageConstant.EMAIL_IS_EXISTS);
        }
        customerMapper.updateCustomer(request, customer);
        customerRepository.save(customer);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> addAddress(ClientAddressRequest request) {

        Customer customer = entityService.getCustomerByAuth();
        if (customer == null) {
            throw new UnauthorizedException(MessageConstant.CUSTOMER_NOT_FOUND);
        }
        Long numberAddressByCustomer = addressRepository.countAddressesByCustomerId(customer.getId()).orElse(0L);
        if (numberAddressByCustomer >= 3) {
            throw new ApiException("Bạn chỉ được tạo tối đa 3 địa chỉ.");
        }
        boolean hasDefaultAddress = addressRepository.existsIsDefaultByCustomer(customer);
        Address address = addressMapper.toEntity(request);
        address.setIsDefault(!hasDefaultAddress);
        addressRepository.save(address);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> updateAddress(ClientAddressRequest request,String id) {

        Customer customer = entityService.getCustomerByAuth();
        if (customer == null) {
            throw new UnauthorizedException(MessageConstant.CUSTOMER_NOT_FOUND);
        }
        if (request.getIsDefault() == null) {
            throw new ApiException("Vui lòng tích chọn có phải địa chỉ mặc định hay không.");
        }
        String customerId = customer.getId();
        Address address = entityService.getAddressByIdAndCustomer(id, customerId);

        if (request.getIsDefault()) {
           entityService.updateDefaultAddressToFalse(customerId,id);
        }
        addressMapper.updateAddress(request, address);
        addressRepository.save(address);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> deleteAddress(String addressId) {

        Customer customer = entityService.getCustomerByAuth();
        if (customer == null) {
            throw new UnauthorizedException(MessageConstant.CUSTOMER_NOT_FOUND);
        }
        Address address = entityService.getAddressByIdAndCustomer(addressId, customer.getId());
        address.setDeleted(true);
        addressRepository.save(address);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> updateDefaultAddress(String addressId) {
        Customer customer = entityService.getCustomerByAuth();
        if (customer == null) {
            throw new UnauthorizedException(MessageConstant.CUSTOMER_NOT_FOUND);
        }
        Address address = entityService.getAddressByIdAndCustomer(addressId, customer.getId());
        entityService.updateDefaultAddressToFalse(customer.getId(), addressId);
        address.setIsDefault(true);
        addressRepository.save(address);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

}
