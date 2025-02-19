package org.example.board_game.core.admin.service.impl.product;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.product.AdminAuthorRequest;
import org.example.board_game.core.admin.domain.dto.response.product.AdminAuthorResponse;
import org.example.board_game.core.admin.domain.mapper.product.AdminAuthorMapper;
import org.example.board_game.core.admin.service.product.AdminAuthorService;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.product.Author;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.repository.product.AuthorRepository;
import org.example.board_game.utils.PaginationUtil;
import org.example.board_game.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminAuthorServiceImpl implements AdminAuthorService {

    AuthorRepository authorRepository;
    EntityService entityService;
    AdminAuthorMapper AuthorMapper = AdminAuthorMapper.INSTANCE;

    @Override
    public Response<PageableObject<AdminAuthorResponse>> findAll(AdminAuthorRequest request) {
        Pageable pageable = PaginationUtil.pageable(request);
        Page<Author> page = authorRepository.findAllAuthor(request, pageable);
        Page<AdminAuthorResponse> resPage = page.map(AuthorMapper::toResponse);
        PageableObject<AdminAuthorResponse> pageableObject = new PageableObject<>(resPage);
        return Response.of(pageableObject).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<Object> create(AdminAuthorRequest request) {
        boolean isNameExist = authorRepository.existsByNameAndDeletedFalse(request.getName());
        if (isNameExist) {
            throw new ApiException("Author name already exist.");
        }
        Author Author = AuthorMapper.toEntity(request);
        authorRepository.save(Author);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> update(AdminAuthorRequest request, String id) {

        boolean checkAuthorExists = authorRepository.existsById(id);
        if (!checkAuthorExists) {
            throw new ResourceNotFoundException("Author not found.");
        }
        boolean isNameExist = authorRepository.existsByNameAndDeletedFalseAndIdNotLike(request.getName(), id);
        if (isNameExist) {
            throw new ApiException("Author name already exist.");
        }
        request.setId(id);
        Author Author = AuthorMapper.toEntity(request);
        authorRepository.save(Author);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<AdminAuthorResponse> findById(String id) {
        Author Author = entityService.getAuthor(id);
        AdminAuthorResponse AuthorResponse = AuthorMapper.toResponse(Author);
        return Response.of(AuthorResponse).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<Object> delete(String id) {
        Author Author = entityService.getAuthor(id);
        Author.setDeleted(true);
        authorRepository.save(Author);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }
}
