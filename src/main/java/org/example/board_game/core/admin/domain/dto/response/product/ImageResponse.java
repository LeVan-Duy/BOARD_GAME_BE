package org.example.board_game.core.admin.domain.dto.response.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImageResponse {

    String id;

    String url;

    boolean newImage;
}
