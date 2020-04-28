package com.spark.bitrade.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constant.LockStatus;
import com.spark.bitrade.constant.SlpProcessStatus;
import com.spark.bitrade.constant.SlpReleaseType;
import com.spark.bitrade.constant.SlpStatus;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.LockSlpConstant;
import com.spark.bitrade.dto.SlpMemberSummaryUpdateDto;
import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.entity.LockSlpReleasePlanRecord;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.job.util.DateUtil;
import com.spark.bitrade.job.util.FuncWrapUtil;
import com.spark.bitrade.mapper.LockSlpMemberSummaryMapper;
import com.spark.bitrade.mq.BuildTaskMessage;
import com.spark.bitrade.mq.BuildTaskMessageType;
import com.spark.bitrade.mq.ReleaseRecordMessage;
import com.spark.bitrade.mq.TaskMessageWrapper;
import com.spark.bitrade.service.ILockService;
import com.spark.bitrade.service.LockSlpReleasePlanRecordService;
import com.spark.bitrade.service.LockSlpReleasePlanService;
import com.spark.bitrade.service.SlpReleaseOperationService;
import com.spark.bitrade.util.FeignFunctionUtil;
import com.spark.bitrade.vo.JobReceipt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * DailyReleaseJobImpl
 *
 * @author Archx[archx@foxmail.com]
 * at 2019-07-08 21:11
 */
@Slf4j
@Service
public class DailyReleaseJobImpl implements DailyReleaseJob {

    private LockSlpMemberSummaryMapper summaryMapper;

    private LockSlpReleasePlanService slpReleasePlanService;
    private LockSlpReleasePlanRecordService slpReleasePlanRecordService;
    private SlpReleaseOperationService slpReleaseOperationService;

