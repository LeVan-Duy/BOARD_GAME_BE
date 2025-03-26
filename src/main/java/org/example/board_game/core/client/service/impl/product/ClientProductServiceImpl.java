package org.example.board_game.core.client.service.impl.product;

import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.response.product.ImageResponse;
import org.example.board_game.core.client.domain.dto.request.product.ClientBestSellerRequest;
import org.example.board_game.core.client.domain.dto.request.product.ClientProductRequest;
import org.example.board_game.core.client.domain.dto.response.product.ClientProductResponse;
import org.example.board_game.core.client.domain.mapper.product.ClientProductMapper;
import org.example.board_game.core.client.service.product.ClientProductService;
import org.example.board_game.core.common.PageableObject;
import org.example.board_game.core.common.base.BaseResponse;
import org.example.board_game.entity.product.*;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.repository.order.OrderDetailRepository;
import org.example.board_game.repository.product.ProductRepository;
import org.example.board_game.utils.CollectionUtils;
import org.example.board_game.utils.PaginationUtil;
import org.example.board_game.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClientProductServiceImpl implements ClientProductService {

    ProductRepository productRepository;
    OrderDetailRepository orderDetailRepository;
    ClientProductMapper productMapper = ClientProductMapper.INSTANCE;

    @Override
    public Response<PageableObject<ClientProductResponse>> getAll(ClientProductRequest request) {

        Pageable pageable = PaginationUtil.pageable(request);
        Page<Product> page = productRepository.findAllForClient(request, pageable);
        List<String> productIds = CollectionUtils.extractField(page.getContent(), Product::getId);
        List<Tuple> soldCountByProducts = orderDetailRepository.soldCountProduct(productIds);
        Map<String, Long> getSoldCountMap = getSoldCountMap(soldCountByProducts);

        List<ClientProductResponse> responseList = page.getContent().stream()
                .map(product -> detailProductRes(product, getSoldCountMap))
                .toList();

        return Response
                .of(new PageableObject<>(new PageImpl<>(responseList, pageable, page.getTotalElements())))
                .success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<List<ClientProductResponse>> getBestSeller(ClientBestSellerRequest request) {

        if (request.getEndDate() == null || request.getStartDate() == null) {
            request.setEndDate(null);
            request.setStartDate(null);
        }
        List<Tuple> getTopSeller = orderDetailRepository.findTopSellerProducts(request.getStartDate(), request.getEndDate());
        List<String> productIds = CollectionUtils.extractField(getTopSeller, tuple -> tuple.get("productId", String.class));
        List<Product> products = productRepository.findAllByIds(productIds);
        Map<String, Long> getSoldCountMap = getSoldCountMap(getTopSeller);

        List<ClientProductResponse> responseList = products.stream()
                .map(product -> detailProductRes(product, getSoldCountMap))
                .toList();

        return Response
                .of(responseList)
                .success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<List<ClientProductResponse>> getNewProducts() {
        List<Product> newProducts = productRepository.findTopNewProducts();
        List<String> productIds = CollectionUtils.extractField(newProducts, Product::getId);
        List<Tuple> soldCountByProducts = orderDetailRepository.soldCountProduct(productIds);
        Map<String, Long> getSoldCountMap = getSoldCountMap(soldCountByProducts);

        List<ClientProductResponse> responseList =newProducts.stream()
                .map(product -> detailProductRes(product, getSoldCountMap))
                .toList();

        return Response
                .of(responseList)
                .success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }


    private BaseResponse baseResponse(String id, String name, String description) {
        BaseResponse response = new BaseResponse();
        response.setId(id);
        response.setName(name);
        response.setDescription(description);
        return response;
    }

    private Map<String, Long> getSoldCountMap(List<Tuple> results) {
        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get("productId", String.class),
                        tuple -> tuple.get("quantity", Long.class)
                ));
    }

    private ClientProductResponse detailProductRes(Product product, Map<String, Long> soldCountMap) {
        ClientProductResponse response = productMapper.toResponse(product);
        Long quantity = soldCountMap.getOrDefault(product.getId(), 0L);
        response.setSoldCount(quantity);

        response.setAuthor(product.getAuthor() != null
                ? baseResponse(product.getAuthor().getId(), product.getAuthor().getName(), product.getAuthor().getDescription())
                : null);
        response.setCategories(product.getProductCategoryList().stream()
                .map(pc -> baseResponse(pc.getCategory().getId(), pc.getCategory().getName(), pc.getCategory().getDescription()))
                .collect(Collectors.toList()));

        response.setPublisher(product.getPublisher() != null
                ? baseResponse(product.getPublisher().getId(), product.getPublisher().getName(), product.getPublisher().getDescription())
                : null);

        response.setImages(product.getProductMediaList().stream()
                .map(image -> new ImageResponse(image.getId(), image.getUrl(), image.isMainImg()))
                .collect(Collectors.toList()));
        return response;
    }

}
