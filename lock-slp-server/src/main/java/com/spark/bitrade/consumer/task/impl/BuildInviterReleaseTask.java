package com.spark.bitrade.consumer.task.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constant.SlpProcessStatus;
import com.spark.bitrade.constant.SlpReleaseType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.LockSlpConstant;
import com.spark.bitrade.consumer.task.AbstractTask;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.job.util.FuncWrapUtil;
import com.spark.bitrade.mq.BuildTaskMessage;
import com.spark.bitrade.mq.ReleaseTaskMessage;
import com.spark.bitrade.mq.ReleaseTaskMessageType;
import com.spark.bitrade.mq.TaskMessage;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.SpringContextUtil;
import io.shardingsphere.api.HintManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * 生成直接推荐人加速释放任务
 */
@Slf4j
@Component("buildInviterReleaseTask")
public class BuildInviterReleaseTask extends AbstractTask<BuildTaskMessage, LockSlpReleasePlanRecord, LockSlpReleaseTask> {

    /*
【概述】
根据《本金返还记录表》的数据，构建及保存“加速释放任务”，并通过消息将更新任务广播出去。

【逻辑】
1、获取《本金返还记录表》中的记录
2、幂等性判断，判断“加速释放任务状态”为“已处理”，则跳过以下3、4
3、构建并保存推荐人的加速释放任务，包含直推任务和节点任务 2部分。
4、更新《本金返还记录表》记录的“加速释放任务状态=1-已处理”
5、广播 <推荐人节点奖励加速释放任务消息>
6、广播 <推荐人直推奖励加速释放任务消息>

【备注】
1、注意 幂等性处理和事务型的处理
     */

    private LockSlpReleasePlanService slpReleasePlanService;
    private LockSlpReleasePlanRecordService slpReleasePlanRecordService;
    private LockSlpReleaseTaskService slpReleaseTaskService;
    private LockSlpMemberSummaryService slpMemberSummaryService;
    private SlpReleaseOperationService slpReleaseOperationService;

    @Override
    public AbstractTask<BuildTaskMessage, LockSlpReleasePlanRecord, LockSlpReleaseTask> getServiceBean() {
        return SpringContextUtil.getBean(this.getClass());
    }

    @Override
    public LockSlpReleasePlanRecord convert(BuildTaskMessage message) {
        // 获取《本金返还记录表》中的记录
        final long id = NumberUtils.toLong(message.getRefId(), 0);
        try {
            HintManager.getInstance().setMasterRouteOnly();
            LockSlpReleasePlanRecord record = slpReleasePlanRecordService.getById(id);// FuncWrapUtil.retryFunc(() -> slpReleasePlanRecordService.getById(id), 3);
            if (record == null) {
                log.warn("未找到本金返还记录 [ id = {} ] ", message.getRefId());
                return null;
            }

            log.info("开始处理本金返还记录 [ id = {} ]", message.getRefId());
            return record;
        } catch (RuntimeException ex) {
            log.error("查找本金返还记录 [ id = {} ] 出错, err = '{}'", message.getRefId(), ex.getMessage());
        }
        return null;
    }

    @Override
    public boolean check(LockSlpReleasePlanRecord prev) {
        // 幂等性判断，判断“加速释放任务状态”为“已处理”，则跳过以下3、4
        return prev.getReleaseTaskStatus() == SlpProcessStatus.NOT_PROCESSED;
    }

