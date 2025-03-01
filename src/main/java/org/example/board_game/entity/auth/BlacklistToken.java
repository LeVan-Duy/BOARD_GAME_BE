package org.example.board_game.entity.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.example.board_game.entity.base.PrimaryEntity;

@Entity
@Table(name = "blacklist_tokens")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistToken extends PrimaryEntity {

    @Column(nullable = false, unique = true)
    private String token;
}
