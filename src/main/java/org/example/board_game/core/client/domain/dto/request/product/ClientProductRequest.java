package org.example.board_game.core.client.domain.dto.request.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.product.AdminAddImageRequest;
import org.example.board_game.core.common.base.BaseRequest;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientProductRequest extends BaseRequest {

    String publisherId;

    List<String> categoryIds;

    String authorId;

    Float price;

    Integer quantity;

    Double globalRating;

    Integer numberOfPlayers;

    Integer minAge;

    Double weight;

    Integer minTime;

    Integer maxTime;

    String language;

    List<AdminAddImageRequest> images;

    // filter
    String categoryId;

    Integer toAge;
    Integer fromAge;

    Integer minQuality;
    Integer maxQuality;
}