    @Override
    public List<LockSlpReleaseTask> execute(LockSlpReleasePlanRecord prev, BuildTaskMessage msg) {
        log.info("生成直接推荐人加速释放任务开始，ref_plan_record_id-{}", prev.getId());
        // 构建并保存推荐人的加速释放任务

        Optional<SlpMemberPromotion> promotion = slpReleaseOperationService.getSlpMemberPromotion(prev.getMemberId());//.ifPresent(promotion -> task.setInviterId(promotion.getInviterId()));

        if (!promotion.isPresent()) {
            log.info("该用户-{}无上级，不生成加速释放任务", prev.getMemberId());
            return null;
        }
        // 获取前一任务目标的汇总的信息
        final LockSlpMemberSummary summary = slpMemberSummaryService.getAndInit(prev.getMemberId(), promotion.get().getInviterId(), prev.getCoinUnit());
        // 无上级则中断或上级为自己
        if (FuncWrapUtil.isNone(summary.getInviterId()) || summary.getInviterId() == prev.getMemberId().longValue()) {
            log.info("生成直接推荐人加速释放任务中断");
            return null;
        }

        // 获取上级的上级
        Long superiorId = slpReleaseOperationService.getSlpMemberPromotion(summary.getInviterId()).map(SlpMemberPromotion::getInviterId).orElse(null);

        // 释放计划
        LockSlpReleasePlan plan = slpReleasePlanService.getById(prev.getRefPlanId());

        // 上级的推荐关系实时汇总
        LockSlpMemberSummary superior = slpMemberSummaryService.getAndInit(summary.getInviterId(), superiorId, prev.getCoinUnit());

        // 直推
        LockSlpReleaseTask invite = buildTask(prev, SlpReleaseType.RELEASE_INVITE, plan, superior);
        if (!slpReleaseTaskService.save(invite)) {
            log.error("生成直接推荐人加速释放任务失败： plan_record = {}, temp_id = {}", prev.getId(), invite.getId());
            throw new MessageCodeException(CommonMsgCode.FAILURE);
        } else {
            log.info("生成直接推荐人加速释放任务成功：plan_record = {}, temp_id = {}", prev.getId(), invite.getId());
        }

        // 节点
        LockSlpReleaseTask cross = buildTask(prev, SlpReleaseType.RELEASE_CROSS, plan, superior);
        if (!slpReleaseTaskService.save(cross)) {
            log.error("生成节点推荐人加速释放任务失败： plan_record = {}, temp_id = {}", prev.getId(), cross.getId());
            throw new MessageCodeException(CommonMsgCode.FAILURE);
        } else {
            log.info("生成节点推荐人加速释放任务成功：plan_record = {}, temp_id = {}", prev.getId(), cross.getId());
        }

        // 太阳
        if (slpReleaseOperationService.isSunLevel(superior.getCurrentLevelId(), superior.getCoinUnit())) {
            LockSlpReleaseTask sun = buildTask(prev, SlpReleaseType.RELEASE_SUN, plan, superior);
            if (!slpReleaseTaskService.save(sun)) {
                log.error("生成太阳加速释放任务失败： plan_record = {}, temp_id = {}", prev.getId(), sun.getId());
                throw new MessageCodeException(CommonMsgCode.FAILURE);
            } else {
                log.info("生成太阳推荐人加速释放任务成功：plan_record = {}, temp_id = {}", prev.getId(), sun.getId());
                return Arrays.asList(invite, cross, sun);
            }
        }

        return Arrays.asList(invite, cross);
    }

    @Override
    public List<LockSlpReleaseTask> processed(LockSlpReleasePlanRecord prev, BuildTaskMessage msg) {
        log.info("生成直接推荐人加速释放任务已经处理，ref_plan_record_id-{}", prev.getId());
        // 查询 两个 next 任务
        QueryWrapper<LockSlpReleaseTask> query = new QueryWrapper<>();
        query.eq("ref_plan_id", prev.getId());

        return slpReleaseTaskService.list(query);
    }

    @Override
    public boolean update(LockSlpReleasePlanRecord prev) {
        log.info("更新前一个任务的状态: plan_record_id - {}", prev.getId());
        UpdateWrapper<LockSlpReleasePlanRecord> update = new UpdateWrapper<>();
        update.eq("id", prev.getId()).set("release_task_status", SlpProcessStatus.PROCESSED);
        update.set("update_time", getNow());
        boolean result = slpReleasePlanRecordService.update(update);
        if (!result) {
            log.info("更新前一个任务的状态失败: plan_record_id - {}", prev.getId());
        } else {
            log.info("更新前一个任务的成功: plan_record_id - {}", prev.getId());
        }
        return result;
    }

