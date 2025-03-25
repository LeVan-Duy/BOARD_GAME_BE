package org.example.board_game.repository.order;

import jakarta.persistence.Tuple;
import org.example.board_game.entity.order.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {

    @Query("""
            SELECT SUM(x.quantity) as quantity, x.product.id as productId
            FROM OrderDetail x
            WHERE x.order.status = 4 AND x.deleted = FALSE AND x.product.id IN :productIds
            GROUP BY x.product.id
            """)
    List<Tuple> soldCountProduct(List<String> productIds);


    @Query(value = """
    SELECT SUM(od.quantity) as quantity, od.product_id as productId
    FROM order_detail od
    JOIN shop_order o ON od.order_id = o.id
    WHERE o.status = 4
        AND od.deleted = FALSE
        AND (
            (:startDate IS NULL AND :endDate IS NULL)
            OR (o.updated_at BETWEEN :startDate AND :endDate)
        )
    GROUP BY od.product_id
    ORDER BY quantity DESC
    LIMIT 10
    """, nativeQuery = true)
    List<Tuple> findTopSellerProducts(@Param("startDate") Long startDate,
                                      @Param("endDate") Long endDate);




}
