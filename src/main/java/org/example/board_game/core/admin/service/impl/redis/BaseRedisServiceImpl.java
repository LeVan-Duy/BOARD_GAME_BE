package org.example.board_game.core.admin.service.impl.redis;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.example.board_game.core.common.base.BaseRedisService;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BaseRedisServiceImpl implements BaseRedisService {

    RedisTemplate<String, Object> redisTemplate;
    HashOperations<String, String, Object> hashOps;

    public BaseRedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOps = redisTemplate.opsForHash();
    }

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public boolean hashExists(String key, String field) {
        return hashOps.hasKey(key, field);
    }

    @Override
    public void setTimeToLive(String key, long timeOutInDay) {
        redisTemplate.expire(key, timeOutInDay, TimeUnit.SECONDS);
    }

    @Override
    public void hashSet(String key, String field, Object value) {
        hashOps.put(key, field, value);
    }

    @Override
    public void delete(String key, String field) {
        hashOps.delete(key, field);
    }

    @Override
    public void delete(String key, List<String> fields) {
        hashOps.delete(key, fields);
    }

    @Override
    public Map<String, Object> getField(String key) {
        return hashOps.entries(key);
    }

    @Override
    public Object hasGet(String key, String field) {
        return hashOps.hasKey(key, field);
    }
}
