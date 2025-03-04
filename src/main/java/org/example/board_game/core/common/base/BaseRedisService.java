package org.example.board_game.core.common.base;


import java.util.List;
import java.util.Map;

public interface BaseRedisService {

    void set(String key, String value);

    Object get(String key);

    void delete(String key);

    boolean exists(String key);

    boolean hashExists(String key, String field);

    void setTimeToLive(String key, long timeOutInDay);

    void hashSet(String key, String field, Object value);

    void delete(String key, String field);

    void delete(String key, List<String> fields);

    Map<String, Object> getField(String key);

    Object hasGet(String key, String field);


}
