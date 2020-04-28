package com.spark.bitrade.lock.redis;

//import com.google.common.collect.Maps;

import com.spark.bitrade.lock.DistributedReentrantLock;
import redis.clients.jedis.JedisPool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于Redis分布式并发锁实现类
 *  
 *
 * @author yangch
 * @since 2019.01.15 18:19
 */
public class RedisReentrantLock implements DistributedReentrantLock {

    //private final ConcurrentMap<Thread, LockData> threadData = Maps.newConcurrentMap();
    private final Map<Thread, LockData> threadData = Collections.synchronizedMap(new HashMap<Thread, LockData>());

    private RedisLockInternal internal;
    private String lockId;


    public RedisReentrantLock(JedisPool jedisPool, String lockId) {
        this.lockId = lockId;
        this.internal = new RedisLockInternal(jedisPool);
    }

    private static class LockData {

        final Thread owningThread;
        final String lockVal;
        final AtomicInteger lockCount = new AtomicInteger(1);

        private LockData(Thread owningThread, String lockVal) {
            this.owningThread = owningThread;
            this.lockVal = lockVal;
        }
    }

    @Override
    public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {

        Thread currentThread = Thread.currentThread();
        LockData lockData = threadData.get(currentThread);

        if (lockData != null) {
            lockData.lockCount.incrementAndGet();
            return true;
        }

        String lockVal = internal.lock(lockId, timeout, unit);
        if (lockVal != null) {
            LockData newLockData = new LockData(currentThread, lockVal);
            threadData.put(currentThread, newLockData);
            return true;
        }

        return false;
    }

    @Override
    public void unlock() {
        Thread currentThread = Thread.currentThread();
        LockData lockData = threadData.get(currentThread);

        if (lockData == null) {
            throw new IllegalMonitorStateException("You do not own the lock: " + lockId);
        }

        int newLockCount = lockData.lockCount.decrementAndGet();
        if (newLockCount > 0) {
            return;
        }

        if (newLockCount < 0) {
            throw new IllegalMonitorStateException("Lock count has gone negative for lock: " + lockId);
        }

        try {
            internal.unlock(lockId, lockData.lockVal);
        } finally {
            threadData.remove(currentThread);
        }
    }
}
