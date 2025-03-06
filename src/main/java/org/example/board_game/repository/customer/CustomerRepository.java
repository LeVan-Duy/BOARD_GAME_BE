package org.example.board_game.repository.customer;

import jakarta.persistence.Tuple;
import org.example.board_game.core.admin.domain.dto.request.customer.AdminCustomerRequest;
import org.example.board_game.entity.customer.Customer;
import org.example.board_game.infrastructure.enums.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {


    @Query("""
            SELECT x.id as id, x.fullName as fullName, x.status as status, x.dateOfBirth as dateOfBirth,
                   x.email as email, x.gender as gender, x.image as image, x.password as password
            FROM Customer x
            WHERE
                (:#{#request.q} IS NULL OR :#{#request.q} ILIKE ''  OR x.fullName ILIKE CONCAT('%', :#{#request.q}, '%')
                OR
                x.email ILIKE CONCAT('%', :#{#request.q}, '%'))
                AND
                (:status IS NULL OR x.status = :status) AND (x.deleted = FALSE)
            """)
    Page<Tuple> findAllCustomer(@Param("request") AdminCustomerRequest request,
                                @Param("status") CustomerStatus status, Pageable pageable);

    Optional<Customer> findByEmailAndDeletedFalse(String email);

    boolean existsByEmailAndDeletedFalse(String email);
    boolean existsByEmailAndDeletedFalseAndIdNotLike(String email, String id);
}
