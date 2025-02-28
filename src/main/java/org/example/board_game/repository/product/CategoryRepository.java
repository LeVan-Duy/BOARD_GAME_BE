package org.example.board_game.repository.product;

import org.example.board_game.core.admin.domain.dto.request.product.AdminCategoryRequest;
import org.example.board_game.entity.product.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    @Query("""
           SELECT x FROM Category x
           WHERE (:#{#request.name} IS NULL OR :#{#request.name} ILIKE '' OR x.name ILIKE  CONCAT('%', :#{#request.name}, '%'))
           AND x.deleted = FALSE
           """)
    Page<Category> findAllCategory(@Param("request") AdminCategoryRequest request, Pageable pageable);

    List<Category> getAllByIdInAndDeletedFalse(List<String> ids);

    boolean existsByNameAndDeletedFalseAndIdNotLike(String name, String id);
    boolean existsByNameAndDeletedFalse(String name);
}
