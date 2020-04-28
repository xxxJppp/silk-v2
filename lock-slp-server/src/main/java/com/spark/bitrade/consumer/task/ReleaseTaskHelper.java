package com.spark.bitrade.consumer.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constant.SlpProcessStatus;
import com.spark.bitrade.constant.SlpReleaseType;
import com.spark.bitrade.constant.SlpStatus;
import com.spark.bitrade.constants.LockSlpConstant;
import com.spark.bitrade.consumer.task.util.TaskMessageUtils;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.mq.ReleaseRecordMessage;
import com.spark.bitrade.mq.ReleaseTaskMessageType;
import com.spark.bitrade.mq.TaskMessage;
import com.spark.bitrade.service.LockSlpMemberSummaryService;
import com.spark.bitrade.service.LockSlpReleaseTaskRecordService;
import com.spark.bitrade.service.SlpReleaseOperationService;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ReleaseTaskHelper
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/10 11:25
 */
public interface ReleaseTaskHelper {

    String TASK_MESSAGE_PREFIX = "TASK:MESSAGES:";

    SlpReleaseOperationService getSlpReleaseOperationService();

    LockSlpReleaseTaskRecordService getLockSlpReleaseTaskRecordService();

    LockSlpMemberSummaryService getLockSlpMemberSummaryService();

    StringRedisTemplate getStringRedisTemplate();


    /**
     * 构建释放记录
     *
     * @param task    当前正在处理的释放任务
     * @param summary 当前会员实时统计
     * @param ratio   收益比例
     * @param type    释放类型
     * @return record
     */
    default LockSlpReleaseTaskRecord buildLockSlpReleaseTaskRecord(LockSlpReleaseTask task,
                                                                   LockSlpMemberSummary summary,
                                                                   BigDecimal ratio, SlpReleaseType type) {

        SlpReleaseOperationService slpReleaseOperationService = getSlpReleaseOperationService();
        LockSlpReleaseTaskRecordService slpReleaseTaskRecordService = getLockSlpReleaseTaskRecordService();

        // 如果是太阳，不烧伤，释放数 = 前一个释放任务id 对应的 释放记录 里面的releaseAmount
        // 我的释放数
        BigDecimal releaseAmount = BigDecimal.ZERO;
        // 释放比例
        BigDecimal releaseRate = task.getLockRate();
        if (task.getType() == SlpReleaseType.RELEASE_SUN) {
            // 获取释放任务
            // LockSlpReleaseTaskRecord lastRecord = slpReleaseTaskRecordService.getOne(
            //        new QueryWrapper<LockSlpReleaseTaskRecord>().eq("task_id", task.getRefLastTaskId()));
            // releaseAmount = lastRecord.getReleaseAmount();
            releaseAmount = task.getReleaseAmount().multiply(ratio);
        } else {
            // 【2.烧伤】
            //  若prev.getLockAmount（下级锁仓币数）> 我的锁仓币数：我的释放y =  下级锁仓币数 * 我的每日释放比例 * 收益比例
            //  反之： 我的释放y = 下级收益 * 收益比例

            // 我的释放数
            releaseAmount = task.getLockAmount().multiply(task.getLockRate()).multiply(ratio);

            // 烧伤
            if (task.getLockAmount().compareTo(summary.getMaxLockAmount()) > 0) {
                releaseAmount = task.getLockAmount().multiply(task.getMyLockRate()).multiply(ratio);
                releaseRate = task.getMyLockRate();
            }
        }

        // 如果用户未参加活动（释放数为0），则不生成记录
        if (releaseAmount.compareTo(BigDecimal.ZERO) < 1) {
            return null;
        }

        // 【2.y * 10% 放入奖池】

        BigDecimal alloc_proportion = slpReleaseOperationService.getAllocProportion();
        BigDecimal jackpotAmount = releaseAmount.multiply(BigDecimal.ONE.subtract(alloc_proportion)); // 10%
        releaseAmount = releaseAmount.multiply(alloc_proportion); // 90%

        LockSlpReleaseTaskRecord record = new LockSlpReleaseTaskRecord();
        record.setId(IdWorker.getId());
        record.setMemberId(task.getMemberId());
        record.setCoinUnit(task.getCoinUnit());
        record.setType(type);
        record.setAllocationProportion(alloc_proportion);
        record.setReleaseAmount(releaseAmount);
        record.setJackpotAmount(jackpotAmount);
        record.setReleaseRate(releaseRate);
        record.setStatus(SlpStatus.NOT_PROCESSED);
        record.setRefPlanId(task.getRefPlanId());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        record.setComment(type.getCnName() + "加速释放");
        record.setTaskId(task.getId());

        record.setRefInviteesId(task.getRefInviteesId());
        record.setRefLockMemberId(task.getRefLockMemberId());

        record.setCoinInUnit("SLP");

        // SLP 异步处理
        // String coinInUnit = slpReleaseOperationService.getCoinInUnit();
        // BigDecimal rate2Usdt = slpReleaseOperationService.getYesterdayExchangeRate2Usdt(coinInUnit);
        // record.setCoinInUnit(coinInUnit);
        // record.setReleaseInRate(rate2Usdt);
        // record.setReleaseInAmount(releaseAmount.multiply(rate2Usdt));
        // record.setJackpotInAmount(jackpotAmount.multiply(rate2Usdt));

        return record;
    }

