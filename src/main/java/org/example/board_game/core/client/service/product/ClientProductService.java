package org.example.board_game.core.client.service.product;

import org.example.board_game.core.admin.domain.dto.request.product.AdminProductRequest;
import org.example.board_game.core.admin.domain.dto.response.product.AdminProductResponse;
import org.example.board_game.core.common.base.BaseService;

public interface ClientProductService extends BaseService<AdminProductResponse, String, AdminProductRequest> {
}
