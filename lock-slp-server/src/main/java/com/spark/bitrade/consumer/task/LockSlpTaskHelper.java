package com.spark.bitrade.consumer.task;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constant.SlpProcessStatus;
import com.spark.bitrade.constant.SlpStatus;
import com.spark.bitrade.constants.LockSlpConstant;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.mq.BuildTaskMessage;
import com.spark.bitrade.mq.BuildTaskMessageType;
import com.spark.bitrade.mq.TaskMessage;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * LockSlpTaskHelper
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/25 14:21
 */
public interface LockSlpTaskHelper {

    default LockSlpReleasePlan buildLockSlpReleasePlan(LockCoinDetail detail, BigDecimal zoomScale, Date now) {
        // 生成返回计划表
        LockSlpReleasePlan plan = new LockSlpReleasePlan();
        plan.setId(IdWorker.getId());
        plan.setMemberId(detail.getMemberId());
        plan.setCoinUnit(detail.getCoinUnit());
        plan.setRefLockDetailId(detail.getId());
        plan.setLockAmount(detail.getTotalAmount());
        plan.setRealAmount(detail.getTotalAmount());

        // 注意后台添加设置 BigDecimal zoomScale = new BigDecimal("3");
        BigDecimal income = detail.getTotalAmount().multiply(zoomScale);
        plan.setZoomScale(zoomScale);
        plan.setPlanIncome(income);
        plan.setRemainAmount(income);

        // 锁仓期数 * 每期天数 (固定 1天)
        int totalDays = detail.getLockCycle() * detail.getCycleDays();
        plan.setReleaseTotalTimes(totalDays);
        // 周期比例
        plan.setReleaseRate(new BigDecimal(detail.getCycleRatio()));

        plan.setReleaseCurrentTimes(0);
        plan.setReleaseCurrentDate(now);
        plan.setCreateTime(now);

        plan.setStatus(SlpStatus.NOT_PROCESSED);
        plan.setTaskStatus(SlpProcessStatus.NOT_PROCESSED);

        return plan;
    }

    default List<TaskMessage> buildNextTaskMessage(List<LockSlpReleasePlan> next) {
        LockSlpReleasePlan task = null;
        if (next.size() == 1 && (task = next.get(0)) != null) {
            BuildTaskMessage message = new BuildTaskMessage();
            message.setRefId(task.getId() + "");
            message.setType(BuildTaskMessageType.BUILD_ACTOR_UPDATE_TASK);

            print("构建本金返还计划任务 [ ref_id = {}, detail_id = {} ]", task.getId(), task.getRefLockDetailId());
            return TaskMessage.wrap(message.toTaskMessage(LockSlpConstant.KAFKA_MSG_BUILD_TASK));
        }

        return null;
    }

    void print(String pattern, Object ...args);
}
