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
public class ClientBestSellerRequest extends BaseRequest {

  Long startDate;

  Long endDate;

}
