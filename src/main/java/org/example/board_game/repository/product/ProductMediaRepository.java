package org.example.board_game.repository.product;

import org.example.board_game.entity.product.Product;
import org.example.board_game.entity.product.ProductMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductMediaRepository extends JpaRepository<ProductMedia, String> {

    List<ProductMedia> getAllByIdInAndProduct_Id(List<String> ids, String product_id);

    Optional<ProductMedia> getProductMediaByProduct_IdAndMainImgTrue(String productId);

    Optional<ProductMedia> findByIdAndProduct_Id(String id, String product_id);
}
