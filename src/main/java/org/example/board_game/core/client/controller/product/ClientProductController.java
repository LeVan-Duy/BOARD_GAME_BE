package org.example.board_game.core.client.controller.product;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.product.AdminProductRequest;
import org.example.board_game.core.admin.domain.dto.response.product.AdminProductResponse;
import org.example.board_game.core.admin.service.product.AdminProductService;
import org.example.board_game.core.common.base.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/product")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClientProductController extends BaseController<AdminProductResponse, String, AdminProductRequest> {

    public ClientProductController(AdminProductService service) {
        super(service);
    }
}
