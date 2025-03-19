package org.example.board_game.repository.cart;

import org.example.board_game.core.common.base.BaseRequest;
import org.example.board_game.entity.cart.Cart;
import org.example.board_game.entity.cart.CartDetail;
import org.example.board_game.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, String> {

    @Query("""
            SELECT x FROM CartDetail x
            WHERE (:#{#request.q} IS NULL OR :#{#request.q} ILIKE ''
            OR x.product.name ILIKE  CONCAT('%', :#{#request.q}, '%')
            OR x.product.description ILIKE  CONCAT('%', :#{#request.q}, '%')
            OR x.product.author.name ILIKE CONCAT('%', :#{#request.q}, '%')
            OR x.product.publisher.name ILIKE CONCAT('%', :#{#request.q}, '%'))
            AND x.cart.id = :cartId
            AND x.deleted = FALSE
            """)
    List<CartDetail> findAllCartDetail(@Param("request") BaseRequest request, String cartId);

    CartDetail findByProductAndCart(Product product, Cart cart);

    Optional<CartDetail> findByIdAndCart(String id, Cart cart);

    List<CartDetail> findAllByCartAndIdIn(Cart cart, List<String> id);

}
