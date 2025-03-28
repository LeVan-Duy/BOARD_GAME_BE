package org.example.board_game.core.common.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.common.base.BaseResponse;

import java.util.List;
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse extends BaseResponse {

    List<BaseResponse> categories;

    BaseResponse image;

    BaseResponse publisher;

    BaseResponse author;

    Float price;

    Double globalRating;

    Integer numberOfPlayers;
}
