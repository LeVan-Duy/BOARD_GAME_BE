package org.example.board_game.core.client.controller.cart;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.client.domain.dto.request.cart.ClientAddProductToCartRequest;
import org.example.board_game.core.client.domain.dto.response.cart.ClientProductsInCartResponse;
import org.example.board_game.core.client.service.cart.ClientCartService;
import org.example.board_game.core.common.base.BaseListIdRequest;
import org.example.board_game.core.common.base.BaseRequest;
import org.example.board_game.utils.Response;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/client/cart")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClientCartController {

    ClientCartService cartService;

    @GetMapping("/get-all")
    public Response<List<ClientProductsInCartResponse>> getAllProductsInCart(BaseRequest request) {
        return cartService.productsInCart(request);
    }

    @PostMapping("/add")
    public Response<Object> addProductToCart(@RequestBody @Valid ClientAddProductToCartRequest request) {
        return cartService.addProductToCart(request);
    }

    @PostMapping("/update")
    public Response<Object> updateQuantity(@RequestBody ClientAddProductToCartRequest request) {
        return cartService.updateQuantity(request);
    }

    @PostMapping("/remove/{id}")
    public Response<Object> removeProductInCart(@PathVariable String id) {
        return cartService.removeProductInCart(id);
    }

    @PostMapping("/remove-all")
    public Response<Object> removeProductsInCart(@RequestBody BaseListIdRequest request) {
        return cartService.removeProductsInCart(request);
    }

}
