package org.example.board_game.core.admin.service.product;

import org.example.board_game.core.admin.domain.dto.request.product.AdminProductMediaRequest;
import org.example.board_game.core.admin.domain.dto.request.product.AdminUploadMainImageRequest;
import org.example.board_game.utils.Response;

public interface AdminProductMediaService {

    Response<Object> addImages(AdminProductMediaRequest request);

    Response<Object> delete(AdminProductMediaRequest request);

    Response<Object> updateMainImage(AdminUploadMainImageRequest request);
}
