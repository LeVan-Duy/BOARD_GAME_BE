package org.example.board_game.repository.order;

import org.example.board_game.core.admin.domain.dto.request.order.AdminOrderRequest;
import org.example.board_game.entity.order.Order;
import org.example.board_game.infrastructure.enums.OrderStatus;
import org.example.board_game.infrastructure.enums.OrderType;
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
public interface OrderRepository extends JpaRepository<Order, String> {


    @Query("""
            SELECT o FROM Order o
            WHERE
            (
               (:#{#request.q} IS NULL OR :#{#request.q} ILIKE '' OR o.code ILIKE  CONCAT('%', :#{#request.q}, '%'))
               OR
               (:#{#request.q} IS NULL OR :#{#request.q} ILIKE '' OR o.fullName ILIKE  CONCAT('%', :#{#request.q}, '%'))
               OR
               (:#{#request.q} IS NULL OR :#{#request.q} ILIKE '' OR o.email ILIKE  CONCAT('%', :#{#request.q}, '%'))
            )
            AND (:#{#request.priceMin} IS NULL OR :#{#request.priceMin} = 0 OR o.totalMoney >= :#{#request.priceMin})
            AND (:#{#request.priceMax} IS NULL OR :#{#request.priceMax} = 0 OR o.totalMoney <= :#{#request.priceMax})
            AND (:status IS NULL OR o.status = :status) AND (:type IS NULL OR o.type = :type)
            AND (:employeeId IS NULL OR :employeeId ILIKE '' OR o.employee.id = :employeeId OR o.employee IS NULL)
            AND (o.deleted = FALSE)
            """)
    Page<Order> findAllOrder(@Param("request") AdminOrderRequest request, @Param("status") OrderStatus status,
                             @Param("type") OrderType type, @Param("employeeId") String employeeId, Pageable pageable);

    Integer countAllByStatusAndTypeAndDeletedFalse(OrderStatus status, OrderType type);

    Optional<Order> findByCodeAndDeletedFalse(String code);

    Optional<Order> findByIdAndCustomer_Id(String id, String customerId);

    List<Order> getAllByCustomer_IdAndDeletedFalse(String customerId);

    List<Order> getAllByCustomer_IdInAndDeletedFalse(List<String> customerIds);

    List<Order> findAllByStatusAndCreatedAtBeforeAndDeletedFalseAndType(OrderStatus status, Long createdAt, OrderType type);

}
