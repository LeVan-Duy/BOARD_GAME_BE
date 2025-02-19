package org.example.board_game.core.common.base;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.common.PageableRequest;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseRequest extends PageableRequest {

    String id;

    @NotBlank(message = "Tên không được bỏ trống.")
    @NotNull(message = "Tên không được bỏ trống.")
    String name;

    String description;
}
