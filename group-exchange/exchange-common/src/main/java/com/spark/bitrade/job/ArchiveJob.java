package com.spark.bitrade.job;

/**
 * 归档任务
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/25 14:12
 */
public interface ArchiveJob<T> {

    /**
     * Job名称
     *
     * @return name
     */
    String getName();

    /**
     * 获取一个任务
     *
     * @return t
     */
    T fetch();

    /**
     * 启动任务
     */
    void start();

    /**
     * 停止任务
     */
    void stop();
}
