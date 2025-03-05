package org.example.board_game.core.admin.domain.dto.request.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {
    private String [] toEmail;
    private String subject;
    private String body;
    private String titleEmail;
}
