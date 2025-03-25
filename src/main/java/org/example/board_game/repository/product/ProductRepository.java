package org.example.board_game.repository.product;

import org.example.board_game.core.admin.domain.dto.request.product.AdminCategoryRequest;
import org.example.board_game.core.admin.domain.dto.request.product.AdminProductRequest;
import org.example.board_game.core.client.domain.dto.request.product.ClientProductRequest;
import org.example.board_game.entity.product.Category;
import org.example.board_game.entity.product.Product;
import org.example.board_game.infrastructure.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    @Query("""
            SELECT x FROM Product x
            WHERE
                (:#{#request.name} IS NULL OR :#{#request.name} ILIKE '' OR x.name ILIKE  CONCAT('%', :#{#request.name}, '%'))
            AND (:#{#request.language} IS NULL OR :#{#request.language} ILIKE '' OR x.language ILIKE  CONCAT('%', :#{#request.language}, '%'))
            AND (:#{#request.publisherId} IS NULL OR :#{#request.publisherId} ILIKE '' OR x.publisher.id = :#{#request.publisherId})
            AND (:#{#request.authorId} IS NULL OR :#{#request.authorId} ILIKE '' OR x.author.id = :#{#request.authorId})
            AND (:#{#request.maxTime} IS NULL OR x.maxTime <= :#{#request.maxTime})
            AND (:#{#request.minTime} IS NULL OR x.maxTime >= :#{#request.minTime})
            AND (:#{#request.toAge} IS NULL OR x.minAge >= :#{#request.toAge})
            AND (:#{#request.fromAge} IS NULL OR x.minAge <= :#{#request.fromAge})
            AND (:#{#request.minQuality} IS NULL OR x.quantity >= :#{#request.minQuality})
            AND (:#{#request.maxQuality} IS NULL OR x.quantity <= :#{#request.maxQuality})
            AND (:status IS NULL OR x.status = :status)
            AND (
                       :#{#request.categoryId} IS NULL
                    OR :#{#request.categoryId} ILIKE ''
                    OR x.id IN (SELECT pg.product.id FROM ProductCategory pg WHERE pg.category.id = :#{#request.categoryId}))
            AND x.deleted = FALSE
            """)
    Page<Product> findAllProduct(@Param("request") AdminProductRequest request,
                                 @Param("status") ProductStatus status, Pageable pageable);

    @Query("""
            SELECT x FROM Product x
            WHERE
                (:#{#request.name} IS NULL OR :#{#request.name} ILIKE '' OR x.name ILIKE  CONCAT('%', :#{#request.name}, '%'))
            AND (:#{#request.language} IS NULL OR :#{#request.language} ILIKE '' OR x.language ILIKE  CONCAT('%', :#{#request.language}, '%'))
            AND (:#{#request.publisherId} IS NULL OR :#{#request.publisherId} ILIKE '' OR x.publisher.id = :#{#request.publisherId})
            AND (:#{#request.authorId} IS NULL OR :#{#request.authorId} ILIKE '' OR x.author.id = :#{#request.authorId})
            AND (:#{#request.maxTime} IS NULL OR x.maxTime <= :#{#request.maxTime})
            AND (:#{#request.minTime} IS NULL OR x.maxTime >= :#{#request.minTime})
            AND (:#{#request.toAge} IS NULL OR x.minAge >= :#{#request.toAge})
            AND (:#{#request.fromAge} IS NULL OR x.minAge <= :#{#request.fromAge})
            AND (:#{#request.minQuality} IS NULL OR x.quantity >= :#{#request.minQuality})
            AND (:#{#request.maxQuality} IS NULL OR x.quantity <= :#{#request.maxQuality})
            AND (x.status = 0)
            AND (
                       :#{#request.categoryId} IS NULL
                    OR :#{#request.categoryId} ILIKE ''
                    OR x.id IN (SELECT pg.product.id FROM ProductCategory pg WHERE pg.category.id = :#{#request.categoryId}))
            AND x.deleted = FALSE
            """)
    Page<Product> findAllForClient(@Param("request") ClientProductRequest request, Pageable pageable);


    @Query("""
            SELECT x FROM Product x
            WHERE x.status = 0 AND x.deleted = FALSE AND x.id IN :productIds
            """)
    List<Product> findAllSellerProduct(List<String> productIds);

    @Query(value = """
            SELECT * FROM product
            WHERE status = 0 AND deleted = FALSE
            ORDER BY created_at DESC
            LIMIT 10
            """, nativeQuery = true)
    List<Product> findTopNewProducts();


    boolean existsByNameAndDeletedFalseAndIdNotLike(String name, String id);

    boolean existsByNameAndDeletedFalse(String name);
}
