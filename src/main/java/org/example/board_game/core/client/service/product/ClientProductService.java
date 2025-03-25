package org.example.board_game.core.client.service.product;

import org.example.board_game.core.client.domain.dto.request.product.ClientBestSellerRequest;
import org.example.board_game.core.client.domain.dto.request.product.ClientProductRequest;
import org.example.board_game.core.client.domain.dto.response.product.ClientProductResponse;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.utils.Response;

import java.util.List;

public interface ClientProductService {

    Response<PageableObject<ClientProductResponse>> getAll(ClientProductRequest request);

    Response<List<ClientProductResponse>> getBestSeller(ClientBestSellerRequest request);

    Response<List<ClientProductResponse>> getNewProducts();

}
