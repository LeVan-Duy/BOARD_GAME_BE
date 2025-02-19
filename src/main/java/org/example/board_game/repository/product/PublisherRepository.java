package org.example.board_game.repository.product;

import org.example.board_game.core.admin.domain.dto.request.product.AdminAuthorRequest;
import org.example.board_game.core.admin.domain.dto.request.product.AdminPublisherRequest;
import org.example.board_game.entity.product.Author;
import org.example.board_game.entity.product.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, String> {

    @Query("""
           SELECT x FROM Publisher x
           WHERE (:#{#request.name} IS NULL OR :#{#request.name} ILIKE '' OR x.name ILIKE  CONCAT('%', :#{#request.name}, '%'))
           AND x.deleted = FALSE
           """)
    Page<Publisher> findAllPublisher(@Param("request") AdminPublisherRequest request, Pageable pageable);

    boolean existsByNameAndDeletedFalseAndIdNotLike(String name, String id);
    boolean existsByNameAndDeletedFalse(String name);
}
