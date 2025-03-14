package org.example.board_game.core.client.service.cart;

import org.example.board_game.core.client.domain.dto.request.cart.ClientAddProductToCartRequest;
import org.example.board_game.core.client.domain.dto.response.cart.ClientProductsInCartResponse;
import org.example.board_game.core.common.base.BaseRequest;
import org.example.board_game.utils.Response;

import java.util.List;

public interface ClientCartService {

    Response<List<ClientProductsInCartResponse>> productsInCart(BaseRequest request);

    Response<Object> addProductToCart(ClientAddProductToCartRequest request);
}
