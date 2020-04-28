package com.spark.bitrade.consumer.task.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constant.SlpProcessStatus;
import com.spark.bitrade.constant.SlpStatus;
import com.spark.bitrade.constants.LSMsgCode;
import com.spark.bitrade.consumer.task.AbstractTask;
import com.spark.bitrade.consumer.task.UpdateTaskHelper;
import com.spark.bitrade.consumer.task.util.TaskMessageUtils;
import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.entity.LockSlpUpdateTask;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.job.util.FuncWrapUtil;
import com.spark.bitrade.mq.TaskMessage;
import com.spark.bitrade.mq.UpdateTaskMessage;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 处理更新任务
 */

@Slf4j
@Component("handleUpdateTask")
public class HandleUpdateTask extends AbstractTask<UpdateTaskMessage, LockSlpUpdateTask, LockSlpUpdateTask>
        implements UpdateTaskHelper {

    private LockSlpUpdateTaskService slpUpdateTaskService;
    private LockSlpMemberSummaryService slpMemberSummaryService;
    private LockSlpReleasePlanService slpReleasePlanService;
    private SlpReleaseOperationService slpReleaseOperationService;

    @Override
    public LockSlpMemberSummaryService getSlpMemberSummaryService() {
        return slpMemberSummaryService;
    }

    @Override
    public AbstractTask<UpdateTaskMessage, LockSlpUpdateTask, LockSlpUpdateTask> getServiceBean() {
        return SpringContextUtil.getBean(this.getClass());
    }

    @Override
    public LockSlpUpdateTask convert(UpdateTaskMessage message) {

        // 获取加速释放任务
        final long id = NumberUtils.toLong(message.getRefId(), 0);
        try {
            HintManager.getInstance().setMasterRouteOnly();
            LockSlpUpdateTask task = slpUpdateTaskService.getById(id);//FuncWrapUtil.retryFunc(() -> slpUpdateTaskService.getById(id), 3);

            if (task == null) {
                log.warn("未找到更新任务 [ id = {} ] ", message.getRefId());
                return null;
            }

            log.info("开始处理更新任务 [ id = {} ]", message.getRefId());
            return task;
        } catch (RuntimeException ex) {
            log.error("查找更新任务 [ id = {} ] 出错, err = '{}'", message.getRefId(), ex.getMessage());
        }
        return null;

        // LockSlpUpdateTask task = slpUpdateTaskService.getById(NumberUtils.toLong(message.getRefId()));
    }

    @Override
    public boolean check(LockSlpUpdateTask prev) {
        return prev.getStatus() == SlpStatus.NOT_PROCESSED;
        // 雨女无瓜 && prev.getReleaseTaskStatus() == SlpProcessStatus.NOT_PROCESSED;
    }

    @Override
    public List<LockSlpUpdateTask> execute(LockSlpUpdateTask prev, UpdateTaskMessage msg) {
        // 更新奖励实时统计表

        // 查找返还计划
        Long refPlanId = prev.getRefPlanId();
        LockSlpReleasePlan plan = slpReleasePlanService.getById(refPlanId);

        // 升级锁仓数据社区等级
        slpMemberSummaryService.updateLockSlpMemberSummary(prev, plan);

        // 中断递归条件
        // 推荐人ID为NULL 或 循环关系链中存在推荐人
        if (FuncWrapUtil.isNone(prev.getInviterId()) || msg.getAcyclicRecommendChain().contains(prev.getInviterId())) {
            log.warn("推荐人ID为NULL 或 循环关系链中存在推荐人, [ deep = {}, chainIds  = {} ] ", prev.getDeep(), Arrays.toString(msg.getAcyclicRecommendChain().toArray()));
            return null;
        }

        // 生成递归任务

        LockSlpUpdateTask next = new LockSlpUpdateTask();
        next.setId(IdWorker.getId());
        next.setMemberId(prev.getInviterId());
        next.setCoinUnit(prev.getCoinUnit());
        next.setRefInviteesId(prev.getMemberId());

        slpReleaseOperationService.getSlpMemberPromotion(prev.getInviterId()).ifPresent(promotion -> next.setInviterId(promotion.getInviterId()));

        next.setRefLockMemberId(prev.getRefLockMemberId()); // 始终保持不变
        next.setRefLockDetailId(prev.getRefLockDetailId()); // 始终保持不变
        next.setRefPlanId(prev.getRefPlanId());


//        LockSlpMemberSummary summary = slpMemberSummaryService.initAndGet(next, plan);
//
//        next.setCurrentPromotionCount(summary.getPromotion());
//        next.setCurrentSubLevelCount(slpMemberSummaryService.countCurrentSubLevel(summary));
//        next.setCurrentPerformanceAmount(summary.getTotalSubValidAmount());
//        next.setCurrentLevelId(summary.getCurrentLevelId());
//        next.setCurrentLevelName(summary.getCurrentLevelName());
        setupSummary(next);

        next.setDeep(prev.getDeep() + 1);
        next.setStatus(SlpStatus.NOT_PROCESSED);
        next.setReleaseTaskStatus(SlpProcessStatus.NOT_PROCESSED);
        next.setComment(prev.getComment());
        next.setCreateTime(getNow());

        if (!slpUpdateTaskService.save(next)) {
            log.error("处理更新任务保存失败 prev_id = {}, temp_id = {}", prev.getId(), next.getId());
            throw new MessageCodeException(LSMsgCode.RECORD_TO_SAVE);
        }

        log.info("生成处理更新任务 [ id = {}, prev_id = {}, plan_id = {} ]", next.getId(), prev.getId(), plan.getId());
        return Collections.singletonList(next);
    }

    @Override
    public List<LockSlpUpdateTask> processed(LockSlpUpdateTask prev, UpdateTaskMessage msg) {
        // 查询 next
        // 只有一条
        log.info("更新任务 [ id = {} ] 已处理， 查询下一条任务", prev.getId());
        QueryWrapper<LockSlpUpdateTask> query = new QueryWrapper<>();
        query.eq("deep", prev.getDeep() + 1)
                .eq("coin_unit", prev.getCoinUnit())
                .eq("member_id", prev.getInviterId())
                .eq("ref_plan_id", prev.getRefPlanId());
        return slpUpdateTaskService.list(query);
    }

    @Override
    public boolean update(LockSlpUpdateTask prev) {
        Long id = prev.getId();
        UpdateWrapper<LockSlpUpdateTask> update = new UpdateWrapper<>();

        update.eq("id", id).set("status", SlpStatus.PROCESSED).set("update_time", getNow());

        boolean result = slpUpdateTaskService.update(update);
        log.info("更新处理更新任务状态 [ id = {}, result = {} ]", prev.getId(), result);

        return result;
    }

    @Override
    public List<TaskMessage> next(List<LockSlpUpdateTask> next, UpdateTaskMessage msg) {
        LockSlpUpdateTask task = null;

        if (next.size() == 1 && (task = next.get(0)) != null) {
            ArrayList<Long> chainIds = msg.getAcyclicRecommendChain();
            chainIds.add(task.getMemberId());

            log.info("生成下一条更新任务 [ id = {}, chainIds = {} ]", task.getId(), Arrays.toString(chainIds.toArray()));
            return TaskMessage.wrap(TaskMessageUtils.buildUpdateTask(task, chainIds));
        }

        log.warn("未生成下一条更新任务， 请检查处理流程");
        return null;
    }

    // ------------------------------
    // SETTERS
    // ------------------------------

    @Autowired
    public void setSlpUpdateTaskService(LockSlpUpdateTaskService slpUpdateTaskService) {
        this.slpUpdateTaskService = slpUpdateTaskService;
    }

    @Autowired
    public void setSlpMemberSummaryService(LockSlpMemberSummaryService slpMemberSummaryService) {
        this.slpMemberSummaryService = slpMemberSummaryService;
    }

    @Autowired
    public void setSlpReleasePlanService(LockSlpReleasePlanService slpReleasePlanService) {
        this.slpReleasePlanService = slpReleasePlanService;
    }

    @Autowired
    public void setSlpReleaseOperationService(SlpReleaseOperationService slpReleaseOperationService) {
        this.slpReleaseOperationService = slpReleaseOperationService;
    }
}
