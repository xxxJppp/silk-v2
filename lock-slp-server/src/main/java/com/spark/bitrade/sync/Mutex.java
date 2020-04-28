package com.spark.bitrade.sync;

/**
 * 互斥锁
 *
 * @author Archx[archx@foxmail.com]
 * at 2019-07-14 16:13
 */
public interface Mutex {

    /**
     * 获取锁
     *
     * @param key          Key
     * @param expireSecond 过期时间 -1 不过期
     * @return bool
     */
    boolean lock(String key, long expireSecond);

    /**
     * 解锁
     *
     * @param key key
     */
    void unlock(String key);
}
