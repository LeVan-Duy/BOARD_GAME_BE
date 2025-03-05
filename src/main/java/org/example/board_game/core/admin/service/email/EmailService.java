package org.example.board_game.core.admin.service.email;

import org.example.board_game.core.admin.domain.dto.request.email.EmailRequest;

public interface EmailService {

    void sendEmail(EmailRequest request);

}