    /**
     * 获取上级
     *
     * @param inviterId 推荐人
     * @param coinUnit 币种
     * @return sumary
     */
    default LockSlpMemberSummary getInviterSummary(long inviterId, String coinUnit) {
        // 获取上级的上级
        Long superiorId = getSlpReleaseOperationService().getSlpMemberPromotion(inviterId)
                .map(SlpMemberPromotion::getInviterId).orElse(null);
        return getLockSlpMemberSummaryService().getAndInit(inviterId, superiorId, coinUnit);
    }

    /**
     * 创建下一个任务
     *
     * @param prev              前一个任务
     * @param isSun             是否是太阳
     * @param inviteSummary     下一个人汇总信息
     * @param inviteLevelConfig 下一个人等级配置
     * @param record            记录
     * @return task
     */
    default LockSlpReleaseTask createNextTask(LockSlpReleaseTask prev, boolean isSun,
                                              LockSlpMemberSummary inviteSummary,
                                              LockSlpReleaseLevelConfig inviteLevelConfig,
                                              LockSlpReleaseTaskRecord record) {
        // 推荐人节点级差占比
        BigDecimal releaseRate = inviteLevelConfig.getRelaseRate();


        // 生成上一级加速释放任务
        LockSlpReleaseTask next = new LockSlpReleaseTask();
        next.setId(IdWorker.getId());
        next.setMemberId(prev.getInviterId());
        next.setCoinUnit(prev.getCoinUnit());
        next.setRefInviteesId(prev.getMemberId());

        // slpReleaseOperationService.getSlpMemberPromotion(prev.getMemberId()).ifPresent(promotion -> next.setInviterId(promotion.getInviterId()));
        next.setInviterId(inviteSummary.getInviterId());

        // 子部门中的最大奖励率x = prev中级差奖励率、子部门中的最大奖励率 取最大
        if (prev.getRewardRate().compareTo(prev.getSubMaxRewardRate()) > 0) {
            next.setSubMaxRewardRate(prev.getRewardRate());
        } else {
            next.setSubMaxRewardRate(prev.getSubMaxRewardRate());
        }

        // 级差奖励率y： 若 next的占比 - x <=0 -》1% ，反之：级差奖励率y = next的占比 - x
        // 如果y != 1%, 平级出现次数与prev相等，反之加一
        if (releaseRate.subtract(next.getSubMaxRewardRate()).compareTo(BigDecimal.ZERO) < 1) {
            next.setRewardRate(inviteLevelConfig.getPeersRate()); // 平级奖励百分比
            next.setPeersTimes(prev.getPeersTimes() + 1);
            next.setType(SlpReleaseType.RELEASE_PEERS);// 释放类型：平级
        } else {
            next.setRewardRate(releaseRate.subtract(next.getSubMaxRewardRate()));
            next.setPeersTimes(prev.getPeersTimes());
            next.setType(SlpReleaseType.RELEASE_CROSS);// 释放类型：级差
        }

        next.setRefLockMemberId(prev.getRefLockMemberId());// 始终保持不变
        next.setReleaseAmount(prev.getReleaseAmount()); // 始终保持不变
        next.setJackpotAmount(prev.getJackpotAmount()); // 始终保持不变
        next.setRefLockDetailId(prev.getRefLockDetailId());// 始终保持不变
        next.setRefPlanId(prev.getRefPlanId());
        next.setLockAmount(prev.getLockAmount());
        next.setLockRate(prev.getLockRate());


        // 太阳奖励下级收益处理
        if (isSun) {
            next.setType(SlpReleaseType.RELEASE_SUN);
            if (record == null) {
                next.setReleaseAmount(BigDecimal.ZERO);
                next.setJackpotAmount(BigDecimal.ZERO);
            } else {
                next.setReleaseAmount(record.getReleaseAmount());
                next.setJackpotAmount(record.getJackpotAmount());
            }
        }

        next.setMyLockRate(inviteSummary.getMaxReleaseRate());
        next.setCurrentPromotionCount(inviteSummary.getPromotion());
        next.setCurrentPerformanceAmount(inviteSummary.getTotalSubValidAmount());
        next.setCurrentLevelId(Long.valueOf(inviteLevelConfig.getLevelId()));
        next.setCurrentLevelName(inviteLevelConfig.getLevelName());
        next.setCurrentReleaseRate(inviteSummary.getMaxReleaseRate());//加速释放奖励率 = 烧伤机制：我的每日释放比例
        next.setDeep(prev.getDeep() + 1);

        next.setStatus(SlpProcessStatus.NOT_PROCESSED);
        next.setCreateTime(new Date());
        next.setUpdateTime(new Date());
        // next.setComment("社区节点加速释放,释放类型-" + next.getType().getCnName());
        next.setRefLastTaskId(prev.getId());

        return next;
    }

