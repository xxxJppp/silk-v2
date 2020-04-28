package com.spark.bitrade.consumer.task.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constant.LockRewardSatus;
import com.spark.bitrade.constant.SlpProcessStatus;
import com.spark.bitrade.constant.SlpStatus;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.LSMsgCode;
import com.spark.bitrade.constants.LockSlpConstant;
import com.spark.bitrade.consumer.task.AbstractTask;
import com.spark.bitrade.consumer.task.LockSlpTaskHelper;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.entity.LockSlpReleasePlanRecord;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.mq.BuildTaskMessage;
import com.spark.bitrade.mq.BuildTaskMessageType;
import com.spark.bitrade.mq.TaskMessage;
import com.spark.bitrade.service.ILockService;
import com.spark.bitrade.service.LockSlpReleasePlanRecordService;
import com.spark.bitrade.service.LockSlpReleasePlanService;
import com.spark.bitrade.service.SlpReleaseOperationService;
import com.spark.bitrade.util.FeignFunctionUtil;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 处理锁仓数据
 */
@Slf4j
@Component("handleLockSlpTask")
public class HandleLockSlpTask extends AbstractTask<BuildTaskMessage, LockCoinDetail, LockSlpReleasePlan>
        implements LockSlpTaskHelper {

    private ILockService lockService;
    private SlpReleaseOperationService slpReleaseOperationService;
    private LockSlpReleasePlanService slpReleasePlanService;
    private LockSlpReleasePlanRecordService slpReleasePlanRecordService;

    @Override
    public AbstractTask<BuildTaskMessage, LockCoinDetail, LockSlpReleasePlan> getServiceBean() {
        return SpringContextUtil.getBean(this.getClass());
    }

    @Override
    public LockCoinDetail convert(BuildTaskMessage message) {

        // 获取锁仓详情
        final long id = NumberUtils.toLong(message.getRefId());
        Optional<LockCoinDetail> optional = FeignFunctionUtil.get(() -> lockService.findLockCoinDetailById(id), (err) -> {
            log.error("获取锁仓详情失败: id = {}, code = {}, message = '{}'", message.getRefId(), err.getCode(), err.getMessage());
        });

        // 不存在或已返佣
        if (!optional.isPresent()) {
            log.warn("锁仓详情为空 [ id = {} ]", message.getRefId());
            return null;
        }
        log.info("开始处理锁仓数据 [ id = {} ]", message.getRefId());
        return optional.get();
    }

    @Override
    public boolean check(LockCoinDetail prev) {
        QueryWrapper<LockSlpReleasePlan> query = new QueryWrapper<>();
        // 该记录应该只有一条
        query.eq("ref_lock_detail_id", prev.getId());

        boolean exists = slpReleasePlanService.count(query) > 0;
        // 幂等性判断
        return prev.getLockRewardSatus() == LockRewardSatus.NO_REWARD && !exists;
    }

    @Override
    public List<LockSlpReleasePlan> execute(LockCoinDetail prev, BuildTaskMessage msg) {

        BigDecimal zoomScale = slpReleaseOperationService.getZoomScale();
        // 生成返回计划表
        LockSlpReleasePlan plan = buildLockSlpReleasePlan(prev, zoomScale, getNow());

        plan.setComment("锁仓数据处理");

        FeignFunctionUtil.get(() -> lockService.findLockSettingById(prev.getRefActivitieId()), (err) -> {
            log.error("获取锁仓详情失败: id = {}, code = {}, message = '{}'", prev.getRefActivitieId(), err.getCode(), err.getMessage());
            throw new MessageCodeException(err);
        }).ifPresent(setting -> plan.setPlanName(setting.getName()));


        boolean save = slpReleasePlanService.save(plan);

        if (!save) {
            log.info("写入本金返还记录表失败 detail_id = {},  plan_temp_id = {}", prev.getId(), plan.getId());
            throw new MessageCodeException(LSMsgCode.RECORD_TO_SAVE);
        }

        // T + 0 任务
        LockSlpReleasePlanRecord record = new LockSlpReleasePlanRecord();

        BigDecimal out = plan.getLockAmount().multiply(plan.getReleaseRate());
        BigDecimal alloc_proportion = slpReleaseOperationService.getAllocProportion();
        BigDecimal jackpotAmount = out.multiply(BigDecimal.ONE.subtract(alloc_proportion));
        BigDecimal releaseAmount = out.multiply(alloc_proportion);

        record.setId(IdWorker.getId());
        record.setMemberId(plan.getMemberId());
        record.setCoinUnit(plan.getCoinUnit());
        record.setPeriod(0);
        record.setReleaseTime(getNow());
        record.setAllocationProportion(alloc_proportion);

        // USDT
        record.setJackpotAmount(jackpotAmount);
        record.setReleaseAmount(releaseAmount);

        record.setReleaseRate(plan.getReleaseRate());
        record.setStatus(SlpStatus.PROCESSED);
        record.setRefPlanId(plan.getId());
        record.setReleaseTaskStatus(SlpProcessStatus.NOT_PROCESSED);
        record.setCreateTime(getNow());
        record.setUpdateTime(getNow());

        record.setCoinInUnit("SLP");
        record.setComment("T + 0 释放任务");

        if (slpReleasePlanRecordService.save(record)) {
            BuildTaskMessage msg_t0 = new BuildTaskMessage();
            msg_t0.setRefId(record.getId() + "");
            msg_t0.setType(BuildTaskMessageType.BUILD_INVITER_RELEASE_TASK);

            log.info("构建T+0生成推荐人加速任务 [ record_id = {}, member_id = {}, plan_id = {} ]",
                    record.getId(), record.getMemberId(), plan.getId());
            push(LockSlpConstant.KAFKA_MSG_BUILD_TASK, msg_t0.stringify());
        } else {
            log.error("生成T+0释放记录记录生成失败 [ plan_id = {}, member_id = {}]", plan.getId(), plan.getMemberId());
            throw new MessageCodeException(CommonMsgCode.FAILURE);
        }

        log.info("生成本金返还计划 [ id = {}, detail_id = {} ]", plan.getId(), prev.getId());
        return Collections.singletonList(plan);
    }

    @Override
    public List<LockSlpReleasePlan> processed(LockCoinDetail prev, BuildTaskMessage msg) {
        log.info("锁仓数据 [ id = {} ] 已处理 ", prev.getId());
        QueryWrapper<LockSlpReleasePlan> query = new QueryWrapper<>();

        // 该记录应该只有一条
        query.eq("ref_lock_detail_id", prev.getId());

        return slpReleasePlanService.list(query);
    }

    @Override
    public boolean update(LockCoinDetail detail) {
        // 更新返佣状态
        final long detailId = detail.getId();

        boolean result = FeignFunctionUtil.get(() -> lockService.updateRewardStatusToCompleteById(detailId), err -> {
            log.error("更新锁仓记录返佣状态失败: id = {}, code = {}, err = '{}'", detailId, err.getCode(), err.getMessage());
            throw new MessageCodeException(err);
        }).orElse(false);
        log.info("更新锁仓记录返佣状态结果 [ id = {}, result = {} ]", detailId, result);
        return result;
    }

    @Override
    public List<TaskMessage> next(List<LockSlpReleasePlan> next, BuildTaskMessage msg) {
        return buildNextTaskMessage(next);
    }

    @Override
    public void print(String pattern, Object... args) {
        log.info(pattern, args);
    }

    // ------------------------------
    // SETTERS
    // ------------------------------

    @Autowired
    public void setLockService(ILockService lockService) {
        this.lockService = lockService;
    }

    @Autowired
    public void setSlpReleaseOperationService(SlpReleaseOperationService slpReleaseOperationService) {
        this.slpReleaseOperationService = slpReleaseOperationService;
    }

    @Autowired
    public void setSlpReleasePlanService(LockSlpReleasePlanService slpReleasePlanService) {
        this.slpReleasePlanService = slpReleasePlanService;
    }

    @Autowired
    public void setSlpReleasePlanRecordService(LockSlpReleasePlanRecordService slpReleasePlanRecordService) {
        this.slpReleasePlanRecordService = slpReleasePlanRecordService;
    }
}
