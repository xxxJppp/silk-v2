package com.spark.bitrade.lock.redis;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
class RedisLockInternal {

    private final static char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z'};

    private JedisPool jedisPool;

    /**
     * 重试等待时间, 单位：毫秒
     */
    private int retryAwait = 5;

    /**
     * 锁定时间, 30秒
     */
    private int lockTimeout = 30000;


    RedisLockInternal(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public RedisLockInternal(JedisPool jedisPool, int retryAwait, int lockTimeout) {
        this.jedisPool = jedisPool;
        this.retryAwait = retryAwait;
        this.lockTimeout = lockTimeout;
    }

    String lock(String lockId, long time, TimeUnit unit) {
        final long startMillis = System.currentTimeMillis();
        final long millisToWait = (unit != null) ? unit.toMillis(time) : 0;
        String lockValue = null;

        while (true) {
            lockValue = doLock(lockId);
            if (lockValue != null) {
                break;
            }
            if (System.currentTimeMillis() - startMillis - retryAwait > millisToWait) {
                break;
            }
            // 等待
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(retryAwait));
        }
        return lockValue;
    }

    void unlock(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            // jedis = jedisPool.getResource();
            String luaScript = ""
                    + "\nlocal v = redis.call('GET', KEYS[1]);"
                    + "\nlocal r= 0;"
                    + "\nif v == ARGV[1] then"
                    + "\nr =redis.call('DEL',KEYS[1]);"
                    + "\nend"
                    + "\nreturn r";

            List<String> keys = new ArrayList<String>();
            keys.add(key);

            List<String> args = new ArrayList<String>();
            args.add(value);

            Object r = jedis.eval(luaScript, keys, args);
        } catch (RuntimeException ex) {
            log.error("unlock redis exception [ key = {}, value = {}, err = {}]", key, value, ex.getMessage());
        }
    }

    private String doLock(String lockId) {

        try (Jedis jedis = jedisPool.getResource()) {
            String value = lockId + randomId(1);

            String luaScript = ""
                    + "\nlocal r = tonumber(redis.call('SETNX', KEYS[1],ARGV[1]));"
                    + "\nredis.call('PEXPIRE',KEYS[1],ARGV[2]);"
                    + "\nreturn r";
            List<String> keys = new ArrayList<String>();
            keys.add(lockId);
            List<String> args = new ArrayList<String>();
            args.add(value);
            args.add(lockTimeout + "");
            Long ret = (Long) jedis.eval(luaScript, keys, args);
            if (new Long(1).equals(ret)) {
                return value;
            }
        } catch (RuntimeException ex) {
            log.error("do lock exception [ lockId = {}, err = {} ]", lockId, ex.getMessage());
        }
        return null;
    }


    private String randomId(int size) {
        char[] cs = new char[size];
        for (int i = 0; i < cs.length; i++) {
            cs[i] = digits[ThreadLocalRandom.current().nextInt(digits.length)];
        }
        return new String(cs);
    }

}
