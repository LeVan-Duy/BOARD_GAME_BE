package org.example.board_game.core.admin.controller.product;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.product.AdminCategoryRequest;
import org.example.board_game.core.admin.domain.dto.response.product.AdminCategoryResponse;
import org.example.board_game.core.admin.service.product.AdminCategoryService;
import org.example.board_game.core.common.base.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/category")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCategoryController extends BaseController<AdminCategoryResponse, String, AdminCategoryRequest> {

    public AdminCategoryController(AdminCategoryService service) {
        super(service);
    }
}
