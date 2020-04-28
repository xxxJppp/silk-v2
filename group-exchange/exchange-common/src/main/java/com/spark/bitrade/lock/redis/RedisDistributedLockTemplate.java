package com.spark.bitrade.lock.redis;

import com.spark.bitrade.lock.Callback;
import com.spark.bitrade.lock.DistributedLockTemplate;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的分布式锁实现类
 *
 * @author yangch
 * @since 2019.01.15 18:17 
 */
@Slf4j
public class RedisDistributedLockTemplate implements DistributedLockTemplate {

    private final JedisPool jedisPool;

    /**
     * 锁ID前缀
     */
    private String lockIdPrefix = "locked:";

    public RedisDistributedLockTemplate(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void setLockIdPrefix(String lockIdPrefix) {

        this.lockIdPrefix = lockIdPrefix;
    }

    @Override
    public <T> T execute(String lockId, int timeout, Callback<T> callback) {
        // prefix
        lockId = lockIdPrefix + lockId;

        try (RedisReentrantLock lock = new RedisReentrantLock(jedisPool, lockId)) {
            if (lock.tryLock(timeout, TimeUnit.MILLISECONDS)) {
                return callback.onGetLock();
            } else {
                return callback.onTimeout();
            }
        } catch (Exception ex) {
            log.error("lock execute exception", ex);
            throw new RuntimeException(ex);
        }
    }
}
