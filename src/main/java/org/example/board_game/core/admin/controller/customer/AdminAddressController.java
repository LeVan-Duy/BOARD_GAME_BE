package org.example.board_game.core.admin.controller.customer;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.customer.AdminAddressRequest;
import org.example.board_game.core.admin.domain.dto.response.customer.AdminAddressResponse;
import org.example.board_game.core.admin.service.customer.AdminAddressService;
import org.example.board_game.core.common.base.BaseController;
import org.example.board_game.utils.Response;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/address")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminAddressController extends BaseController<AdminAddressResponse, String, AdminAddressRequest> {

    AdminAddressService adminAddressService;

    public AdminAddressController(AdminAddressService service) {
        super(service);
        this.adminAddressService = service;
    }

    @PostMapping("/update-default/{id}")
    public Response<Object> updateDefaultAddress(@PathVariable("id") String id) {
        return adminAddressService.updateDefaultAddress(id);
    }

    @GetMapping("/get-all-by-customerId/{id}")
    public Response<List<AdminAddressResponse>> getAllByCustomer(@PathVariable("id") String id) {
        return adminAddressService.getAllByCustomerId(id);
    }

}
