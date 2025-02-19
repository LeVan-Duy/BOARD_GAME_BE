package org.example.board_game.core.admin.domain.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.common.base.BaseRequest;
import org.example.board_game.infrastructure.enums.ProductStatus;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminProductRequest extends BaseRequest {

    @NotNull(message = "Nhà phát hành không được bỏ trống.")
    @NotBlank(message = "Nhà phát hành không được bỏ trống.")
    String publisherId;

    List<String> categoryIds;

    @NotNull(message = "Tác giả không được bỏ trống.")
    @NotBlank(message = "Tác giả không được bỏ trống.")
    String authorId;

    Float price;

    Integer quantity;

    Double globalRating;

    Integer numberOfPlayers;

    Integer minAge;

    // độ khó
    Double weight;

    Integer minTime;

    Integer maxTime;

    @NotNull(message = "Ngôn ngữ không được bỏ trống.")
    @NotBlank(message = "Ngôn ngữ không được bỏ trống.")
    String language;

    String description;

    ProductStatus status;
}
