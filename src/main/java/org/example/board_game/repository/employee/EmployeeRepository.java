package org.example.board_game.repository.employee;

import org.example.board_game.core.admin.domain.dto.request.employee.AdminEmployeeRequest;
import org.example.board_game.entity.employee.Employee;
import org.example.board_game.infrastructure.enums.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    @Query("""
                SELECT x FROM Employee x
                WHERE
                (
                   :#{#request.q} IS NULL OR :#{#request.q} ILIKE '' OR x.fullName ILIKE CONCAT('%', :#{#request.q}, '%')
                   OR x.email ILIKE CONCAT('%', :#{#request.q}, '%') OR x.address ILIKE CONCAT('%', :#{#request.q}, '%')
                   OR x.phoneNumber ILIKE CONCAT('%', :#{#request.q}, '%')
                )
                AND
                (:status IS NULL OR x.status = :status) AND (x.deleted=FALSE)
            """)
    Page<Employee> findAllEmployee(@Param("request") AdminEmployeeRequest request,
                                   @Param("status") EmployeeStatus status, Pageable pageable);

    Optional<Employee> findByEmailAndDeletedFalse(String email);

    boolean existsByEmailAndDeletedFalse(String email);
    boolean existsByEmailAndDeletedFalseAndIdNotLike(String email, String id);

}
