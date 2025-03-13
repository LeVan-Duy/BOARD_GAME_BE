package org.example.board_game;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.employee.Employee;
import org.example.board_game.infrastructure.enums.EmployeeStatus;
import org.example.board_game.infrastructure.enums.Gender;
import org.example.board_game.infrastructure.enums.Role;
import org.example.board_game.repository.employee.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataInitializer implements CommandLineRunner {

    EmployeeRepository employeeRepository;
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createAccountAdminForFirstLogin();
    }

    public void createAccountAdminForFirstLogin() {
        if (employeeRepository.findByEmailAndDeletedFalse("admin@gmail.com").isPresent()) {
            return;
        }
        Employee employee = new Employee();
        employee.setEmail("admin@gmail.com");
        employee.setPassword(passwordEncoder.encode("admin"));
        employee.setGender(Gender.MALE);
        employee.setCreatedBy("system");
        employee.setRole(Role.ADMIN);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setFullName("ADMIN BOARD-GAME");
        employeeRepository.save(employee);
    }
}
