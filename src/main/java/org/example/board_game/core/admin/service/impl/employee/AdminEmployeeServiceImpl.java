package org.example.board_game.core.admin.service.impl.employee;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.service.employee.AdminEmployeeService;
import org.example.board_game.core.admin.service.order.AdminOrderService;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminEmployeeServiceImpl implements AdminEmployeeService {

}
