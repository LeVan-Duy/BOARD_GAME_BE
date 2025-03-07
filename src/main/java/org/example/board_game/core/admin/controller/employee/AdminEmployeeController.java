package org.example.board_game.core.admin.controller.employee;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.employee.AdminEmployeeRequest;
import org.example.board_game.core.admin.domain.dto.response.employee.AdminEmployeeResponse;
import org.example.board_game.core.admin.service.employee.AdminEmployeeService;
import org.example.board_game.core.common.base.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/employee")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminEmployeeController extends BaseController<AdminEmployeeResponse, String, AdminEmployeeRequest> {

    public AdminEmployeeController(AdminEmployeeService service) {
        super(service);
    }
}
