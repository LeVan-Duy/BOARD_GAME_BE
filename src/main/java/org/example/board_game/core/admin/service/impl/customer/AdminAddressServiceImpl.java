package org.example.board_game.core.admin.service.impl.customer;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.customer.AdminAddressRequest;
import org.example.board_game.core.admin.domain.dto.response.customer.AdminAddressResponse;
import org.example.board_game.core.admin.domain.mapper.customer.AdminAddressMapper;
import org.example.board_game.core.admin.service.customer.AdminAddressService;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.customer.Address;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.repository.customer.AddressRepository;
import org.example.board_game.utils.Response;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminAddressServiceImpl implements AdminAddressService {

    EntityService entityService;
    AddressRepository addressRepository;
    AdminAddressMapper addressMapper = AdminAddressMapper.INSTANCE;

    @Override
    public Response<PageableObject<AdminAddressResponse>> findAll(AdminAddressRequest request) {
        throw new ResourceNotFoundException("API NOT SUPPORT.");
    }

    @Override
    public Response<Object> create(AdminAddressRequest request) {

        String customerId = request.getCustomerId();

        Customer customer = entityService.getCustomer(customerId);
        Long numberAddressByCustomer = addressRepository.countAddressesByCustomerId(customerId).orElse(0L);
        if (numberAddressByCustomer >= 3) {
            throw new ApiException("Không thể thêm mới vì khách hàng này đã có tối đa 3 địa chỉ.");
        }
        boolean hasDefaultAddress = addressRepository.existsIsDefaultByCustomer(customer);
        Address address = addressMapper.toEntity(request);
        address.setIsDefault(!hasDefaultAddress);
        addressRepository.save(address);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> update(AdminAddressRequest request, String id) {

        String customerId = request.getCustomerId();

        if (request.getIsDefault() == null) {
            throw new ApiException("Vui lòng tích chọn có phải địa chỉ mặc định hay không.");
        }
        Address address = entityService.getAddressByIdAndCustomer(id, customerId);
        if (request.getIsDefault()) {

            Address addressDefault = addressRepository
                    .findByCustomer_IdAndDeletedFalseAndIsDefaultTrue(customerId)
                    .orElse(null);

            if (addressDefault != null) {
                if (!Objects.equals(addressDefault.getId(), id)) {
                    addressDefault.setIsDefault(false);
                    addressRepository.save(addressDefault);
                }
            }
        }
        addressMapper.updateAddress(request, address);
        addressRepository.save(address);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<AdminAddressResponse> findById(String id) {
        Address address = entityService.getAddress(id);
        AdminAddressResponse response = addressMapper.toResponse(address);
        return Response.of(response).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<Object> delete(String id) {
        Address address = entityService.getAddress(id);
        address.setDeleted(true);
        addressRepository.save(address);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> updateDefaultAddress(String addressId) {

        Address address = entityService.getAddress(addressId);
        Address addressDefault = addressRepository
                .findByCustomer_IdAndDeletedFalseAndIsDefaultTrue(address.getCustomer().getId())
                .orElse(null);
        if (addressDefault != null) {
            addressDefault.setIsDefault(false);
            addressRepository.save(addressDefault);
        }
        address.setIsDefault(true);
        addressRepository.save(address);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }
}
