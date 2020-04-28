package com.spark.bitrade.consumer.task.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.spark.bitrade.constant.LockStatus;
import com.spark.bitrade.constant.SlpStatus;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.LockSlpConstant;
import com.spark.bitrade.consumer.task.AbstractTask;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.mq.BuildTaskMessage;
import com.spark.bitrade.mq.BuildTaskMessageType;
import com.spark.bitrade.mq.TaskMessage;
import com.spark.bitrade.service.ILockService;
import com.spark.bitrade.service.LockSlpReleasePlanService;
import com.spark.bitrade.util.FeignFunctionUtil;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 预处理加仓业务
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/25 16:50
 */
@Slf4j
@Component("handleLockSlpPreAppendTask")
public class HandleLockSlpPreAppendTask extends AbstractTask<BuildTaskMessage, LockCoinDetail, LockSlpReleasePlan> {

    private ILockService lockService;
    private LockSlpReleasePlanService slpReleasePlanService;

    @Override
    public AbstractTask<BuildTaskMessage, LockCoinDetail, LockSlpReleasePlan> getServiceBean() {
        return SpringContextUtil.getBean(getClass());
    }

    @Override
    public LockCoinDetail convert(BuildTaskMessage message) {
        // [id1,id2]
        JSONArray objects = JSON.parseArray(message.getRefId());
        if (objects.size() != 2) {
            log.error("加仓参数传入错误 [ refId = '{}' ", message.getRefId());
        }

        final long id1 = objects.getLongValue(0);
        final long id2 = objects.getLongValue(1);

        Optional<LockCoinDetail> prev = FeignFunctionUtil.get(() -> lockService.findLockCoinDetailById(id1), (err) -> {
            log.error("获取锁仓详情失败: id = {}, code = {}, message = '{}'", id1, err.getCode(), err.getMessage());
        });

        Optional<LockCoinDetail> append = FeignFunctionUtil.get(() -> lockService.findLockCoinDetailById(id2), (err) -> {
            log.error("获取锁仓详情失败: id = {}, code = {}, message = '{}'", id2, err.getCode(), err.getMessage());
        });

        if (!prev.isPresent()) {
            log.warn("前一个锁仓详情为空 [ id = {} ]", id1);
            return null;
        }

        if (!append.isPresent()) {
            log.warn("加仓锁仓详情为空 [ id = {} ]", id2);
            return null;
        }

        log.info("开始处理加仓数据 [ prev_id = {}, append_id = {} ]", id1, id2);
        return prev.get();
    }

    @Override
    public boolean check(LockCoinDetail prev) {
        // 前一条锁仓记录返回计划状态为未处理

        QueryWrapper<LockSlpReleasePlan> query = new QueryWrapper<>();
        // 该记录应该只有一条
        query.eq("ref_lock_detail_id", prev.getId()).eq("status", SlpStatus.NOT_PROCESSED);
        boolean exists = slpReleasePlanService.count(query) > 0;

        return prev.getStatus() == LockStatus.CANCLE && exists;
    }

    @Override
    public List<LockSlpReleasePlan> execute(LockCoinDetail prev, BuildTaskMessage msg) {

        LockSlpReleasePlan plan = getPlan(prev.getId());

        log.info("开始加仓数据预处理 [ detail_id = {},  plan_id = {} ]", prev.getId(), plan.getId());

        UpdateWrapper<LockSlpReleasePlan> update = new UpdateWrapper<>();
        update.eq("id", plan.getId()).eq("status", SlpStatus.NOT_PROCESSED);
        update.set("status", SlpStatus.APPENDING);

        if (slpReleasePlanService.update(update)) {
            return Collections.singletonList(plan);
        }

        log.error("加仓预处理失败 [ detail_id = {}, plan_id = {}, plan_status = {} ]",
                prev.getId(), plan.getId(), plan.getStatus());
        throw new MessageCodeException(CommonMsgCode.FAILURE);
    }

    @Override
    public List<LockSlpReleasePlan> processed(LockCoinDetail prev, BuildTaskMessage msg) {
        LockSlpReleasePlan plan = getPlan(prev.getId());

        if (plan == null) {
            log.error("为获取到锁仓详情对应的返回计划 [ detail_id = {} ]", prev.getId());
            return Collections.emptyList();
        }

        return Collections.singletonList(plan);
    }

    @Override
    public boolean update(LockCoinDetail prev) {
        return true;
    }

    @Override
    public List<TaskMessage> next(List<LockSlpReleasePlan> next, BuildTaskMessage msg) {
        if (next.size() > 0) {
            BuildTaskMessage postMsg = new BuildTaskMessage();
            postMsg.setRefId(msg.getRefId());
            postMsg.setType(BuildTaskMessageType.LOCK_SLP_POST_APPEND_TASK);

            return TaskMessage.wrap(postMsg.toTaskMessage(LockSlpConstant.KAFKA_MSG_BUILD_TASK));
        }
        return null;
    }

    private LockSlpReleasePlan getPlan(long detailId) {
        QueryWrapper<LockSlpReleasePlan> query = new QueryWrapper<>();
        // 该记录应该只有一条
        query.eq("ref_lock_detail_id", detailId);

        return slpReleasePlanService.getOne(query);
    }

    // ------------------------------
    // SETTERS
    // ------------------------------

    @Autowired
    public void setLockService(ILockService lockService) {
        this.lockService = lockService;
    }

    @Autowired
    public void setSlpReleasePlanService(LockSlpReleasePlanService slpReleasePlanService) {
        this.slpReleasePlanService = slpReleasePlanService;
    }
}
