package org.example.board_game.core.auth.service;


import org.example.board_game.utils.Response;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.file.AccessDeniedException;

public interface JwtService {

    String generateToken(UserDetails userDetails);

    String extractUsername(String token);

    boolean isValidToken(String token, UserDetails userDetails);

    String createRefreshToken(UserDetails userDetails);

    boolean validRefreshToken(String token);

    void invalidateRefreshToken(String token,String username);
}
