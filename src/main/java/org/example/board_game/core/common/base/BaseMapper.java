package org.example.board_game.core.common.base;

import java.util.List;

public interface BaseMapper <T, E, R>  {

    E toEntity(R request);

    T toResponse(E entity);

    List<E> toEntityList(List<R> requests);

    List<T> toResponseList(List<E> entities);
}
