package org.example.board_game.core.admin.controller.customer;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.customer.AdminCustomerRequest;
import org.example.board_game.core.admin.domain.dto.response.customer.AdminCustomerResponse;
import org.example.board_game.core.admin.service.customer.AdminCustomerService;
import org.example.board_game.core.common.base.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/customer")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCustomerController extends BaseController<AdminCustomerResponse, String, AdminCustomerRequest> {

    public AdminCustomerController(AdminCustomerService service) {
        super(service);
    }
}
