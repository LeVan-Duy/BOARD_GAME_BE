package org.example.board_game.core.admin.controller.product;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.product.AdminPublisherRequest;
import org.example.board_game.core.admin.domain.dto.response.product.AdminPublisherResponse;
import org.example.board_game.core.admin.service.product.AdminPublisherService;
import org.example.board_game.core.common.base.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/publisher")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminPublisherController extends BaseController<AdminPublisherResponse, String, AdminPublisherRequest> {

    public AdminPublisherController(AdminPublisherService service) {
        super(service);
    }
}
