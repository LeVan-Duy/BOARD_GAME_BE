package org.example.board_game.repository.employee;

import org.example.board_game.entity.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findByEmailAndDeletedFalse(String email);
    boolean existsByEmailAndDeletedFalse(String email);
}
