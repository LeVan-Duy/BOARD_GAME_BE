package org.example.board_game.repository.product;

import org.example.board_game.core.admin.domain.dto.request.product.AdminCategoryRequest;
import org.example.board_game.entity.product.Category;
import org.example.board_game.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Query("""
           SELECT x FROM Product x
           WHERE (:#{#request.name} IS NULL OR :#{#request.name} ILIKE '' OR x.name ILIKE  CONCAT('%', :#{#request.name}, '%'))
           AND x.deleted = FALSE
           """)
    Page<Product> findAllProduct(@Param("request") AdminCategoryRequest request, Pageable pageable);

    boolean existsByNameAndDeletedFalseAndIdNotLike(String name, String id);
    boolean existsByNameAndDeletedFalse(String name);
}
