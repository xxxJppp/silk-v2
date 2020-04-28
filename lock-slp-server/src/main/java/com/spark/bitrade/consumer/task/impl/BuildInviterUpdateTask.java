package com.spark.bitrade.consumer.task.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constant.SlpProcessStatus;
import com.spark.bitrade.constants.LSMsgCode;
import com.spark.bitrade.consumer.task.AbstractTask;
import com.spark.bitrade.entity.LockSlpReleasePlanRecord;
import com.spark.bitrade.entity.LockSlpUpdateTask;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.mq.BuildTaskMessage;
import com.spark.bitrade.mq.TaskMessage;
import com.spark.bitrade.service.LockSlpReleasePlanRecordService;
import com.spark.bitrade.service.LockSlpUpdateTaskService;
import com.spark.bitrade.util.SpringContextUtil;
import com.spark.bitrade.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 生成直接推荐人的更新任务
 * <p>
 * 已废弃
 */
@Slf4j
@Component("buildInviterUpdateTask")
@Deprecated
public class BuildInviterUpdateTask extends AbstractTask<BuildTaskMessage, LockSlpReleasePlanRecord, LockSlpUpdateTask> {

    private LockSlpReleasePlanRecordService slpReleasePlanRecordService;
    private LockSlpUpdateTaskService slpUpdateTaskService;

    @Autowired
    public void setSlpReleasePlanRecordService(LockSlpReleasePlanRecordService slpReleasePlanRecordService) {
        this.slpReleasePlanRecordService = slpReleasePlanRecordService;
    }

    @Autowired
    public void setSlpUpdateTaskService(LockSlpUpdateTaskService slpUpdateTaskService) {
        this.slpUpdateTaskService = slpUpdateTaskService;
    }

    @Override
    public AbstractTask<BuildTaskMessage, LockSlpReleasePlanRecord, LockSlpUpdateTask> getServiceBean() {
        return SpringContextUtil.getBean(this.getClass());
    }

    @Override
    public LockSlpReleasePlanRecord convert(BuildTaskMessage message) {
        Optional<Long> of = StringUtil.of(message.getRefId(), Long.class);
        // 获取《本金返还记录表》中的记录
        return of.map(id -> slpReleasePlanRecordService.getById(id)).orElse(null);
    }

    @Override
    public boolean check(LockSlpReleasePlanRecord prev) {
        // 幂等性判断，判断“更新任务状态”为“已处理”，则跳过以下3、4
        return false; // prev.getReleaseTaskStatus() == SlpProcessStatus.NOT_PROCESSED;
    }

    @Override
    public List<LockSlpUpdateTask> execute(LockSlpReleasePlanRecord prev, BuildTaskMessage msg) {
        // 生成更新任务
        LockSlpUpdateTask next = new LockSlpUpdateTask();

        next.setId(IdWorker.getId());
        next.setCoinUnit(prev.getCoinUnit());
        next.setDeep(0);
        next.setCreateTime(getNow());

        // TODO fill fields value

        if (!slpUpdateTaskService.save(next)) {
            log.error("生成直接推荐人的更新任务失败 plan_record_id = {}, temp_id = {}", prev.getId(), next.getId());
            throw new MessageCodeException(LSMsgCode.RECORD_TO_SAVE);
        }

        return Collections.singletonList(next);
    }

    @Override
    public List<LockSlpUpdateTask> processed(LockSlpReleasePlanRecord prev, BuildTaskMessage msg) {
        return null;
    }

    @Override
    public boolean update(LockSlpReleasePlanRecord prev) {
        // 更新《本金返还记录表》记录的“更新任务状态=1-已处理”
        UpdateWrapper<LockSlpReleasePlanRecord> update = new UpdateWrapper<>();
        update.set("release_task_status", SlpProcessStatus.PROCESSED).set("update_time", getNow()).eq("id", prev.getId());
        return slpReleasePlanRecordService.update(update);
    }

    @Override
    public List<TaskMessage> next(List<LockSlpUpdateTask> next, BuildTaskMessage msg) {
        return new ArrayList<>();
    }
}
