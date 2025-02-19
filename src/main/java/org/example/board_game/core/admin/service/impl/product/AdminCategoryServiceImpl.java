package org.example.board_game.core.admin.service.impl.product;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.product.AdminCategoryRequest;
import org.example.board_game.core.admin.domain.dto.response.product.AdminCategoryResponse;
import org.example.board_game.core.admin.domain.mapper.product.AdminCategoryMapper;
import org.example.board_game.core.admin.service.product.AdminCategoryService;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.product.Category;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.repository.product.CategoryRepository;
import org.example.board_game.utils.PaginationUtil;
import org.example.board_game.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    CategoryRepository categoryRepository;
    EntityService entityService;
    AdminCategoryMapper categoryMapper = AdminCategoryMapper.INSTANCE;

    @Override
    public Response<PageableObject<AdminCategoryResponse>> findAll(AdminCategoryRequest request) {
        Pageable pageable = PaginationUtil.pageable(request);
        Page<Category> page = categoryRepository.findAllCategory(request, pageable);
        Page<AdminCategoryResponse> resPage = page.map(categoryMapper::toResponse);
        PageableObject<AdminCategoryResponse> pageableObject = new PageableObject<>(resPage);
        return Response.of(pageableObject).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<Object> create(AdminCategoryRequest request) {
        boolean isNameExist = categoryRepository.existsByNameAndDeletedFalse(request.getName());
        if (isNameExist) {
            throw new ApiException("Category name already exist.");
        }
        Category category = categoryMapper.toEntity(request);
        categoryRepository.save(category);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> update(AdminCategoryRequest request, String id) {

        boolean checkCategoryExists = categoryRepository.existsById(id);
        if (!checkCategoryExists) {
            throw new ResourceNotFoundException("Category not found.");
        }
        boolean isNameExist = categoryRepository.existsByNameAndDeletedFalseAndIdNotLike(request.getName(), id);
        if (isNameExist) {
            throw new ApiException("Category name already exist.");
        }
        request.setId(id);
        Category category = categoryMapper.toEntity(request);
        categoryRepository.save(category);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<AdminCategoryResponse> findById(String id) {
        Category category = entityService.getCategory(id);
        AdminCategoryResponse categoryResponse = categoryMapper.toResponse(category);
        return Response.of(categoryResponse).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<Object> delete(String id) {
        Category category = entityService.getCategory(id);
        category.setDeleted(true);
        categoryRepository.save(category);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }
}
