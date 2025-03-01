package org.example.board_game.core.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RefreshTokenRequest {

    @NotNull(message = "Không được để trống refreshToken.")
    @NotBlank(message = "Không được để trống refreshToken.")
    String refreshToken;
}
