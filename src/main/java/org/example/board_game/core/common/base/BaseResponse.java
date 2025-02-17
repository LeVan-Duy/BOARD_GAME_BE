package org.example.board_game.core.common.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.common.PageableRequest;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseResponse {

    String id;

    String name;

    String description;
}