    @Override
    public List<TaskMessage> next(List<LockSlpReleaseTask> next, BuildTaskMessage msg) {

        // 构建并保存推荐人的加速释放任务，包含直推任务和节点任务 2部分。
        List<TaskMessage> tasks = new ArrayList<>();
        for (LockSlpReleaseTask task : next) {
            SlpReleaseType type = task.getType();

            ReleaseTaskMessageType rt = ReleaseTaskMessageType.HANDLE_COMMUNITY_RELEASE_TASK;

            if (type == SlpReleaseType.RELEASE_INVITE) {
                rt = ReleaseTaskMessageType.HANDLE_SHARE_RELEASE_TASK;
            } else if (type == SlpReleaseType.RELEASE_SUN) {
                rt = ReleaseTaskMessageType.HANDLE_SUN_RELEASE_TASK;
            }

            ReleaseTaskMessage message = new ReleaseTaskMessage();
            message.setRefId(task.getId() + "");
            message.setType(rt);

            ArrayList<Long> chainIds = new ArrayList<>();
            chainIds.add(task.getMemberId());
            message.setAcyclicRecommendChain(chainIds);

            log.info("生成加速释放任务消息 task_id = {}, type = {}, ref_plan_id = {} ", task.getId(), type.getCnName(), task.getRefPlanId());

            tasks.add(message.toTaskMessage(LockSlpConstant.KAFKA_MSG_RELEASE_TASK));
        }
        return tasks;
    }

    private LockSlpReleaseTask buildTask(LockSlpReleasePlanRecord prev, SlpReleaseType type,
                                         LockSlpReleasePlan plan, LockSlpMemberSummary summary) {
        log.info("构建加速释放任务：ref_plan_id - {}, type - {}", prev.getId(), type.getCnName());
        LockSlpReleaseTask next = new LockSlpReleaseTask();
        next.setId(IdWorker.getId());
        next.setMemberId(summary.getMemberId());
        next.setCoinUnit(prev.getCoinUnit());
        next.setRefInviteesId(prev.getMemberId());
        next.setInviterId(summary.getInviterId());
        next.setType(type);

        next.setRefLockMemberId(plan.getMemberId());
        next.setRefLockDetailId(plan.getRefLockDetailId());
        next.setRefPlanId(prev.getId());

        next.setReleaseAmount(prev.getReleaseAmount());
        next.setJackpotAmount(prev.getJackpotAmount());

        // 烧伤
        next.setLockAmount(plan.getLockAmount());
        next.setLockRate(plan.getReleaseRate());
        next.setMyLockRate(summary.getMaxReleaseRate());

        // 实时统计信息
        next.setCurrentLevelId(summary.getCurrentLevelId());
        next.setCurrentLevelName(summary.getCurrentLevelName());
        next.setCurrentPromotionCount(summary.getPromotion());
        next.setCurrentReleaseRate(summary.getMaxReleaseRate());
        next.setCurrentPerformanceAmount(summary.getTotalSubValidAmount());

        next.setDeep(1);

        // 初始配置为0
        next.setRewardRate(summary.getReleaseRate()); // 级差占比
        next.setSubMaxRewardRate(BigDecimal.ZERO);
        next.setPeersTimes(0);
        next.setStatus(SlpProcessStatus.NOT_PROCESSED);

        next.setCreateTime(new Date());
        next.setUpdateTime(new Date());
        next.setComment("加速任务：类型 -" + type.getCnName());
        log.info("构建加速释放任务成功：task_id - {}", next.getId());
        return next;
    }

    // --------------------------------
    // SETTERS ....
    // --------------------------------

    @Autowired
    public void setSlpReleasePlanService(LockSlpReleasePlanService slpReleasePlanService) {
        this.slpReleasePlanService = slpReleasePlanService;
    }

    @Autowired
    public void setSlpReleasePlanRecordService(LockSlpReleasePlanRecordService slpReleasePlanRecordService) {
        this.slpReleasePlanRecordService = slpReleasePlanRecordService;
    }

    @Autowired
    public void setSlpReleaseTaskService(LockSlpReleaseTaskService slpReleaseTaskService) {
        this.slpReleaseTaskService = slpReleaseTaskService;
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
