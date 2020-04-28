package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.AwardTaskType;
import com.spark.bitrade.constant.ProcessStatus;
import com.spark.bitrade.entity.ExchangeReleaseAwardTask;
import com.spark.bitrade.entity.ExchangeWalletWalRecord;
import com.spark.bitrade.entity.constants.ExchangeProcessStatus;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.mapper.ExchangeReleaseAwardTaskMapper;
import com.spark.bitrade.service.ExchangeReleaseAwardTaskService;
import com.spark.bitrade.service.ExchangeWalletOperations;
import com.spark.bitrade.service.GlobalParamService;
import com.spark.bitrade.uitl.WalletUtils;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 币币交易-推荐人奖励任务表(ExchangeReleaseAwardTask)表服务实现类
 *
 * @author yangch
 * @since 2020-01-17 17:18:53
 */
@Slf4j
@Service("exchangeReleaseAwardTaskService")
public class ExchangeReleaseAwardTaskServiceImpl extends ServiceImpl<ExchangeReleaseAwardTaskMapper, ExchangeReleaseAwardTask> implements ExchangeReleaseAwardTaskService {

    private ExchangeWalletOperations walletOperations;
    private GlobalParamService globalParamService;

    @Override
    public List<ExchangeReleaseAwardTask> findAwaitReleaseTask() {
        QueryWrapper<ExchangeReleaseAwardTask> wrapper = new QueryWrapper<>();
        /// wrapper.eq("status", ProcessStatus.NOT_PROCESSED).le("release_time", new Date());
        wrapper.lambda().eq(ExchangeReleaseAwardTask::getStatus, ProcessStatus.NOT_PROCESSED)
                .le(ExchangeReleaseAwardTask::getReleaseTime, new Date());
        return list(wrapper);
    }

    @Override
    public void releaseTask(Long taskId) {
        // 更新状态
//        if (!updateTaskStatus(taskId, ProcessStatus.NOT_PROCESSED, ProcessStatus.PROCESSING)) {
//            log.error("推荐人奖励任务 [ taskId = {} ] 处理中断，状态不匹配", taskId);
//            return;
//        }

        ExchangeReleaseAwardTask task = getById(taskId);
        if (Objects.nonNull(task)) {
            if (task.getStatus().equals(ProcessStatus.NOT_PROCESSED)) {
                log.info("开始执行奖励任务，taskId={}", taskId);
                getService().releaseTask(task);
            }
        } else {
            log.warn("任务不存在，taskId={}", taskId);
        }
    }

    @Transactional(rollbackFor = Exception.class)

    public void releaseTask(ExchangeReleaseAwardTask task) {
        Long taskId = task.getId();
        // 先从归集账户扣除
        Long awardAccount = globalParamService.getAwardAccount();
        ExchangeWalletWalRecord award = buildAwardAccountDecrementWal(task, awardAccount);

        try {
            walletOperations.booking(award);
        } catch (MessageCodeException ex) {
            log.error("推荐人奖励任务 [ taskId = {} ] 处理失败 -> code = {}, err = '{}'", taskId, ex.getCode(), ex.getMessage());
            throw ex;
        }

        // 增加到推荐人账户
        ExchangeWalletWalRecord incrementWal = buildAwardIncrementWal(task);
        try {
            walletOperations.booking(incrementWal);
        } catch (MessageCodeException ex) {
            log.error("推荐人奖励任务 [ taskId = {} ] 处理失败，归集账户扣款无法回退 -> code = {}, err = '{}'", taskId, ex.getCode(), ex.getMessage());
            throw ex;
        }

        // 更新状态
        if (updateTaskStatus(taskId, ProcessStatus.NOT_PROCESSED, ProcessStatus.PROCESSED)) {
            log.info("推荐人奖励任务 [ taskId = {} ] 处理完成", taskId);
        } else {
            // 不应该到这一步!!!!
            log.error("推荐人奖励任务 [ taskId = {} ] 处理失败", taskId);
        }
    }

    private ExchangeWalletWalRecord buildAwardAccountDecrementWal(ExchangeReleaseAwardTask task, Long account) {
        // 构建wal
        ExchangeWalletWalRecord record = new ExchangeWalletWalRecord();

        // 关联会员和订单
        record.setMemberId(account);
        record.setRefId(task.getId() + "");
        record.setCoinUnit(task.getAwardSymbol());

        // - 余额
        record.setTradeBalance(WalletUtils.negativeOf(task.getAmount()));
        record.setTradeFrozen(BigDecimal.ZERO);
        record.setFee(BigDecimal.ZERO);

        // 状态
        record.setTradeType(WalTradeType.EXCHANGE_REWARD);
        record.setStatus(ExchangeProcessStatus.NOT_PROCESSED);
        // record.setTccStatus(); default 0 暂未使用到

        if (task.getType().equals(AwardTaskType.AWARD_FOR_ACCUMULATION)) {
            // 若发ESP，备注需要显示：直推会员累计买入奖励返佣给#会员ID#
            record.setRemark(String.format("直推会员累计买入奖励返佣给#%s#", task.getMemberId()));
        } else {
            // 若发USDT，备注需要显示：#会员ID#买入ESP返佣给#会员ID#
            record.setRemark(String.format("#%s#买入ESP返佣给#%s#", task.getInviteeId(), task.getMemberId()));
        }

        record.setCreateTime(Calendar.getInstance().getTime());

        return record;
    }

    private ExchangeWalletWalRecord buildAwardIncrementWal(ExchangeReleaseAwardTask task) {
        // 构建wal
        ExchangeWalletWalRecord record = new ExchangeWalletWalRecord();

        // 关联会员和订单
        record.setMemberId(task.getMemberId());
        record.setRefId(task.getId() + "");
        record.setCoinUnit(task.getAwardSymbol());

        // + 余额
        record.setTradeBalance(task.getAmount().abs());
        record.setTradeFrozen(BigDecimal.ZERO);
        record.setFee(BigDecimal.ZERO);

        // 状态
        record.setTradeType(WalTradeType.EXCHANGE_REWARD);
        record.setStatus(ExchangeProcessStatus.NOT_PROCESSED);
        if (task.getType().equals(AwardTaskType.AWARD_FOR_FEE)) {
            record.setRemark("直推用户手续费奖励");
        } else {
            record.setRemark("直推用户累计购买奖励");
        }
        // record.setTccStatus(); default 0 暂未使用到

        record.setCreateTime(Calendar.getInstance().getTime());

        return record;
    }

    private boolean updateTaskStatus(Long taskId, ProcessStatus source, ProcessStatus target) {
        UpdateWrapper<ExchangeReleaseAwardTask> wrapper = new UpdateWrapper<>();
        wrapper.lambda().eq(ExchangeReleaseAwardTask::getId, taskId)
                .eq(ExchangeReleaseAwardTask::getStatus, source)
                .set(ExchangeReleaseAwardTask::getStatus, target)
                .set(ExchangeReleaseAwardTask::getUpdateTime, new Date());
        /*wrapper.eq("id", taskId).eq("status", source)
                .set("status", target).set("update_time", new Date());*/
        return update(wrapper);
    }

    @Autowired
    public void setWalletOperations(ExchangeWalletOperations walletOperations) {
        this.walletOperations = walletOperations;
    }

    @Autowired
    public void setGlobalParamService(GlobalParamService globalParamService) {
        this.globalParamService = globalParamService;
    }

    public ExchangeReleaseAwardTaskServiceImpl getService() {
        return SpringContextUtil.getBean(ExchangeReleaseAwardTaskServiceImpl.class);
    }
}