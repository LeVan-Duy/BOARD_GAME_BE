package org.example.board_game.core.admin.service.impl.product;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.product.AdminPublisherRequest;
import org.example.board_game.core.admin.domain.dto.response.product.AdminPublisherResponse;
import org.example.board_game.core.admin.domain.mapper.product.AdminPublisherMapper;
import org.example.board_game.core.admin.service.product.AdminPublisherService;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.product.Publisher;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.repository.product.PublisherRepository;
import org.example.board_game.utils.PaginationUtil;
import org.example.board_game.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminPublisherServiceImpl implements AdminPublisherService {

    PublisherRepository publisherRepository;
    EntityService entityService;
    AdminPublisherMapper PublisherMapper = AdminPublisherMapper.INSTANCE;

    @Override
    public Response<PageableObject<AdminPublisherResponse>> findAll(AdminPublisherRequest request) {
        Pageable pageable = PaginationUtil.pageable(request);
        Page<Publisher> page = publisherRepository.findAllPublisher(request, pageable);
        Page<AdminPublisherResponse> resPage = page.map(PublisherMapper::toResponse);
        PageableObject<AdminPublisherResponse> pageableObject = new PageableObject<>(resPage);
        return Response.of(pageableObject).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<Object> create(AdminPublisherRequest request) {
        boolean isNameExist = publisherRepository.existsByNameAndDeletedFalse(request.getName());
        if (isNameExist) {
            throw new ApiException("Publisher name already exist.");
        }
        Publisher Publisher = PublisherMapper.toEntity(request);
        publisherRepository.save(Publisher);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> update(AdminPublisherRequest request, String id) {

        boolean checkPublisherExists = publisherRepository.existsById(id);
        if (!checkPublisherExists) {
            throw new ResourceNotFoundException("Publisher not found.");
        }
        boolean isNameExist = publisherRepository.existsByNameAndDeletedFalseAndIdNotLike(request.getName(), id);
        if (isNameExist) {
            throw new ApiException("Publisher name already exist.");
        }
        request.setId(id);
        Publisher Publisher = PublisherMapper.toEntity(request);
        publisherRepository.save(Publisher);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<AdminPublisherResponse> findById(String id) {
        Publisher Publisher = entityService.getPublisher(id);
        AdminPublisherResponse PublisherResponse = PublisherMapper.toResponse(Publisher);
        return Response.of(PublisherResponse).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<Object> delete(String id) {
        Publisher Publisher = entityService.getPublisher(id);
        Publisher.setDeleted(true);
        publisherRepository.save(Publisher);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }
}
