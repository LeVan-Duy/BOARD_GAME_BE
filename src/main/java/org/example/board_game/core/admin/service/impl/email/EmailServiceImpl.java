package org.example.board_game.core.admin.service.impl.email;

import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.board_game.core.admin.domain.dto.request.email.EmailRequest;
import org.example.board_game.core.admin.service.email.EmailService;
import org.example.board_game.infrastructure.constants.MailConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailServiceImpl implements EmailService {

    final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    String sender;

    @Override
    @Async
    public void sendEmail(EmailRequest details) {
        String htmlBody = MailConstant.BODY_STARTS +
                details.getTitleEmail() +
                details.getBody() +
                MailConstant.BODY_END;
        sendSimpleMail(details.getToEmail(), htmlBody, details.getSubject());
    }

    private void sendSimpleMail(String[] recipients, String msgBody, String subject) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.toString());
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setBcc(recipients);
            mimeMessageHelper.setText(msgBody, true);
            mimeMessageHelper.setSubject(subject);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("ERROR WHILE SENDING MAIL: {}", e.getMessage());
        }
    }
}
