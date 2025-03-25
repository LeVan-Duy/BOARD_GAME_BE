package org.example.board_game.core.client.controller.product;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.client.domain.dto.request.product.ClientBestSellerRequest;
import org.example.board_game.core.client.domain.dto.request.product.ClientProductRequest;
import org.example.board_game.core.client.domain.dto.response.product.ClientProductResponse;
import org.example.board_game.core.client.service.product.ClientProductService;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.utils.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/client/product")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClientProductController {

    ClientProductService productService;

    @GetMapping("/get-all")
    public Response<PageableObject<ClientProductResponse>> getAll(ClientProductRequest request) {
        return productService.getAll(request);
    }

    @GetMapping("/top-seller")
    public Response<List<ClientProductResponse>> getTopSeller(ClientBestSellerRequest request) {
        return productService.getBestSeller(request);
    }

    @GetMapping("/top-new")
    public Response<List<ClientProductResponse>> getTopNew() {
        return productService.getNewProducts();
    }
}
