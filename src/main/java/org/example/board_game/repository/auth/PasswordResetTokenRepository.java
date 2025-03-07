package org.example.board_game.repository.auth;

import org.example.board_game.entity.auth.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, String> {

    Optional<PasswordResetToken> findByUsername(String username);

    Optional<PasswordResetToken> findByToken(String token);
}
