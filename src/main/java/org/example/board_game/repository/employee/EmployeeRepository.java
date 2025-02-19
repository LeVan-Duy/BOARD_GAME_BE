package org.example.board_game.repository.employee;

import org.example.board_game.entity.employee.Employee;
import org.example.board_game.entity.voucher.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

}
