package org.example.board_game.core.admin.service.impl.scheduled;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.service.scheduled.ScheduledService;
import org.example.board_game.repository.voucher.VoucherRepository;
import org.example.board_game.utils.ConvertUtil;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class ScheduledServiceImpl implements ScheduledService {

    VoucherRepository voucherRepository;

    @Override
    public void updateVoucherStatusAutomatically() {
        Long currentDate = ConvertUtil.convertDateToLong(new Date());
        voucherRepository.updateStatusAutomatically(currentDate);
    }

}
