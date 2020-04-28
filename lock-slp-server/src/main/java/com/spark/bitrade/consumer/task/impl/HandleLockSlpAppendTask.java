package com.spark.bitrade.consumer.task.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.spark.bitrade.constant.LockRewardSatus;
import com.spark.bitrade.constant.LockStatus;
import com.spark.bitrade.constant.SlpStatus;
import com.spark.bitrade.constants.LSMsgCode;
import com.spark.bitrade.consumer.task.AbstractTask;
import com.spark.bitrade.consumer.task.LockSlpTaskHelper;
import com.spark.bitrade.dto.LockCoinDetailDto;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.mq.BuildTaskMessage;
import com.spark.bitrade.mq.TaskMessage;
import com.spark.bitrade.service.ILockService;
import com.spark.bitrade.service.LockSlpReleasePlanService;
import com.spark.bitrade.service.SlpReleaseOperationService;
import com.spark.bitrade.util.FeignFunctionUtil;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 加仓处理任务
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/25 13:46
 */
@Slf4j
@Component("handleLockSlpAppendTask")
public class HandleLockSlpAppendTask extends AbstractTask<BuildTaskMessage, LockCoinDetailDto, LockSlpReleasePlan>
        implements LockSlpTaskHelper {

    private ILockService lockService;
    private SlpReleaseOperationService slpReleaseOperationService;
    private LockSlpReleasePlanService slpReleasePlanService;

    @Override
    public AbstractTask<BuildTaskMessage, LockCoinDetailDto, LockSlpReleasePlan> getServiceBean() {
        return SpringContextUtil.getBean(getClass());
    }

    @Override
    public LockCoinDetailDto convert(BuildTaskMessage message) {

        JSONArray objects = JSON.parseArray(message.getRefId());
        if (objects.size() != 2) {
            log.error("加仓参数传入出错 [ refId = '{}' ", message.getRefId());
        }

        final long id1 = objects.getLongValue(0);
        final long id2 = objects.getLongValue(1);

        Optional<LockCoinDetail> prev = getDetail(id1);

        Optional<LockCoinDetail> append = getDetail(id2);

        if (!prev.isPresent() || !append.isPresent()) {
            log.error("缺失锁仓详情数据 id -> present : [ prev {} -> {},  append {} -> {} ], ",
                    id1, prev.isPresent(), id2, append.isPresent());
            return null;
        }

        log.info("开始处理加仓数据 [ prev_id = {}, append_id = {} ]", id1, id2);
        return new LockCoinDetailDto(prev.get(), append.get());
    }

    @Override
    public boolean check(LockCoinDetailDto dto) {

        LockCoinDetail append = dto.getAppend();
        LockCoinDetail prev = dto.getPrev();

        QueryWrapper<LockSlpReleasePlan> query = new QueryWrapper<>();
        // 该记录应该只有一条
        query.eq("ref_lock_detail_id", append.getId());
        boolean exists = slpReleasePlanService.count(query) > 0;

        // 幂等性判断
        boolean checked = prev.getStatus() == LockStatus.CANCLE
                && append.getLockRewardSatus() == LockRewardSatus.NO_REWARD
                && !exists;

        if (!checked) {
            log.error("加仓幂等性加仓未通过 [ prev_status = {}, append_lock_reward_status = {}, plan_exist = {}",
                    prev.getStatus(), append.getLockRewardSatus(), exists);
        }

        return checked;
    }

    @Override
    public List<LockSlpReleasePlan> execute(LockCoinDetailDto dto, BuildTaskMessage msg) {

        LockCoinDetail append = dto.getAppend();
        BigDecimal zoomScale = slpReleaseOperationService.getZoomScale();

        // 旧的计划
        QueryWrapper<LockSlpReleasePlan> query = new QueryWrapper<>();
        // 该记录应该只有一条
        query.eq("ref_lock_detail_id", dto.getPrev().getId());
        LockSlpReleasePlan oldPlan = slpReleasePlanService.getOne(query);

        // 生成返回计划表
        LockSlpReleasePlan plan = buildLockSlpReleasePlan(append, zoomScale, getNow());

        // 上一个计划已经释放的
        BigDecimal released = oldPlan.getPlanIncome().subtract(oldPlan.getRemainAmount());
        // 升仓到的套餐总数- 已解锁的
        plan.setRemainAmount(plan.getPlanIncome().subtract(released));

        // 业绩
        plan.setRealAmount(plan.getLockAmount().subtract(oldPlan.getLockAmount()));

        plan.setRefPrevId(oldPlan.getId());
        plan.setRefPrevPlanName(oldPlan.getPlanName());
        plan.setComment("加仓数据处理，前一条返回计划id = " + oldPlan.getId());

        FeignFunctionUtil.get(() -> lockService.findLockSettingById(append.getRefActivitieId()), (err) -> {
            log.error("获取加仓详情失败: id = {}, code = {}, message = '{}'", append.getRefActivitieId(), err.getCode(), err.getMessage());
            throw new MessageCodeException(err);
        }).ifPresent(setting -> plan.setPlanName(setting.getName()));

        // 修改上一个本金返还计划
        UpdateWrapper<LockSlpReleasePlan> update = new UpdateWrapper<>();
        update.eq("id", oldPlan.getId()).eq("status", SlpStatus.APPENDING);
        update.set("status", SlpStatus.APPEND_PROCESSED).set("comment", "加仓处理，新返回计划id = " + plan.getId());

        boolean result = slpReleasePlanService.save(plan) && slpReleasePlanService.update(update);

        if (!result) {
            log.info("加仓处理本金返还记录失败 detail_id = {},  plan_temp_id = {}", append.getId(), plan.getId());
            throw new MessageCodeException(LSMsgCode.RECORD_TO_SAVE);
        }

        log.info("生成加仓本金返还计划 [ id = {}, detail_id = {} ]", plan.getId(), append.getId());
        return Collections.singletonList(plan);
    }

    @Override
    public List<LockSlpReleasePlan> processed(LockCoinDetailDto dto, BuildTaskMessage msg) {
        log.info("加仓数据 [ id = {} ] 已处理 ", dto.getAppend().getId());
        QueryWrapper<LockSlpReleasePlan> query = new QueryWrapper<>();

        // 该记录应该只有一条
        query.eq("ref_lock_detail_id", dto.getAppend().getId());

        return slpReleasePlanService.list(query);
    }

    @Override
    public boolean update(LockCoinDetailDto prev) {
        // 更新返佣状态
        final long detailId = prev.getAppend().getId();

        boolean result = FeignFunctionUtil.get(() -> lockService.updateRewardStatusToCompleteById(detailId), err -> {
            log.error("更新加仓记录返佣状态失败: id = {}, code = {}, err = '{}'", detailId, err.getCode(), err.getMessage());
            throw new MessageCodeException(err);
        }).orElse(false);
        log.info("更新加仓记录返佣状态结果 [ id = {}, result = {} ]", detailId, result);
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

    private Optional<LockCoinDetail> getDetail(final long detailId) {
        return FeignFunctionUtil.get(() -> lockService.findLockCoinDetailById(detailId), (err) -> {
            log.error("获取锁仓详情失败: id = {}, code = {}, message = '{}'", detailId, err.getCode(), err.getMessage());
        });
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
}
