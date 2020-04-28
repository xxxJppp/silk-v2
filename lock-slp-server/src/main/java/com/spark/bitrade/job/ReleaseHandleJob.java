package com.spark.bitrade.job;

import com.spark.bitrade.entity.LockSlpReleasePlanRecord;
import com.spark.bitrade.entity.LockSlpReleaseTaskRecord;

/**
 * 释放处理任务
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/15 14:06
 */
public interface ReleaseHandleJob {

    /**
     * 处理每日释放记录
     *
     * @param record record
     */
    void execute(LockSlpReleasePlanRecord record);

    /**
     * 处理加速释放记录
     *
     * @param record record
     */
    void execute(LockSlpReleaseTaskRecord record);
}
