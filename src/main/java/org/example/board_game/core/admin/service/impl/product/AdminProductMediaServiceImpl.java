package org.example.board_game.core.admin.service.impl.product;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.product.AdminProductMediaRequest;
import org.example.board_game.core.admin.domain.dto.request.product.AdminUploadMainImageRequest;
import org.example.board_game.core.admin.service.product.AdminProductMediaService;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.product.Product;
import org.example.board_game.entity.product.ProductMedia;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.repository.product.ProductMediaRepository;
import org.example.board_game.utils.CollectionUtils;
import org.example.board_game.utils.Response;
import org.example.board_game.utils.StrUtils;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminProductMediaServiceImpl implements AdminProductMediaService {

    ProductMediaRepository productMediaRepository;
    EntityService entityService;

    @Override
    public Response<Object> addImages(AdminProductMediaRequest request) {
        String productId = request.getProductId();
        List<String> urlImages = request.getUrls();
        if (CollectionUtils.isListEmpty(urlImages)) {
            throw new ResourceNotFoundException("Vui lòng chọn ít nhất 1 image cho sản phẩm này.");
        }
        Product product = entityService.getProduct(productId);
        List<ProductMedia> productMedia = new ArrayList<>();
        urlImages.forEach(url -> {
            ProductMedia media = new ProductMedia();
            media.setUrl(url);
            media.setMainImg(false);
            media.setProduct(product);
            productMedia.add(media);
        });
        productMediaRepository.saveAll(productMedia);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> delete(AdminProductMediaRequest request) {

        String productId = request.getProductId();
        List<String> ids = request.getMediaIdsRemove();
        if (CollectionUtils.isListEmpty(ids)) {
            throw new ResourceNotFoundException("Vui lòng chọn image của sản phẩm này để xóa.");
        }
        List<ProductMedia> listMediaByProduct = productMediaRepository.getAllByIdInAndProduct_Id(ids,productId);
        if (CollectionUtils.isListEmpty(listMediaByProduct)) {
            throw new ResourceNotFoundException("Không tìm thấy image của sản phẩm này.");
        }
        productMediaRepository.deleteAll(listMediaByProduct);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> updateMainImage(AdminUploadMainImageRequest request) {
        String productId = request.getProductId();
        Product product = entityService.getProduct(productId);
        ProductMedia media;
        if (request.isNewImage()) {
            if (StrUtils.isBlank(request.getUrl())) {
                throw new ResourceNotFoundException("Vui lòng chọn image đại diện sản phẩm từ thư viện của bạn.");
            }
            entityService.resetMainImage(productId);
            media = new ProductMedia();
            media.setProduct(product);
            media.setMainImg(true);
            media.setUrl(request.getUrl());
        } else {
            media = entityService.getMediaByIdAndProductId(request.getId(), productId);
            entityService.resetMainImage(productId);
            media.setMainImg(true);
        }
        productMediaRepository.save(media);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

}
