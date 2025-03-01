package org.example.board_game.repository.auth;

import org.example.board_game.entity.auth.BlacklistToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistTokenRepository extends JpaRepository<BlacklistToken, String> {

    boolean existsByToken(String token);
}
