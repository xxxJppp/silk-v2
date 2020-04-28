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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 太阳等级加速任务
 *
 * @author Archx[archx@foxmail.com]
 * at 2019-07-10 21:18
 */
@Slf4j
@Component("handleSunReleaseTask")
public class HandleSunReleaseTask extends AbstractTask<ReleaseTaskMessage, LockSlpReleaseTask, LockSlpReleaseTask>
        implements ReleaseTaskHelper {

    private LockSlpReleaseTaskService slpReleaseTaskService;
    private LockSlpMemberSummaryService slpMemberSummaryService;
    private LockSlpReleaseTaskRecordService slpReleaseTaskRecordService;
    private SlpReleaseOperationService slpReleaseOperationService;
    private LockSlpReleaseLevelConfigService slpReleaseLevelConfigService;
    private StringRedisTemplate redisTemplate;

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
        return SpringContextUtil.getBean(getClass());
    }

    @Override
    public LockSlpReleaseTask convert(ReleaseTaskMessage message) {
        // 获取加速释放任务
        final long id = NumberUtils.toLong(message.getRefId(), 0);
        try {
            HintManager.getInstance().setMasterRouteOnly();
            LockSlpReleaseTask task = slpReleaseTaskService.getById(id); // FuncWrapUtil.retryFunc(() -> slpReleaseTaskService.getById(id), 3);

            if (task == null) {
                log.warn("未找到太阳加速释放任务 [ id = {} ] ", message.getRefId());
                return null;
            }

            log.info("开始处理太阳加速释放任务 [ id = {} ]", message.getRefId());
            return task;
        } catch (RuntimeException ex) {
            log.error("查找太阳加速释放任务 [ id = {} ] 出错, err = '{}'", message.getRefId(), ex.getMessage());
        }
        return null;
        // 获取加速释放任务
        // return slpReleaseTaskService.getById(NumberUtils.toLong(message.getRefId(), 0));
    }

    @Override
    public boolean check(LockSlpReleaseTask prev) {
        //幂等性判断，判断“记录状态”为“已处理”，则跳过以下3、4、5
        return prev.getStatus() == SlpProcessStatus.NOT_PROCESSED && prev.getType() == SlpReleaseType.RELEASE_SUN;
    }

    /**
     * 1.烧伤：
     * 若prev.getLockAmount（下级锁仓币数）> 我的锁仓币数：我的释放y =  下级锁仓币数 * 我的每日释放比例 * 直推奖励比例
     * 反之：我的释放y = 下级收益 * 直推奖励比例10%
     * 2. y * 10% 放入奖池
     * 3. 存入lock_slp_release_task_record （待返还）
     * 4. 修改账户
     * 5. 修改lock_slp_release_task_record 状态（已返还）
     */
    @Override
    public List<LockSlpReleaseTask> execute(LockSlpReleaseTask prev, ReleaseTaskMessage msg) {

        // 推荐人是自己时不处理
        if (prev.getMemberId() == prev.getInviterId().longValue()) {
            return null;
        }

        // 我的锁仓币数（lock_slp_member_summary 会员id+币种）
        LockSlpMemberSummary summary = slpMemberSummaryService.findByMemberIdAndUnit(prev.getMemberId(), prev.getCoinUnit());

        // 我的加速释放数量
        // 太阳等级配置 （找等级配置里的太阳每日收益比例）
        LockSlpReleaseLevelConfig config = slpReleaseLevelConfigService.findByUnitAndLevelId(prev.getCoinUnit(), prev.getCurrentLevelId().intValue());
        BigDecimal ratio = config.getSubLevelRate();

        // 确定太阳奖励释放收益是否烧伤(不烧伤)
        LockSlpReleaseTaskRecord record = buildLockSlpReleaseTaskRecord(prev, summary, ratio, SlpReleaseType.RELEASE_SUN);
        if (record != null) {
            log.info("生成太阳奖加速释放任务记录结束 -> [ ref_task_id = {}, record_id = {}, sub_level_rate = {} ]", prev.getId(), record.getId(), ratio);
            slpReleaseTaskRecordService.save(record);
            // 交给异步任务处理
            addReleaseRecordMessage(record);
        } else {
            log.info("未生成太阳奖加速释放记录 [ ref_task_id = {}, lock_rate = {}, my_lock_rate = {}, sub_level_rate = {} ]",
                    prev.getId(), prev.getLockRate(), prev.getMyLockRate(), ratio);
            prev.setComment(prev.getComment() + ", 收益比例设置为 " + ratio.toString());
        }

        // 太阳奖处理
        if (FuncWrapUtil.isNone(summary.getInviterId()) || msg.getAcyclicRecommendChain().contains(summary.getInviterId())) {
            // 不处理
            return Collections.singletonList(prev);
        }

        // 推荐人节点
        // 获取上级的上级
        LockSlpMemberSummary inviteSummary = getInviterSummary(prev.getInviterId(),prev.getCoinUnit());

        LockSlpReleaseLevelConfig inviteLevelConfig =
                slpReleaseLevelConfigService.findByUnitAndLevelId(prev.getCoinUnit(), inviteSummary.getCurrentLevelId().intValue());

        // 判断下一个任务是否为太阳等级
        if (slpReleaseOperationService.isSunLevel(inviteSummary.getCurrentLevelId(), inviteSummary.getCoinUnit())) {
            // 下级直接收益的 10% ... 是否包含奖池部分(releaseAmount)

            LockSlpReleaseTask next = createNextTask(prev, true, inviteSummary, inviteLevelConfig, record);
            next.setComment("太阳奖创建： 类型 -太阳奖");
            if (!slpReleaseTaskService.save(next)) {
                log.error("太阳奖创建下一个太阳加速任务失败 prev_id = {}, temp_id = {}", next.getRefLastTaskId(), next.getId());
                throw new MessageCodeException(LSMsgCode.RECORD_TO_SAVE);
            }
            log.info("太阳奖创建下一个太阳加速任务: id - {}, type - {}", next.getId(), next.getType().getCnName());

            return Arrays.asList(prev, next);
        }

        return Collections.singletonList(prev);
    }

    @Override
    public List<LockSlpReleaseTask> processed(LockSlpReleaseTask prev, ReleaseTaskMessage msg) {
        // 不做处理
        return null;
    }

    @Override
    public boolean update(LockSlpReleaseTask prev) {
        // 更新当前任务的“记录状态”为“1-已处理”
        return slpReleaseTaskService.processed(prev.getId(), prev.getComment());
    }

    @Override
    public List<TaskMessage> next(List<LockSlpReleaseTask> next, ReleaseTaskMessage msg) {
       return getNextSunTaskMessages(next, msg.getAcyclicRecommendChain());
    }

    @Override
    public SlpReleaseOperationService getSlpReleaseOperationService() {
        return slpReleaseOperationService;
    }

    @Override
    public LockSlpReleaseTaskRecordService getLockSlpReleaseTaskRecordService() {
        return slpReleaseTaskRecordService;
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
