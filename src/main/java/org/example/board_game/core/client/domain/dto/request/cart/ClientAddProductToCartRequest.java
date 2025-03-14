package org.example.board_game.core.client.domain.dto.request.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientAddProductToCartRequest {

    @NotBlank(message = "Vui lòng chọn sản phẩm bạn muốn thêm vào giỏ hàng.")
    String productId;

    @NotNull(message = "Vui lòng nhập số lượng bạn muốn thêm vào giỏ hàng.")
    @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1.")
    int quantity;
}