    private TaskMessageWrapper taskMessageWrapper;
    private ILockService lockService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public JobReceipt execute(Long id, String datetime) {

        log.info("定时任务处理开始 [ plan_id = {}, datetime = '{}' ]", id, datetime);

        // 将时间转换为前一天
        Date date = DateUtil.parse(datetime);
        Date before = DateUtil.getTheDayBeforeOf(date);

        // 获取记录
        Optional<LockSlpReleasePlan> optional = getPlan(id, before);
        if (!optional.isPresent()) {
            return JobReceipt.builder().success(false).build();
        }

        LockSlpReleasePlan plan = optional.get();

        // 检查本金返回记录表数据，不需要此方式，根据返还日期判断是否需要构建任务
        // LockSlpReleasePlanRecord record = slpReleasePlanRecordService.getRecord(id, plan.getReleaseCurrentTimes());

        // if (record != null) {
        //     log.info("任务 [ id = {}, period = {} ] 找到返回记录", id, plan.getReleaseCurrentTimes());
        //     if (record.getReleaseTaskStatus() != SlpProcessStatus.PROCESSED) {
        //         // 重新发起任务
        //         buildAndSendTask(record);
        //     }
        //     return JobReceipt.builder().recordId(record.getId() + "").success(true).build();
        //  }

        // 1.保存记录
        LockSlpReleasePlanRecord record = doRelease(plan);


        if (record != null) {
            // 2.奖池数据
            // slpJackpotService.add(record.getCoinUnit(), record.getJackpotAmount());

            buildAndSendTask(record);
            return JobReceipt.builder().recordId(record.getId() + "").success(true).build();
        }

        return JobReceipt.builder().success(false).message("已释放完毕或写入数据库失败").build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public JobReceipt completeCheck() {
        log.info("定时检查任务处理开始");

        QueryWrapper<LockSlpReleasePlan> query = new QueryWrapper<>();
        query.eq("status", SlpStatus.NOT_PROCESSED).eq("remain_amount", BigDecimal.ZERO);

        List<LockSlpReleasePlan> list = slpReleasePlanService.list(query);

        JobReceipt.JobReceiptBuilder job = JobReceipt.builder();

        for (LockSlpReleasePlan plan : list) {
            doComplete(plan);
        }
        job.success(true).message("处理完成 size = " + list.size());

        return job.build();
    }

    /**
     * 获取返回计划
     *
     * @param id id
     * @return option
     */
    private Optional<LockSlpReleasePlan> getPlan(Long id, Date before) {
        LockSlpReleasePlan plan = slpReleasePlanService.getById(id);

        if (plan == null || plan.getStatus() != SlpStatus.NOT_PROCESSED) {
            log.error("任务 [ id = {} ] 不存在或已完成", id);
            return Optional.empty();
        }

        // 上一次处理时间，默认情况下为昨天的时间
        Date releaseAt = plan.getReleaseCurrentDate();

        // 间隔24小时，需要处理
        if (releaseAt.before(before)) {
            return Optional.of(plan);
        }

        log.warn("任务 [ id = {} ] 不符合执行条件 [ createdAt = '{}', releasedAt = '{}' ]",
                id, DateUtil.format(plan.getCreateTime()), DateUtil.format(releaseAt));
        return Optional.empty();
    }

    /**
     * 处理释放
     *
     * @param plan 返回计划
     * @return 返回记录
     */
    private LockSlpReleasePlanRecord doRelease(LockSlpReleasePlan plan) {
        // 释放数量
        BigDecimal out = plan.getLockAmount().multiply(plan.getReleaseRate());

        // 比较剩余
        if (out.compareTo(plan.getRemainAmount()) > 0) {
            out = plan.getRemainAmount(); // 最后一次释放完
        }

        // 构建更新条件
        UpdateWrapper<LockSlpReleasePlan> update = new UpdateWrapper<>();
        update.eq("id", plan.getId());

        // 未完成标识
        boolean unprocessed = plan.getRemainAmount().compareTo(BigDecimal.ZERO) > 0;

        if (unprocessed) {
            int period = FuncWrapUtil.orElse(plan.getReleaseCurrentTimes(), 0) + 1; // 期数

            // 注意！！！
            // 此处字段只表示理论释放时间，每次 +1 天方便任务重做
            Date releaseAt = DateUtil.getTheDayAfter(plan.getReleaseCurrentDate());

            update.set("release_current_times", period);
            update.set("release_current_date", releaseAt);

            log.info("任务 [ id = {} ] ", plan.getId());

            // 更新状态
            if (slpReleasePlanService.update(update)) {

                BigDecimal alloc_proportion = slpReleaseOperationService.getAllocProportion();
                BigDecimal jackpotAmount = out.multiply(BigDecimal.ONE.subtract(alloc_proportion));
                BigDecimal releaseAmount = out.multiply(alloc_proportion);

                LockSlpReleasePlanRecord record = new LockSlpReleasePlanRecord();

                record.setId(IdWorker.getId());
                record.setMemberId(plan.getMemberId());
                record.setCoinUnit(plan.getCoinUnit());
                record.setPeriod(period);
                record.setReleaseTime(releaseAt);
                record.setAllocationProportion(alloc_proportion);

                // USDT
                record.setJackpotAmount(jackpotAmount);
                record.setReleaseAmount(releaseAmount);

                record.setReleaseRate(plan.getReleaseRate());
                record.setStatus(SlpStatus.NOT_PROCESSED);
                record.setRefPlanId(plan.getId());
                record.setReleaseTaskStatus(SlpProcessStatus.NOT_PROCESSED);
                record.setCreateTime(new Date());
                record.setUpdateTime(new Date());

                record.setCoinInUnit("SLP");

                // SLP
                // String coinInUnit = slpReleaseOperationService.getCoinInUnit();
                // BigDecimal rate = slpReleaseOperationService.getYesterdayExchangeRate2Usdt(coinInUnit);
                // record.setCoinInUnit(coinInUnit);
                // record.setReleaseInRate(rate);
                // record.setReleaseInAmount(releaseAmount.multiply(rate));
                // record.setJackpotInAmount(jackpotAmount.multiply(rate));


                if (slpReleasePlanRecordService.save(record)) {
                    return record;
                }

                // 修改账户,每日释放 同SLP一样交给异步任务处理
                // MessageRespResult<Boolean> resp = walletTradeClient.trade(record, WalletTradeClient.RType.DAILY);

            }

            log.error("返还记录生成失败 [ plan_id = {}, member_id = {}, period = {} ]", plan.getId(), plan.getMemberId(), period);
            throw new MessageCodeException(CommonMsgCode.FAILURE);
        }
        // 释放完成后第二次再处理状态？？
        else {
            doComplete(plan);
        }


        return null;
    }

    private void doComplete(LockSlpReleasePlan plan) {

        // 构建更新条件
        UpdateWrapper<LockSlpReleasePlan> update = new UpdateWrapper<>();
        update.eq("id", plan.getId());
        update.set("status", SlpStatus.PROCESSED); // 已释放完成

        // 更新汇总统计数据
        String pk = plan.getMemberId() + plan.getCoinUnit();
        SlpMemberSummaryUpdateDto dto = new SlpMemberSummaryUpdateDto(pk,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                DateUtil.getNow());

        if (!slpReleasePlanService.update(update) || summaryMapper.updateBySummaryDto(dto) <= 0) {
            log.error("更新返还计划状态和重置实时统计数据失败 [ plan_id = {}, member_id = {}, summary_id = '{}' ]",
                    plan.getId(), plan.getMemberId(), pk);
            throw new MessageCodeException(CommonMsgCode.FAILURE);
        }

        // 修改锁仓详情状态
        boolean result = FeignFunctionUtil.get(() -> lockService.updateStatusById(plan.getRefLockDetailId(), LockStatus.LOCKED, LockStatus.UNLOCKED), err -> {
            log.error("修改锁仓详情状态失败 [ plan_id = {}, detail_id = {}, err = '{}' ]", plan.getId(), plan.getRefLockDetailId(), err.getMessage());
            throw new MessageCodeException(err);
        }).orElse(false);

        if (!result) {
            log.error("修改锁仓详情状态失败 [ plan_id = {}, detail_id = {} ]", plan.getId(), plan.getRefLockDetailId());
            throw new MessageCodeException(CommonMsgCode.FAILURE);
        }
    }

    /**
     * 构建并下发任务
     *
     * @param record 释放记录
     */
    private void buildAndSendTask(LockSlpReleasePlanRecord record) {
        log.info("构建生成推荐人加速任务 [ record_id = {}, member_id = {} ]", record.getId(), record.getMemberId());
        BuildTaskMessage msg = new BuildTaskMessage();
        msg.setRefId(record.getId() + "");
        msg.setType(BuildTaskMessageType.BUILD_INVITER_RELEASE_TASK);
        // kafkaTemplate.send(LockSlpConstant.KAFKA_MSG_BUILD_TASK, msg.stringify());

        log.info("构建释放记录处理任务[ record_id = {}, member_id = {} ]", record.getId(), record.getMemberId());
        ReleaseRecordMessage msg1 = new ReleaseRecordMessage();
        msg1.setRefId(record.getId() + "");
        msg1.setType(SlpReleaseType.RELEASE_DAILY);
        // kafkaTemplate.send(LockSlpConstant.KAFKA_MSG_RELEASE_RECORD_TASK, msg1.stringify());

        taskMessageWrapper.add(record.getRefPlanId(), msg.toTaskMessage(LockSlpConstant.KAFKA_MSG_BUILD_TASK));
        taskMessageWrapper.add(record.getRefPlanId(), msg1.toTaskMessage(LockSlpConstant.KAFKA_MSG_RELEASE_RECORD_TASK));
    }


    @Autowired
    public void setSummaryMapper(LockSlpMemberSummaryMapper summaryMapper) {
        this.summaryMapper = summaryMapper;
    }

    @Autowired
    public void setSlpReleasePlanService(LockSlpReleasePlanService slpReleasePlanService) {
        this.slpReleasePlanService = slpReleasePlanService;
    }

    @Autowired
    public void setSlpReleasePlanRecordService(LockSlpReleasePlanRecordService slpReleasePlanRecordService) {
        this.slpReleasePlanRecordService = slpReleasePlanRecordService;
    }

    @Autowired
    public void setSlpReleaseOperationService(SlpReleaseOperationService slpReleaseOperationService) {
        this.slpReleaseOperationService = slpReleaseOperationService;
    }

    @Autowired
    public void setTaskMessageWrapper(TaskMessageWrapper taskMessageWrapper) {
        this.taskMessageWrapper = taskMessageWrapper;
    }

    @Autowired
    public void setLockService(ILockService lockService) {
        this.lockService = lockService;
    }
}
