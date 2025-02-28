package org.example.board_game.core.admin.domain.dto.request.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminUploadMainImageRequest {

    @NotBlank(message = "Không được bỏ trống Id sản phẩm.")
    @NotNull(message = "Không được bỏ trống Id sản phẩm.")
    String productId;

    String url;

    String id;

    boolean newImage;
}
