package com.spark.bitrade.support;

/**
 * ArchiveTask
 *
 * @author Pikachu
 * @since 2019/11/4 11:22
 */
public interface ArchiveTask extends ArchiveTrigger {

    /**
     * 具体执行方法
     */
    void execute();
}
