package org.example.board_game.core.client.service.impl.cart;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.client.domain.dto.request.cart.ClientAddProductToCartRequest;
import org.example.board_game.core.client.domain.dto.response.cart.ClientProductsInCartResponse;
import org.example.board_game.core.client.domain.mapper.product.ClientProductMapper;
import org.example.board_game.core.client.service.cart.ClientCartService;
import org.example.board_game.core.common.base.BaseListIdRequest;
import org.example.board_game.core.common.base.BaseRequest;
import org.example.board_game.core.common.base.EntityService;
import org.example.board_game.entity.cart.Cart;
import org.example.board_game.entity.cart.CartDetail;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.entity.product.Product;
import org.example.board_game.infrastructure.constants.EntityProperties;
import org.example.board_game.infrastructure.constants.MessageConstant;
import org.example.board_game.infrastructure.exception.ApiException;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.infrastructure.exception.UnauthorizedException;
import org.example.board_game.repository.cart.CartDetailRepository;
import org.example.board_game.utils.Response;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ClientCartServiceImpl implements ClientCartService {

    EntityService entityService;
    CartDetailRepository cartDetailRepository;
    ClientProductMapper productMapper = ClientProductMapper.INSTANCE;

    @Override
    public Response<List<ClientProductsInCartResponse>> productsInCart(BaseRequest request) {

        String cartId = getCartByUser().getId();
        List<CartDetail> productsInCart = cartDetailRepository.findAllCartDetail(request, cartId);
        List<ClientProductsInCartResponse> responses = productsInCart.stream().map(cartDetail -> {
            ClientProductsInCartResponse response = new ClientProductsInCartResponse();
            response.setId(cartDetail.getId());
            response.setQuantity(cartDetail.getQuantity());
            response.setProduct(productMapper.toResponse(cartDetail.getProduct()));
            return response;
        }).toList();
        return Response.of(responses).success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<Object> addProductToCart(ClientAddProductToCartRequest request) {

        Cart cart = getCartByUser();
        int quantityReq = request.getQuantity();
        if (quantityReq > 5) {
            throw new ApiException("Số lượng của mỗi sản phẩm thêm vào không được vượt quá 5.");
        }
        Product product = entityService.getProduct(request.getProductId());
        int quantityProduct = product.getQuantity();
        if (quantityProduct == 0) {
            throw new ApiException("Sản phẩm này hiện tại đã hết hàng.");
        }
        if (quantityReq > quantityProduct) {
            throw new ApiException("Số lượng tồn kho sản phẩm này không đủ.");
        }
        CartDetail cartDetail = cartDetailRepository.findByProductAndCart(product, cart);
        if (cartDetail == null) {
            cartDetail = new CartDetail();
            cartDetail.setQuantity(quantityReq);
            cartDetail.setProduct(product);
            cartDetail.setCart(cart);
        } else {
            int quantityCartDetail = cartDetail.getQuantity();
            int totalQuantity = quantityCartDetail + quantityReq;
            int remainingQuantity = totalQuantity - quantityCartDetail;
            if (totalQuantity > 5) {
                throw new ApiException("Bạn đã có " + quantityCartDetail
                        + " số lượng của sản phẩm này trong giỏ hàng. Chỉ được phép thêm tối đa " + remainingQuantity
                        + " số lượng cho sản phẩm này.");
            }
            cartDetail.setQuantity(quantityCartDetail + quantityReq);
        }
        cartDetailRepository.save(cartDetail);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> updateQuantity(ClientAddProductToCartRequest request) {

        Cart cart = getCartByUser();
        String id = request.getId();

        CartDetail cartDetail = entityService.getCartDetail(id, cart);
        int quantityReq = request.getQuantity();
        if (quantityReq == 0) {
            cartDetailRepository.delete(cartDetail);
            return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
        }
        if (quantityReq > 5) {
            throw new ApiException("Số lượng của mỗi sản phẩm trong giỏ hàng không được vượt quá 5.");
        }
        Product product = cartDetail.getProduct();
        int quantityProduct = product.getQuantity();
        int quantityCartDetail = cartDetail.getQuantity();

        if (quantityReq > quantityCartDetail && quantityReq > quantityProduct) {
            throw new ApiException("Số lượng sản phẩm tồn kho không đủ.");
        }
        cartDetail.setQuantity(quantityReq);
        cartDetailRepository.save(cartDetail);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_GET);
    }

    @Override
    public Response<Object> removeProductInCart(String id) {
        Cart cart = getCartByUser();
        CartDetail cartDetail = entityService.getCartDetail(id, cart);
        cartDetailRepository.delete(cartDetail);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    @Override
    public Response<Object> removeProductsInCart(BaseListIdRequest request) {
        Cart cart = getCartByUser();
        List<String> cartDetailIds = request.getIds();
        if (cartDetailIds.isEmpty()) {
            throw new ResourceNotFoundException("Vui lòng chọn sản phẩm bạn muốn xóa khỏi giỏ hàng.");
        }
        List<CartDetail> cartDetails = cartDetailRepository.findAllByCartAndIdIn(cart, cartDetailIds);
        if (cartDetails.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ hàng.");
        }
        if (cartDetails.size() != cartDetailIds.size()) {
            throw new ApiException("Một số sản phẩm trong giỏ hàng không hợp lệ.");
        }
        cartDetailRepository.deleteAll(cartDetails);
        return Response.ok().success(EntityProperties.SUCCESS, EntityProperties.CODE_POST);
    }

    //todo có thể thêm orderId vào cartDetail để xóa cartDetail đó khi thanh toán
    //todo thêm hàm mearch giỏ hàng local với giỏ hàng trong db khi login

    private Cart getCartByUser() {
        Customer customer = entityService.getCustomerByAuth();
        if (customer == null) {
            throw new UnauthorizedException(MessageConstant.USER_NOT_FOUND);
        }
        return customer.getCart();
    }

}
