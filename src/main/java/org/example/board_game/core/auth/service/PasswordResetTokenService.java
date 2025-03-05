package org.example.board_game.core.auth.service;

import org.example.board_game.core.auth.dto.request.*;
import org.example.board_game.utils.Response;

public interface PasswordResetTokenService {

    Response<Object> sendMailReset(String email);

    Response<Object> resetPassword(PasswordResetRequest request);

}
