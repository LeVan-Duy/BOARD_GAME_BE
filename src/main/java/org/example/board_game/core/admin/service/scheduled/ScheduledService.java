package org.example.board_game.core.admin.service.scheduled;

public interface ScheduledService {

    void updateVoucherStatusAutomatically();

    void removeOrderPendingAndOnline();
}
