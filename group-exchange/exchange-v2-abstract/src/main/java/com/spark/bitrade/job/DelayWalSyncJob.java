package com.spark.bitrade.job;

/**
 * DelayWalSyncJob
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/11 10:39
 */
public interface DelayWalSyncJob {

    /**
     * 发送同步通知
     *
     * @param memberId 会员id
     * @param coinUnit 币种
     */
    void sync(Long memberId, String coinUnit);
}
