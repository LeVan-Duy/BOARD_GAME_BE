package org.example.board_game.core.client.controller.customer;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.client.domain.dto.request.customer.ClientAddressRequest;
import org.example.board_game.core.client.domain.dto.request.customer.ClientCustomerRequest;
import org.example.board_game.core.client.domain.dto.response.customer.ClientCustomerResponse;
import org.example.board_game.core.client.service.customer.ClientCustomerService;
import org.example.board_game.utils.Response;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client/customer")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClientCustomerController  {

    ClientCustomerService clientCustomerService;

    @GetMapping("/profile")
    public Response<ClientCustomerResponse> getProfile() {
        return clientCustomerService.getProfile();
    }

    @PostMapping("/update-profile")
    public Response<Object> updateProfile(@RequestBody @Valid ClientCustomerRequest request) {
        return clientCustomerService.updateProfile(request);
    }

    @PostMapping("/update-default/{id}")
    public Response<Object> updateDefaultAddress(@PathVariable("id") String id) {
        return clientCustomerService.updateDefaultAddress(id);
    }

    @PostMapping("/add-address")
    public Response<Object> addAddress(@RequestBody ClientAddressRequest request) {
        return clientCustomerService.addAddress(request);
    }

    @PostMapping("/update-address/{id}")
    public Response<Object> updateAddress(@PathVariable("id") String id,@Valid @RequestBody ClientAddressRequest request) {
        return clientCustomerService.updateAddress(request,id);
    }

    @PostMapping("/delete-address/{id}")
    public Response<Object> deletedAddress(@PathVariable("id") String id) {
        return clientCustomerService.deleteAddress(id);
    }
}
