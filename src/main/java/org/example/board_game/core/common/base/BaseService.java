package org.example.board_game.core.common.base;


import org.example.board_game.core.common.PageableObject;
import org.example.board_game.utils.Response;

public interface BaseService<T, K, R> {

    Response<PageableObject<T>> findAll(R request);

    Response<Object> create(R request);

    Response<Object> update(R request, K id);

    Response<T> findById(K id);

    Response<Object> delete(K id);
}
