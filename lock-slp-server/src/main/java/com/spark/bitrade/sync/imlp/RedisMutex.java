package com.spark.bitrade.sync.imlp;

import com.spark.bitrade.sync.Mutex;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * RedisMutex
 *
 * @author Archx[archx@foxmail.com]
 * at 2019-07-14 16:17
 */
public class RedisMutex implements Mutex {

    private StringRedisTemplate redisTemplate;

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean lock(String key, long expireSecond) {
        Long incr = redisTemplate.opsForValue().increment("key", 1);

        if (incr != null && incr == 1) {
            redisTemplate.expire(key, expireSecond, TimeUnit.SECONDS);
            return true;
        }

        return false;
    }

    @Override
    public void unlock(String key) {
        redisTemplate.delete(key);
    }
}
