package org.example.board_game.utils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.common.PageableRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginationUtil {
    static final String ASC = "asc";
    static final String DESC = "desc";

    PaginationUtil() {}

    public static Pageable pageable(PageableRequest paginationRequest) {
        if (paginationRequest == null) {
            throw new IllegalArgumentException("Pagination request cannot be null");
        }

        Sort.Direction direction = paginationRequest.getOrderBy().equals(ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, paginationRequest.getSortBy());

        return PageRequest.of(paginationRequest.getPage(), paginationRequest.getPageSize(), sort);
    }
}
