package com.spark.bitrade.consumer.task.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constant.SlpProcessStatus;
import com.spark.bitrade.constant.SlpStatus;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.consumer.task.AbstractTask;
import com.spark.bitrade.consumer.task.UpdateTaskHelper;
import com.spark.bitrade.consumer.task.util.TaskMessageUtils;
import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.entity.LockSlpUpdateTask;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.mq.BuildTaskMessage;
import com.spark.bitrade.mq.TaskMessage;
import com.spark.bitrade.service.LockSlpMemberSummaryService;
import com.spark.bitrade.service.LockSlpReleasePlanService;
import com.spark.bitrade.service.LockSlpUpdateTaskService;
import com.spark.bitrade.service.SlpReleaseOperationService;
import com.spark.bitrade.util.SpringContextUtil;
import io.shardingsphere.api.HintManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 生成参与者更新任务
 */
@Slf4j
@Component("buildActorUpdateTask")
public class BuildActorUpdateTask extends AbstractTask<BuildTaskMessage, LockSlpReleasePlan, LockSlpUpdateTask>
        implements UpdateTaskHelper {

    private LockSlpReleasePlanService slpReleasePlanService;
    private LockSlpUpdateTaskService slpUpdateTaskService;
    private LockSlpMemberSummaryService slpMemberSummaryService;
    private SlpReleaseOperationService slpReleaseOperationService;

    @Override
    public LockSlpMemberSummaryService getSlpMemberSummaryService() {
        return slpMemberSummaryService;
    }

    @Override
    public AbstractTask<BuildTaskMessage, LockSlpReleasePlan, LockSlpUpdateTask> getServiceBean() {
        return SpringContextUtil.getBean(this.getClass());
    }

    @Override
    public LockSlpReleasePlan convert(BuildTaskMessage message) {
        long id = NumberUtils.toLong(message.getRefId(), 0);

        // 重试三次

        try {
            HintManager.getInstance().setMasterRouteOnly();
            LockSlpReleasePlan plan = slpReleasePlanService.getById(id);//FuncWrapUtil.retryFunc(() -> slpReleasePlanService.getById(id), 3);

            if (plan == null) {
                log.warn("未找到本金返还计划 [ id = {} ] ", message.getRefId());
                return null;
            }

            log.info("开始处理本金返还计划 [ id = {} ]", message.getRefId());
            return plan;
        } catch (RuntimeException ex) {
            log.error("查找本金返还计划 [ id = {} ] 出错, err = '{}'", message.getRefId(), ex.getMessage());
        }
        return null;
    }

    @Override
    public boolean check(LockSlpReleasePlan prev) {
        QueryWrapper<LockSlpUpdateTask> query = new QueryWrapper<>();
        query.eq("deep", 1).eq("ref_plan_id", prev.getId());
        // 不处理条件
        return prev.getTaskStatus() == SlpProcessStatus.NOT_PROCESSED && slpUpdateTaskService.count(query) == 0;
    }

    @Override
    public List<LockSlpUpdateTask> execute(LockSlpReleasePlan prev, BuildTaskMessage msg) {
        // 生成更新任务
        LockSlpUpdateTask task = new LockSlpUpdateTask();

        task.setId(IdWorker.getId());
        task.setMemberId(prev.getMemberId());
        task.setCoinUnit(prev.getCoinUnit());

        task.setRefInviteesId(null);
        slpReleaseOperationService.getSlpMemberPromotion(prev.getMemberId()).ifPresent(promotion -> task.setInviterId(promotion.getInviterId()));

        task.setRefLockMemberId(prev.getMemberId());
        task.setRefLockDetailId(prev.getRefLockDetailId());
        task.setRefPlanId(prev.getId());

//        LockSlpMemberSummary summary = slpMemberSummaryService.initAndGet(task, prev);
//        task.setCurrentPromotionCount(summary.getPromotion());
//        task.setCurrentSubLevelCount(slpMemberSummaryService.countCurrentSubLevel(summary));
//        task.setCurrentPerformanceAmount(summary.getTotalSubValidAmount());
//        task.setCurrentLevelId(summary.getCurrentLevelId());
//        task.setCurrentLevelName(summary.getCurrentLevelName());

        // set summary
        setupSummary(task);

        task.setDeep(1);
        task.setStatus(SlpStatus.NOT_PROCESSED);
        task.setReleaseTaskStatus(SlpProcessStatus.NOT_PROCESSED);
        task.setComment("锁仓触发任务");

        Date now = getNow();
        task.setCreateTime(now);
        task.setUpdateTime(now);

        boolean save = slpUpdateTaskService.save(task);

        if (!save) {
            log.error("写入更新任务记录表 id = {}, result = {}", task.getId(), save);
            throw new MessageCodeException(CommonMsgCode.FAILURE);
        }

        log.info("生成更新任务记录 [ id = {}, plan_id = {} ]", task.getId(), prev.getId());
        return Collections.singletonList(task);
    }

    @Override
    public List<LockSlpUpdateTask> processed(LockSlpReleasePlan prev, BuildTaskMessage msg) {
        log.info("本金返还计划 [ id = {} ] 已处理", prev.getId());
        QueryWrapper<LockSlpUpdateTask> query = new QueryWrapper<>();
        query.eq("deep", 1).eq("ref_plan_id", prev.getId());
        return slpUpdateTaskService.list(query);
    }

    @Override
    public boolean update(LockSlpReleasePlan plan) {
        // 更新计划状态
        UpdateWrapper<LockSlpReleasePlan> update = new UpdateWrapper<>();
        update.eq("id", plan.getId()).set("task_status", SlpProcessStatus.PROCESSED);// FIXME 无更新时间戳字段 .set("update_time", getNow());

        boolean result =  slpReleasePlanService.update(update);
        log.info("更新本金返还计划任务处理状态 [ id = {}, result = {} ]", plan.getId(), result);
        return result;
    }

    @Override
    public List<TaskMessage> next(List<LockSlpUpdateTask> next, BuildTaskMessage msg) {
        LockSlpUpdateTask task = null;

        if (next.size() == 1 && (task = next.get(0)) != null) {
            log.info("构建处理更新计划任务 [ ref_id = {}, plan_id = {} ]", task.getId(), task.getRefPlanId());
            return TaskMessage.wrap(TaskMessageUtils.buildUpdateTask(task,
                    new ArrayList<>(Collections.singletonList(task.getMemberId()))));
        }
        log.error("未构建处理更新计划任务，请检查处理流程");
        return null;
    }

    // ------------------------------
    // SETTERS
    // ------------------------------

    @Autowired
    public void setSlpReleasePlanService(LockSlpReleasePlanService slpReleasePlanService) {
        this.slpReleasePlanService = slpReleasePlanService;
    }

    @Autowired
    public void setSlpUpdateTaskService(LockSlpUpdateTaskService slpUpdateTaskService) {
        this.slpUpdateTaskService = slpUpdateTaskService;
    }

    @Autowired
    public void setSlpMemberSummaryService(LockSlpMemberSummaryService slpMemberSummaryService) {
        this.slpMemberSummaryService = slpMemberSummaryService;
    }

    @Autowired
    public void setSlpReleaseOperationService(SlpReleaseOperationService slpReleaseOperationService) {
        this.slpReleaseOperationService = slpReleaseOperationService;
    }

}
