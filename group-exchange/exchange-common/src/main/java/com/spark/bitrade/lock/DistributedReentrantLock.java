package com.spark.bitrade.lock;

import java.util.concurrent.TimeUnit;

/**
 * 分布式并发锁接口
 *
 * @author yangch
 * @since 2019.01.15 18:09
 */
public interface DistributedReentrantLock extends AutoCloseable {

    boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException;

    void unlock();

    @Override
    default void close() throws Exception {
        unlock();
    }
}