    /**
     * 添加释放记录处理任务
     *
     * @param record record
     */
    default void addReleaseRecordMessage(LockSlpReleaseTaskRecord record) {
        ReleaseRecordMessage msg = new ReleaseRecordMessage();
        msg.setRefId(record.getId() + "");
        msg.setType(record.getType());

        // 存放10s
        final String key = TASK_MESSAGE_PREFIX + record.getTaskId();

        StringRedisTemplate template = getStringRedisTemplate();
        template.opsForList().rightPush(key, msg.stringify());
        template.expire(key, 30, TimeUnit.SECONDS);

        // return msg.toTaskMessage(LockSlpConstant.KAFKA_MSG_RELEASE_RECORD_TASK);
    }

    /**
     * 获取对应的消息
     *
     * @param taskId 任务ID
     * @return messages
     */
    default List<TaskMessage> getReleaseRecordMessage(Long taskId) {
        final String key = TASK_MESSAGE_PREFIX + taskId;

        ListOperations<String, String> opsForList = getStringRedisTemplate().opsForList();
        Long size = opsForList.size(key);

        List<TaskMessage> messages = new ArrayList<>();
        if (size != null && size > 0) {
            while (size-- > 0) {
                ReleaseRecordMessage msg = JSON.parseObject(opsForList.leftPop(key), ReleaseRecordMessage.class);
                if (msg != null) {
                    messages.add(msg.toTaskMessage(LockSlpConstant.KAFKA_MSG_RELEASE_RECORD_TASK));
                }
            }
        }
        return messages;
    }

    default List<TaskMessage> getReleaseRecordMessage(List<LockSlpReleaseTask> tasks) {
        List<TaskMessage> messages = new ArrayList<>();

        if (tasks != null) {
            for (LockSlpReleaseTask task : tasks) {
                messages.addAll(getReleaseRecordMessage(task.getId()));
            }
        }

        return messages;
    }

    default List<TaskMessage> getNextSunTaskMessages(List<LockSlpReleaseTask> next, ArrayList<Long> chain) {
        // 无后续处理
        List<TaskMessage> messages = getReleaseRecordMessage(next);

        if (next != null && next.size() == 2) {
            LockSlpReleaseTask task = next.get(1);
            chain.add(task.getMemberId());
            messages.add(TaskMessageUtils.buildReleaseTask(task, ReleaseTaskMessageType.HANDLE_SUN_RELEASE_TASK, chain));
        }

        return messages;
    }
}
