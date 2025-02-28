package org.example.board_game.core.admin.service.impl.product;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.product.AdminAddImageRequest;
import org.example.board_game.core.admin.domain.dto.request.product.AdminProductRequest;
import org.example.board_game.core.admin.domain.dto.response.product.AdminProductResponse;
import org.example.board_game.core.admin.domain.mapper.product.AdminProductMapper;
import org.example.board_game.core.admin.service.product.AdminProductService;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.product.*;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.enums.ProductStatus;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.repository.product.CategoryRepository;
import org.example.board_game.repository.product.ProductCategoryRepository;
import org.example.board_game.repository.product.ProductMediaRepository;
import org.example.board_game.repository.product.ProductRepository;
import org.example.board_game.utils.CollectionUtils;
import org.example.board_game.utils.PaginationUtil;
import org.example.board_game.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    ProductCategoryRepository productCategoryRepository;
    ProductMediaRepository productMediaRepository;
    EntityService entityService;
    AdminProductMapper productMapper = AdminProductMapper.INSTANCE;

    //todo function response
    @Override
    public Response<PageableObject<AdminProductResponse>> findAll(AdminProductRequest request) {
        Pageable pageable = PaginationUtil.pageable(request);
        Page<Product> page = productRepository.findAllProduct(request, request.getStatus(), pageable);
        Page<AdminProductResponse> resPage = page.map(productMapper::toResponse);
        PageableObject<AdminProductResponse> pageableObject = new PageableObject<>(resPage);
        return Response.of(pageableObject).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<Object> create(AdminProductRequest request) {

        List<AdminAddImageRequest> images = request.getImages();
        List<String> categoryIds = request.getCategoryIds();

        if (CollectionUtils.isListEmpty(images)) {
            throw new ResourceNotFoundException("Vui lòng upload image cho sản phẩm này.");
        }
        if (images.stream().filter(AdminAddImageRequest::isMainImage).count() != 1) {
            throw new IllegalArgumentException("Sản phẩm cần có một ảnh đại diện.");
        }
        if (CollectionUtils.isListEmpty(categoryIds)) {
            throw new ResourceNotFoundException("Vui lòng chọn ít nhất một thể loại cho sản phẩm này.");
        }
        boolean isNameExist = productRepository.existsByNameAndDeletedFalse(request.getName());
        if (isNameExist) {
            throw new ApiException("Tên sản phẩm đã tồn tại.");
        }
        Publisher publisher = entityService.getPublisher(request.getPublisherId());
        Author author = entityService.getAuthor(request.getAuthorId());
        Product product = productMapper.toEntity(request);

        product.setAuthor(author);
        product.setPublisher(publisher);
        product.setStatus(ProductStatus.ACTIVE);
        Product saveProduct = productRepository.save(product);
        addCategoriesToProduct(categoryIds, saveProduct);
        addImagesToProduct(images, saveProduct);

        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> update(AdminProductRequest request, String id) {

        List<String> categoryIds = request.getCategoryIds();
        if (CollectionUtils.isListEmpty(categoryIds)) {
            throw new ResourceNotFoundException("Vui lòng chọn ít nhất một thể loại cho sản phẩm này.");
        }
        boolean isNameExist = productRepository.existsByNameAndDeletedFalseAndIdNotLike(request.getName(), id);
        if (isNameExist) {
            throw new ApiException("Tên sản phẩm đã tồn tại.");
        }
        Product product = entityService.getProduct(id);
        removeProductCategories(id);
        Publisher publisher = entityService.getPublisher(request.getPublisherId());
        Author author = entityService.getAuthor(request.getAuthorId());
        productMapper.updateProduct(request,product);
        product.setAuthor(author);
        product.setPublisher(publisher);
        productRepository.save(product);
        addCategoriesToProduct(categoryIds,product);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<AdminProductResponse> findById(String id) {
        Product Product = entityService.getProduct(id);
        AdminProductResponse ProductResponse = productMapper.toResponse(Product);
        return Response.of(ProductResponse).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<Object> delete(String id) {
        Product Product = entityService.getProduct(id);
        Product.setDeleted(true);
        productRepository.save(Product);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    private void addCategoriesToProduct(List<String> categoryIds, Product product) {
        List<Category> categories = categoryRepository.getAllByIdInAndDeletedFalse(categoryIds);
        if (CollectionUtils.isListEmpty(categories)) {
            throw new ResourceNotFoundException("Không tìm thấy thể loại này trong hệ thống.");
        }
        List<ProductCategory> productCategories = new ArrayList<>();
        categories.forEach(category -> {
            ProductCategory productCategory = new ProductCategory();
            productCategory.setCategory(category);
            productCategory.setProduct(product);
            productCategories.add(productCategory);
        });
        productCategoryRepository.saveAll(productCategories);
    }

    private void addImagesToProduct(List<AdminAddImageRequest> images, Product product) {
        List<ProductMedia> productMediaList = new ArrayList<>();
        images.forEach(image -> {
            ProductMedia productMedia = new ProductMedia();
            productMedia.setMainImg(image.isMainImage());
            productMedia.setUrl(image.getUrl());
            productMedia.setProduct(product);
            productMediaList.add(productMedia);
        });
        productMediaRepository.saveAll(productMediaList);
    }

    private void removeProductCategories(String productId) {
        List<ProductCategory> productCategories = productCategoryRepository.getAllByProduct_Id(productId);
        if (CollectionUtils.isListEmpty(productCategories)) {
            return;
        }
        productCategoryRepository.deleteAll(productCategories);
    }
}
