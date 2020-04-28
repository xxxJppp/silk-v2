package com.spark.bitrade.service;

import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.vo.JobReceipt;
import com.spark.bitrade.vo.SlpReleaseJobParam;

import java.util.List;

/**
 * LockSlpReleaseService
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/9 10:19
 */
public interface LockSlpReleaseService {

    /**
     * 获取指定时间待处理的计划
     *
     * @param param 日期时间字符串,间隔
     * @return 计划列表
     */
    List<LockSlpReleasePlan> getPendingReleasePlan(SlpReleaseJobParam param);

    /**
     * 执行每日释放
     *
     * @param plan     返回计划
     * @param datetime 日期时间
     * @return 回执
     */
    JobReceipt doRelease(LockSlpReleasePlan plan, String datetime);


    /**
     * 执行释放完成检查任务
     *
     * @return 回执
     */
    JobReceipt doReleaseCompleteCheck();
}
