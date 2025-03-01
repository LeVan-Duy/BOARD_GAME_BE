package org.example.board_game.core.auth.service.impl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.auth.service.UserDetailService;
import org.example.board_game.entity.employee.Employee;
import org.example.board_game.infrastructure.exception.ResourceNotFoundException;
import org.example.board_game.repository.customer.CustomerRepository;
import org.example.board_game.repository.employee.EmployeeRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailService {

    EmployeeRepository employeeRepository;
    CustomerRepository customerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Employee employee = employeeRepository
                .findByEmailAndDeletedFalse(username)
                .orElse(null);
        if (employee == null) {
            return customerRepository
                    .findByEmailAndDeletedFalse(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        }
        return employee;
    }
}
