package org.example.board_game.core.client.domain.dto.request.product;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.admin.domain.dto.request.product.AdminAddImageRequest;
import org.example.board_game.core.common.base.BaseRequest;
import org.example.board_game.infrastructure.enums.ProductStatus;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientProductRequest extends BaseRequest {

    @NotNull(message = "Nhà phát hành không được bỏ trống.")
    @NotBlank(message = "Nhà phát hành không được bỏ trống.")
    String publisherId;

    List<String> categoryIds;

    @NotNull(message = "Tác giả không được bỏ trống.")
    @NotBlank(message = "Tác giả không được bỏ trống.")
    String authorId;

    @NotNull(message = "Giá không được để trống.")
    @Positive(message = "Giá phải lớn hơn 0.")
    Float price;

    @NotNull(message = "Số lượng không được để trống.")
    @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1.")
    Integer quantity;

    @NotNull(message = "Xếp hạng toàn cầu không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Xếp hạng phải từ 0.0 trở lên.")
    @DecimalMax(value = "5.0", inclusive = true, message = "Xếp hạng không được lớn hơn 5.0.")
    Double globalRating;

    @NotNull(message = "Số người chơi không được để trống.")
    @Min(value = 1, message = "Số người chơi phải lớn hơn hoặc bằng 1.")
    Integer numberOfPlayers;

    @NotNull(message = "Tuổi tối thiểu không được để trống.")
    @Min(value = 1, message = "Tuổi tối thiểu phải lớn hơn hoặc bằng 1.")
    Integer minAge;

    @NotNull(message = "Độ khó không được để trống.")
    @DecimalMin(value = "1.0", message = "Độ khó phải từ 1.0 trở lên.")
    @DecimalMax(value = "5.0", message = "Độ khó không được lớn hơn 5.0.")
    Double weight;


    @NotNull(message = "Thời gian chơi tối thiểu không được để trống.")
    @Min(value = 1, message = "Thời gian chơi tối thiểu phải lớn hơn 0.")
    Integer minTime;

    @NotNull(message = "Thời gian chơi tối đa không được để trống.")
    @Min(value = 1, message = "Thời gian chơi tối đa phải lớn hơn 0.")
    Integer maxTime;

    @NotNull(message = "Ngôn ngữ không được bỏ trống.")
    @NotBlank(message = "Ngôn ngữ không được bỏ trống.")
    String language;

    String description;

    ProductStatus status;

    List<AdminAddImageRequest> images;

    // filter
    String categoryId;

    Integer toAge;
    Integer fromAge;

    Integer minQuality;
    Integer maxQuality;
}
