package org.example.board_game.core.admin.domain.dto.request.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.common.base.BaseRequest;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCategoryRequest extends BaseRequest {
}
