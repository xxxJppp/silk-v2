package com.spark.bitrade.job;

import com.spark.bitrade.vo.JobReceipt;

/**
 * 每日释放任务接口
 *
 * @author Archx[archx@foxmail.com]
 * at 2019-07-08 21:07
 */
public interface DailyReleaseJob {

    JobReceipt execute(Long id, String datetime);

    JobReceipt completeCheck();
}
