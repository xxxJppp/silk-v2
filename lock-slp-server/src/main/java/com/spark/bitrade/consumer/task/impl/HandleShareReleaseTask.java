package com.spark.bitrade.consumer.task.impl;

import com.spark.bitrade.constant.SlpProcessStatus;
import com.spark.bitrade.constant.SlpReleaseType;
import com.spark.bitrade.constants.LSMsgCode;
import com.spark.bitrade.consumer.task.AbstractTask;
import com.spark.bitrade.consumer.task.ReleaseTaskHelper;
import com.spark.bitrade.entity.LockSlpMemberSummary;
import com.spark.bitrade.entity.LockSlpReleaseLevelConfig;
import com.spark.bitrade.entity.LockSlpReleaseTask;
import com.spark.bitrade.entity.LockSlpReleaseTaskRecord;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.job.util.FuncWrapUtil;
import com.spark.bitrade.mq.ReleaseTaskMessage;
import com.spark.bitrade.mq.TaskMessage;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.SpringContextUtil;
import io.shardingsphere.api.HintManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 直推任务
 */
@Slf4j
@Component("handleShareReleaseTask")
public class HandleShareReleaseTask extends AbstractTask<ReleaseTaskMessage, LockSlpReleaseTask, LockSlpReleaseTask>
        implements ReleaseTaskHelper {

    /*
【概述】
根据加速释放任务的数据，处理加速释放的逻辑；其次，根据推荐关系构建推荐人的加速任务，并通过消息将更新任务广播出去。

【逻辑】
1、根据通知内容，获取加速释放任务
2、幂等性判断，判断“记录状态”为“已处理”，则跳过以下3、4、5
3、处理分享收益加速释放的逻辑【需细化】
4、更新当前任务的“记录状态”为“1-已处理”
DEL 5、根据推荐关系构建及保存推荐人的加速释放任务
DEL 6、广播<推荐人社区奖励加速释放任务消息>

【备注】
1、注意 幂等性处理和事务型的处理
2、目前只有两代，所以只需要处理2级就可以了
     */


    private LockSlpReleaseTaskService slpReleaseTaskService;
    private LockSlpMemberSummaryService slpMemberSummaryService;
    private LockSlpReleaseTaskRecordService slpReleaseTaskRecordService;
    private LockSlpReleaseLevelConfigService slpReleaseLevelConfigService;
    private SlpReleaseOperationService slpReleaseOperationService;
    private StringRedisTemplate redisTemplate;

    @Override
    public SlpReleaseOperationService getSlpReleaseOperationService() {
        return slpReleaseOperationService;
    }

    @Override
    public LockSlpReleaseTaskRecordService getLockSlpReleaseTaskRecordService() {
        return slpReleaseTaskRecordService;
    }

    @Override
    public StringRedisTemplate getStringRedisTemplate() {
        return redisTemplate;
    }

    @Override
    public LockSlpMemberSummaryService getLockSlpMemberSummaryService() {
        return slpMemberSummaryService;
    }

    @Override
    public AbstractTask<ReleaseTaskMessage, LockSlpReleaseTask, LockSlpReleaseTask> getServiceBean() {
        return SpringContextUtil.getBean(this.getClass());
    }

    @Override
    public LockSlpReleaseTask convert(ReleaseTaskMessage message) {
        // 获取加速释放任务
        final long id = NumberUtils.toLong(message.getRefId(), 0);
        try {
            HintManager.getInstance().setMasterRouteOnly();
            LockSlpReleaseTask task = slpReleaseTaskService.getById(id); //FuncWrapUtil.retryFunc(() -> slpReleaseTaskService.getById(id), 3);

            if (task == null) {
                log.warn("未找到直推加速释放任务 [ id = {} ] ", message.getRefId());
                return null;
            }

            log.info("开始处理直推加速释放任务 [ id = {} ]", message.getRefId());
            return task;
        } catch (RuntimeException ex) {
            log.error("查找直推加速释放任务 [ id = {} ] 出错, err = '{}'", message.getRefId(), ex.getMessage());
        }
        return null;
        // 获取加速释放任务
        // return slpReleaseTaskService.getById(NumberUtils.toLong(message.getRefId(), 0));
    }

    @Override
    public boolean check(LockSlpReleaseTask prev) {
        //幂等性判断，判断“记录状态”为“已处理”，则跳过以下3、4、5
        return prev.getStatus() == SlpProcessStatus.NOT_PROCESSED;
    }

    /**
     * 1.烧伤：
     * 若prev.getLockAmount（下级锁仓币数）> 我的锁仓币数：我的释放y =  x * 我的每日释放比例 * 直推奖励比例
     * 反之：我的释放y = 下级收益 * 直推奖励比例10%
     * 2. y * 10% 放入奖池
     * 3. 存入lock_slp_release_task_record （待返还）
     * 4. 修改账户
     * 5. 修改lock_slp_release_task_record 状态（已返还）
     */
    @Override
    @Transactional
    public List<LockSlpReleaseTask> execute(LockSlpReleaseTask prev, ReleaseTaskMessage msg) {
        log.info("处理直推加速释放任务开始：ref_task_id - {}", prev.getId());
        // 推荐人是自己时不处理
        if (prev.getMemberId() == prev.getInviterId().longValue()) {
            log.info("直推加速释放处理递归中断");
            return null;
        }

        // 我的锁仓币数（lock_slp_member_summary 会员id+币种）
        LockSlpMemberSummary summary = slpMemberSummaryService.findByMemberIdAndUnit(prev.getMemberId(), prev.getCoinUnit());

        // 1.烧伤(小单分享大单)：若prev.getLockAmount（下级锁仓币数）> 我的锁仓币数：
        //      我的释放 = 下级锁仓数 * 我的每日释放比例 * 10% = y
        // 反之：我的释放 = 下级收益的 10% = y

        // 我的加速释放数量
        BigDecimal ratio = slpReleaseOperationService.getInviteRatio();

        LockSlpReleaseTaskRecord record = buildLockSlpReleaseTaskRecord(prev, summary, ratio, SlpReleaseType.RELEASE_INVITE);
        if (record != null) {
            log.info("生成直推加速释放任务记录: [ ref_task_id = {}, record_id = {} ]", prev.getId(), record.getId());
            slpReleaseTaskRecordService.save(record);
            // 交给异步任务处理
            addReleaseRecordMessage(record);
        } else {
            log.info("未生成直推加速释放任务记录: [ ref_task_id = {}, lock_rate = {}, my_lock_rate = {}, inviter_rate = {}, ",
                    prev.getId(), prev.getLockRate(), prev.getMyLockRate(), ratio);
        }


        // 太阳奖处理
        if (FuncWrapUtil.isNone(summary.getInviterId())) {
            // 不处理
            return Collections.singletonList(prev);
        }

        // 推荐人节点
        LockSlpMemberSummary inviteSummary = getInviterSummary(prev.getInviterId(), prev.getCoinUnit());
        LockSlpReleaseLevelConfig inviteLevelConfig =
                slpReleaseLevelConfigService.findByUnitAndLevelId(prev.getCoinUnit(), inviteSummary.getCurrentLevelId().intValue());

        // 判断下一个任务是否为太阳等级
        if (slpReleaseOperationService.isSunLevel(inviteSummary.getCurrentLevelId(), inviteSummary.getCoinUnit())) {
            // 下级直接收益的 10% ... 是否包含奖池部分(releaseAmount)

            LockSlpReleaseTask next = createNextTask(prev, true, inviteSummary, inviteLevelConfig, record);
            next.setComment("直推创建： 类型 -太阳奖");
            if (!slpReleaseTaskService.save(next)) {
                log.error("直推创建下一个太阳加速任务失败 prev_id = {}, temp_id = {}", next.getRefLastTaskId(), next.getId());
                throw new MessageCodeException(LSMsgCode.RECORD_TO_SAVE);
            }
            log.info("直推创建下一个太阳加速任务: id - {}, type - {}", next.getId(), next.getType().getCnName());

            return Arrays.asList(prev, next);
        }

        // 无后续处理
        return Collections.singletonList(prev);
    }

    @Override
    public List<LockSlpReleaseTask> processed(LockSlpReleaseTask prev, ReleaseTaskMessage msg) {
        // 不做处理
        log.info("处理直推加速释放任务已处理：ref_task_id - {}", prev.getId());
        return null;
    }

    @Override
    public boolean update(LockSlpReleaseTask prev) {
        log.info("更新当前直推释放任务状态：task_id - {}", prev.getId());
        // 更新当前任务的“记录状态”为“1-已处理”
        return slpReleaseTaskService.processed(prev.getId(), "");
    }

    @Override
    public List<TaskMessage> next(List<LockSlpReleaseTask> next, ReleaseTaskMessage msg) {
        return getNextSunTaskMessages(next, msg.getAcyclicRecommendChain());
    }


    // -----------------------------------------
    // SETTERS
    // -----------------------------------------


    @Autowired
    public void setSlpReleaseTaskService(LockSlpReleaseTaskService slpReleaseTaskService) {
        this.slpReleaseTaskService = slpReleaseTaskService;
    }

    @Autowired
    public void setSlpMemberSummaryService(LockSlpMemberSummaryService slpMemberSummaryService) {
        this.slpMemberSummaryService = slpMemberSummaryService;
    }

    @Autowired
    public void setSlpReleaseTaskRecordService(LockSlpReleaseTaskRecordService slpReleaseTaskRecordService) {
        this.slpReleaseTaskRecordService = slpReleaseTaskRecordService;
    }

    @Autowired
    public void setSlpReleaseOperationService(SlpReleaseOperationService slpReleaseOperationService) {
        this.slpReleaseOperationService = slpReleaseOperationService;
    }

    @Autowired
    public void setSlpReleaseLevelConfigService(LockSlpReleaseLevelConfigService slpReleaseLevelConfigService) {
        this.slpReleaseLevelConfigService = slpReleaseLevelConfigService;
    }

    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
