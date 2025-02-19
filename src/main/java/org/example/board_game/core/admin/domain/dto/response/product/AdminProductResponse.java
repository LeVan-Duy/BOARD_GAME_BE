package org.example.board_game.core.admin.domain.dto.response.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.common.base.BaseResponse;
import org.example.board_game.infrastructure.enums.ProductStatus;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminProductResponse extends BaseResponse {

    BaseResponse publisher;

    List<BaseResponse> categories;

    BaseResponse author;

    Float price;

    Integer quantity;

    Double globalRating;

    Integer numberOfPlayers;

    Integer minAge;

    // độ khó
    Double weight;

    Integer minTime;

    Integer maxTime;

    String language;

    String description;

    ProductStatus status;
}
