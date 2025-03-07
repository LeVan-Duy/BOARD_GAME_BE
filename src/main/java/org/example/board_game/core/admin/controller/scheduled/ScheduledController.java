package org.example.board_game.core.admin.controller.scheduled;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.service.scheduled.ScheduledService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScheduledController {

    ScheduledService scheduledService;

    @Scheduled(cron = "0 * * * * ?") // chạy 1 phút 1 lần...
    public void updateDiscountStatusDaily() {
        scheduledService.updateVoucherStatusAutomatically();
    }
}
