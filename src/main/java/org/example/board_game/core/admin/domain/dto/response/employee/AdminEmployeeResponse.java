package org.example.board_game.core.admin.domain.dto.response.employee;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.infrastructure.enums.EmployeeStatus;
import org.example.board_game.infrastructure.enums.Role;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminEmployeeResponse {

    String id;

    String fullName;

    String email;

    String password;

    String address;

    String gender;

    String phoneNumber;

    String image;

    Role role;

    EmployeeStatus status;
}
