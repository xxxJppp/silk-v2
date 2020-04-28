package com.spark.bitrade.consumer.task;

import com.spark.bitrade.entity.LockSlpMemberSummary;
import com.spark.bitrade.entity.LockSlpUpdateTask;
import com.spark.bitrade.service.LockSlpMemberSummaryService;

/**
 * UpdateTaskHelper
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/10 11:04
 */
public interface UpdateTaskHelper {

    LockSlpMemberSummaryService getSlpMemberSummaryService();


    default void setupSummary(LockSlpUpdateTask task) {
        LockSlpMemberSummary summary = getSlpMemberSummaryService().getAndInit(task.getMemberId(), task.getInviterId(), task.getCoinUnit());

        task.setCurrentPromotionCount(summary.getPromotion());

        task.setCurrentSubLevelCount(getSlpMemberSummaryService().countCurrentSubLevel(summary));

        task.setCurrentPerformanceAmount(summary.getTotalSubValidAmount());
        task.setCurrentLevelId(summary.getCurrentLevelId());
        task.setCurrentLevelName(summary.getCurrentLevelName());
    }
}
