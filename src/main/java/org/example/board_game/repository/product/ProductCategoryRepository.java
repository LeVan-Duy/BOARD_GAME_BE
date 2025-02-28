package org.example.board_game.repository.product;

import org.example.board_game.entity.product.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, String> {

    List<ProductCategory> getAllByProduct_Id(String productId);
}
