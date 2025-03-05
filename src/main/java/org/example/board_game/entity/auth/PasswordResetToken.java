package org.example.board_game.entity.auth;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.entity.base.PrimaryEntity;
import java.util.Date;

@Getter
@Setter
@Table(name = "password_reset_token")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordResetToken extends PrimaryEntity {

    @Column(name = "token")
    String token;

    @Column(name = "exporation_time")
    Date expirationTime;

    @Column(name = "username")
    String username;
}
