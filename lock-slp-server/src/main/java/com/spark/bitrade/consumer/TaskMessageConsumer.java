package com.spark.bitrade.consumer;

/**
 * 消费者接口
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/3 11:31
 */
public interface TaskMessageConsumer<M> {

    void consume(M message);
}
