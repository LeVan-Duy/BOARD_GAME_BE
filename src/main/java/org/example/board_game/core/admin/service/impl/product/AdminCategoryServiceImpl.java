package org.example.board_game.core.admin.service.impl.product;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.product.AdminCategoryRequest;
import org.example.board_game.core.admin.domain.dto.response.product.AdminCategoryResponse;
import org.example.board_game.core.admin.service.product.AdminCategoryService;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.repository.product.CategoryRepository;
import org.example.board_game.utils.Response;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    CategoryRepository categoryRepository;

    @Override
    public Response<PageableObject<AdminCategoryResponse>> findAll(AdminCategoryRequest request) {
        return null;
    }

    @Override
    public Response<Object> create(AdminCategoryRequest request) {
        return null;
    }

    @Override
    public Response<Object> update(AdminCategoryRequest request, String id) {
        return null;
    }

    @Override
    public Response<AdminCategoryResponse> findById(String id) {
        return null;
    }

    @Override
    public Response<Object> delete(String id) {
        return null;
    }
}
