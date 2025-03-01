package org.example.board_game.core.admin.domain.dto.response.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class ImageResponse {

    String id;

    String url;

    boolean newImage;

}
