package org.example.board_game.core.admin.controller.product;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.product.AdminAuthorRequest;
import org.example.board_game.core.admin.domain.dto.response.product.AdminAuthorResponse;
import org.example.board_game.core.admin.service.product.AdminAuthorService;
import org.example.board_game.core.common.base.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/author")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminAuthorController extends BaseController<AdminAuthorResponse, String, AdminAuthorRequest> {

    public AdminAuthorController(AdminAuthorService service) {
        super(service);
    }
}
