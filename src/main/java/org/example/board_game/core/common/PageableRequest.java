package org.example.board_game.core.common;

import lombok.Getter;
import lombok.Setter;
import org.example.board_game.infrastructure.constants.PaginationConstant;

@Getter
@Setter
public abstract class PageableRequest {
    private int page = PaginationConstant.DEFAULT_PAGE;
    private int pageSize = PaginationConstant.DEFAULT_SIZE;
    private String orderBy = PaginationConstant.DEFAULT_ORDER_BY;
    private String sortBy = PaginationConstant.DEFAULT_SORT_BY;
    private String q = PaginationConstant.DEFAULT_Q;
}