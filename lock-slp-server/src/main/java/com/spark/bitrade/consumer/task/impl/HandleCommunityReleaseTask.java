package com.spark.bitrade.consumer.task.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.constant.SlpProcessStatus;
import com.spark.bitrade.constant.SlpReleaseType;
import com.spark.bitrade.constants.LSMsgCode;
import com.spark.bitrade.consumer.task.AbstractTask;
import com.spark.bitrade.consumer.task.ReleaseTaskHelper;
import com.spark.bitrade.consumer.task.util.TaskMessageUtils;
import com.spark.bitrade.entity.LockSlpMemberSummary;
import com.spark.bitrade.entity.LockSlpReleaseLevelConfig;
import com.spark.bitrade.entity.LockSlpReleaseTask;
import com.spark.bitrade.entity.LockSlpReleaseTaskRecord;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.job.util.FuncWrapUtil;
import com.spark.bitrade.mq.ReleaseTaskMessage;
import com.spark.bitrade.mq.ReleaseTaskMessageType;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 节点加速任务
 */
@Slf4j
@Component("handleCommunityReleaseTask")
public class HandleCommunityReleaseTask extends AbstractTask<ReleaseTaskMessage, LockSlpReleaseTask, LockSlpReleaseTask>
        implements ReleaseTaskHelper {

    /*
【概述】
根据加速释放任务的数据，处理加速释放的逻辑；其次，根据推荐关系构建推荐人的加速任务，并通过消息将更新任务广播出去。

【逻辑】
1、根据通知内容，获取加速释放任务
2、幂等性判断，判断“记录状态”为“已处理”，则跳过以下3、4、5
3、处理社区奖励加速释放的逻辑【需细化】
4、更新当前任务的“记录状态”为“1-已处理”
5、根据推荐关系构建及保存推荐人的加速释放任务
6、广播 <推荐人节点奖励加速释放任务消息>

【备注】
1、注意 幂等性处理和事务型的处理

【处理节点奖励加速释放的逻辑】
1）处理类型包含，无等级、级差、平级
     */

    /**
     * 平级或越级最大出现次数
     */
    private static final int PEER_MAX_TIMES = 1;

    private LockSlpReleaseTaskService slpReleaseTaskService;
    private LockSlpReleaseLevelConfigService lockSlpReleaseLevelConfigService;
    private LockSlpMemberSummaryService slpMemberSummaryService;
    private LockSlpReleaseTaskRecordService slpReleaseTaskRecordService;
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
            LockSlpReleaseTask task = slpReleaseTaskService.getById(id); // FuncWrapUtil.retryFunc(() -> slpReleaseTaskService.getById(id), 3);

            if (task == null) {
                log.warn("未找到加速释放任务 [ id = {} ] ", message.getRefId());
                return null;
            }

            log.info("开始处理加速释放任务 [ id = {} ]", message.getRefId());
            return task;
        } catch (RuntimeException ex) {
            log.error("查找加速释放任务 [ id = {} ] 出错, err = '{}'", message.getRefId(), ex.getMessage());
        }
        return null;
    }

    @Override
    public boolean check(LockSlpReleaseTask prev) {
        // 幂等性判断，判断“记录状态”为“已处理”，则跳过以下3、4、5
        return prev.getStatus() == SlpProcessStatus.NOT_PROCESSED;
    }

    /**
     * 【1.收益比例： 级差奖励率】
     * 【2.烧伤（类同直推加速释放任务HandleShareReleaseTask）：】
     * 若prev.getLockAmount（下级锁仓币数）> 我的锁仓币数：我的释放y =  x * 我的每日释放比例 * 收益比例
     * 反之： 我的释放y = 下级收益 * 收益比例
     * 【3. y * 10% 放入奖池】
     * 【4. 存入lock_slp_release_task_record （待返还）】
     * 【5. 修改账户】
     * 【6. 修改lock_slp_release_task_record 状态（已返还）】
     * 【7. 生成下一个加速释放任务】
     */
    @Override
    @Transactional
    public List<LockSlpReleaseTask> execute(LockSlpReleaseTask prev, ReleaseTaskMessage msg) {
        log.info("处理节点加速释放任务开始：ref_task_id - {}", prev.getId());

        // 平级奖出现次数超出
        if (prev.getPeersTimes() > PEER_MAX_TIMES) {
            log.info("平级奖次数超出，直接创建下一个释放任务 task_id = {}, peers_time = {} ", prev.getId(), prev.getPeersTimes());
            // 创建下一个任务
            return buildNextTask(prev, msg, null);
        }

        // 级差奖励率
        BigDecimal ratio = prev.getRewardRate();
        // 我的锁仓币数（lock_slp_member_summary 会员id+币种）
        LockSlpMemberSummary summary = slpMemberSummaryService.findByMemberIdAndUnit(prev.getMemberId(), prev.getCoinUnit());

        LockSlpReleaseTaskRecord record = buildLockSlpReleaseTaskRecord(prev, summary, ratio, prev.getType());
        if (record != null) {
            log.info("生成加速释放任务记录: [ ref_task_id = {}, task_record_id = {} ]", prev.getId(), record.getId());
            slpReleaseTaskRecordService.save(record);
            // 异步任务
            addReleaseRecordMessage(record);
            // push(message.getTopic(), message.stringify());
        } else {
            log.info("未生成加速释放任务记录 [ ref_task_id = {}, reward_rate = {}, lock_rate = {}, my_lock_rate = {} ]",
                    prev.getId(), ratio, prev.getLockRate(), prev.getMyLockRate());
        }


        // 放入奖池
        // slpJackpotService.add(record.getCoinUnit(), record.getJackpotInAmount());
        //【4. 修改账户】
        // 异步任务处理 MessageRespResult<Boolean> result = walletTradeClient.trade(record, WalletTradeClient.RType.NODE);

        //【5.修改lock_slp_release_task_record 状态（已返还）】

        /*
         * 【7. 生成下一个加速释放任务】
         * 加速释放奖励率 = 烧伤机制：我的每日释放比例
         * 子部门中的最大奖励率x = prev中 级差奖励率、子部门中的最大奖励率 取最大
         * 级差奖励率y： 若 next的占比 - x <=0 -》1% ，反之：级差奖励率y = next的占比 - x
         * 如果y != 1%, 平级出现次数与prev相等，反之加一
         */

        return buildNextTask(prev, msg, record);

    }


    /**
     * 生成下一个加速释放任务
     * 加速释放奖励率 = 烧伤机制：我的每日释放比例
     * 子部门中的最大奖励率x = prev中 级差奖励率、子部门中的最大奖励率 取最大
     * 级差奖励率y： 若 next的占比 - x <=0 -》1% ，反之：级差奖励率y = next的占比 - x
     * 如果y != 1%, 平级出现次数与prev相等，反之加一
     */
    private List<LockSlpReleaseTask> buildNextTask(LockSlpReleaseTask prev, ReleaseTaskMessage msg,
                                                   LockSlpReleaseTaskRecord record) {
        log.info("创建下一个节点加速任务");

        // 递归中断条件处理
        // 中断递归条件
        // 推荐人ID为NULL 或 循环关系链中存在推荐人
        if (FuncWrapUtil.isNone(prev.getInviterId()) || msg.getAcyclicRecommendChain().contains(prev.getInviterId())) {
            log.info("生成下一个节点加速任务，递归中断，ref_task_id - {}, inviter_id = {}", prev.getId(), prev.getInviterId());
            return null;
        }

        // 推荐人节点
        // 获取上级的上级
        LockSlpMemberSummary inviteSummary = getInviterSummary(prev.getInviterId(), prev.getCoinUnit());
        LockSlpReleaseLevelConfig inviteLevelConfig =
                lockSlpReleaseLevelConfigService.findByUnitAndLevelId(prev.getCoinUnit(), inviteSummary.getCurrentLevelId().intValue());


        List<LockSlpReleaseTask> list = new ArrayList<>();

        LockSlpReleaseTask next = createNextTask(prev, false, inviteSummary, inviteLevelConfig, record);
        doSaveTask(next);
        list.add(next);

        log.info("创建下一个节点加速任务: id - {}, type - {}", next.getId(), next.getType().getCnName());
        // 判断下一个任务是否为太阳等级
        if (slpReleaseOperationService.isSunLevel(inviteSummary.getCurrentLevelId(), inviteSummary.getCoinUnit())) {
            // 下级直接收益的 10% ... 是否包含奖池部分(releaseAmount)
            LockSlpReleaseTask sunNext = createNextTask(prev, true, inviteSummary, inviteLevelConfig, record);
            doSaveTask(sunNext);
            list.add(sunNext);
            log.info("创建下一个太阳加速任务: id - {}, type - {}", sunNext.getId(), sunNext.getType().getCnName());
        }
        return list;
    }

    private void doSaveTask(LockSlpReleaseTask next) {
        next.setComment("社区节点加速释放,释放类型-" + next.getType().getCnName());
        if (!slpReleaseTaskService.save(next)) {
            log.error("处理节点加速任务保存失败 prev_id = {}, temp_id = {}", next.getRefLastTaskId(), next.getId());
            throw new MessageCodeException(LSMsgCode.RECORD_TO_SAVE);
        }
    }

    @Override
    public List<LockSlpReleaseTask> processed(LockSlpReleaseTask prev, ReleaseTaskMessage msg) {
        log.info("查询已处理过的节点加速释放任务：ref_plan_id - {}, member_id - {}, deep - {}",
                prev.getRefPlanId(), prev.getInviterId(), prev.getDeep() + 1);
        // 查询 next 任务
        //只有一条
        QueryWrapper<LockSlpReleaseTask> query = new QueryWrapper<>();
        query.eq("deep", prev.getDeep() + 1)
                .eq("coin_unit", prev.getCoinUnit())
                .eq("member_id", prev.getInviterId())
                .eq("ref_plan_id", prev.getRefPlanId())
                .ne("type", SlpReleaseType.RELEASE_INVITE);
        return slpReleaseTaskService.list(query);
    }

    @Override
    public boolean update(LockSlpReleaseTask prev) {
        // 更新当前任务的“记录状态”为“1-已处理”
        log.info("更新当前节点释放任务状态：task_id - {}", prev.getId());
        return slpReleaseTaskService.processed(prev.getId(), "");
    }

    @Override
    public List<TaskMessage> next(List<LockSlpReleaseTask> next, ReleaseTaskMessage msg) {
        log.info("构建并保存推荐人的加速释放任务======");
        List<TaskMessage> messages = new ArrayList<>();
        if (next != null) {
            messages = getReleaseRecordMessage(next.get(0).getRefLastTaskId());

            for (LockSlpReleaseTask task : next) {
                ArrayList<Long> chainIds = msg.getAcyclicRecommendChain();
                chainIds.add(task.getMemberId());

                if (task.getType() == SlpReleaseType.RELEASE_CROSS || task.getType() == SlpReleaseType.RELEASE_PEERS) {
                    messages.add(TaskMessageUtils.buildReleaseTask(task, ReleaseTaskMessageType.HANDLE_COMMUNITY_RELEASE_TASK, chainIds));
                } else {
                    messages.add(TaskMessageUtils.buildReleaseTask(task, ReleaseTaskMessageType.HANDLE_SUN_RELEASE_TASK, chainIds));
                }
            }
        }
        return messages;
    }


    // -----------------------------------------
    // SETTERS
    // -----------------------------------------

    @Autowired
    public void setSlpReleaseTaskService(LockSlpReleaseTaskService slpReleaseTaskService) {
        this.slpReleaseTaskService = slpReleaseTaskService;
    }

    @Autowired
    public void setLockSlpReleaseLevelConfigService(LockSlpReleaseLevelConfigService lockSlpReleaseLevelConfigService) {
        this.lockSlpReleaseLevelConfigService = lockSlpReleaseLevelConfigService;
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
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
