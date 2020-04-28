package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.dsc.DscContext;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.entity.constants.ExchangeProcessStatus;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.job.DelayWalSyncJob;
import com.spark.bitrade.mapper.ExchangeReleaseTaskMapper;
import com.spark.bitrade.service.*;
import com.spark.bitrade.trans.ServiceChargeEntity;
import com.spark.bitrade.trans.TradeSettleDelta;
import com.spark.bitrade.trans.TradeTccCancelEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.uitl.WalletUtils;
import com.spark.bitrade.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 币币交易释放-释放任务表(ExchangeReleaseTask)表服务实现类
 *
 * @author yangch
 * @since 2019-12-16 14:52:16
 */
@Slf4j
@Service("exchangeReleaseTaskService")
public class ExchangeReleaseTaskServiceImpl
        extends ServiceImpl<ExchangeReleaseTaskMapper, ExchangeReleaseTask> implements ExchangeReleaseTaskService {
    @Autowired
    private IMemberWalletApiService memberWalletApiService;
    @Autowired
    private ExchangeReleaseFreezeRuleService releaseFreezeRuleService;

    @Autowired
    private ExchangeReleaseWalletService releaseWallteService;

    @Autowired
    private ExchangeRateService rateService;

    @Autowired
    private DscContext dscContext;
    @Autowired
    private DelayWalSyncJob delayWalSyncJob;
    @Autowired
    private ExchangeWalletWalRecordService recordService;
    @Autowired
    private PushMessage pushMessage;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveReleaseTask(TradeSettleDelta delta, ExchangeWalletWalRecord freezeRecord) {
        // 创建买币冻结24小时后后到账的任务
        Optional<ExchangeReleaseFreezeRule> ruleOptional = releaseFreezeRuleService.findBySymbol4LocalCache(delta.getSymbol());

        // 添加 冻结释放任务
        this.baseMapper.insert(this.builderFreezeReleaseTask(freezeRecord, ruleOptional.get().getFreezeDuration()));


        //释放锁仓的币
        Optional<ExchangeReleaseWallet> walletOptional = releaseWallteService.find(freezeRecord.getMemberId(), freezeRecord.getCoinUnit());
        if (walletOptional.isPresent() && BigDecimalUtil.gte0(walletOptional.get().getLockAmount())) {
            // 创建 释放 锁仓的任务(数量按交易前的数量算)
            BigDecimal releasAmount = this.calculateReleasLockAmount(ruleOptional.get(),
                    WalletUtils.positiveOf(freezeRecord.getTradeBalance()).add(delta.getRealFee()));
            if (walletOptional.get().getLockAmount().compareTo(releasAmount) < 0) {
                // 数量 不够，释放剩下的所有
                log.info("可释放的锁仓币数不够，释放剩下是锁仓数量。freezeId={}, memberId={}, releasAmount={}, remainAmount={}",
                        freezeRecord.getId(), freezeRecord.getMemberId(), releasAmount, walletOptional.get().getLockAmount());

                releasAmount = walletOptional.get().getLockAmount();
            }

            if (BigDecimalUtil.gte0(releasAmount)) {
                this.getService().freezeFromLock(ruleOptional.get(), walletOptional.get(), freezeRecord, releasAmount);

//                // 扣除钱包待释放的币
//                if (releaseWallteService.decreaseLockAmount(freezeRecord.getMemberId(), freezeRecord.getCoinUnit(), releasAmount)) {
//                    // 添加释放 解锁任务
//                    this.baseMapper.insert(this.builderLockReleaseTask(freezeRecord, releasAmount, ruleOptional.get().getFreezeDuration()));
//                } else {
//                    // to do 释放失败，需要重试
//                    log.warn("扣除钱包待释放的币失败。freezeId={}, memberId={}, releasAmount={}, remainAmount={}",
//                            freezeRecord.getId(), freezeRecord.getMemberId(), releasAmount, walletOptional.get().getLockAmount());
//                }
            } else {
                log.warn("释放数量为0，不生成锁仓释放任务。freezeId={}, memberId={}, releasAmount={}, remainAmount={}",
                        freezeRecord.getId(), freezeRecord.getMemberId(), releasAmount, walletOptional.get().getLockAmount());
            }
        } else {
            log.info("没有可解锁的币。freezeId={}, memberId={}", freezeRecord.getId(), freezeRecord.getMemberId());
        }
    }

    ExchangeReleaseTask builderFreezeReleaseTask(ExchangeWalletWalRecord freezeRecord, int freezeHourDuration) {
        // 构建冻结释放任务
        ExchangeReleaseTask task = new ExchangeReleaseTask();
        task.setId(IdWorker.getId());
        task.setMemberId(freezeRecord.getMemberId());
        task.setCoinSymbol(freezeRecord.getCoinUnit());
        task.setType(ReleaseTaskType.RELEASE_4_FREEZE);
        task.setAmount(WalletUtils.positiveOf(freezeRecord.getTradeBalance()));
        task.setRefId(String.valueOf(freezeRecord.getId()));
        task.setReleaseStatus(ReleaseTaskStatus.UNRELEASE);
        //
        task.setReleaseTime(DateUtil.dateAddMinute(new Date(), +freezeHourDuration * 60));

        return task;
    }

    ExchangeReleaseTask builderLockReleaseTask(ExchangeWalletWalRecord freezeRecord, BigDecimal releasAmount, int freezeHourDuration) {
        // 构建锁仓释放任务
        ExchangeReleaseTask task = new ExchangeReleaseTask();
        task.setId(IdWorker.getId());
        task.setMemberId(freezeRecord.getMemberId());
        task.setCoinSymbol(freezeRecord.getCoinUnit());
        task.setType(ReleaseTaskType.RELEASE_4_LOCK);
        task.setAmount(WalletUtils.positiveOf(releasAmount));
        task.setRefId(String.valueOf(freezeRecord.getId()));
        task.setReleaseStatus(ReleaseTaskStatus.UNRELEASE);
        task.setReleaseTime(DateUtil.dateAddMinute(new Date(), +freezeHourDuration * 60));

        return task;
    }

    /**
     * 计算释放锁仓币数
     *
     * @param rule        规则
     * @param tradeAmount 交易数量
     * @return
     */
    BigDecimal calculateReleasLockAmount(ExchangeReleaseFreezeRule rule, BigDecimal tradeAmount) {
        // 每购买 XX 个，释放 XX 个
        return BigDecimalUtil.mul2down(BigDecimalUtil.div2down(WalletUtils.positiveOf(tradeAmount), rule.getRateBuyAmount()),
                rule.getRateReleaseAmount());
    }

    @Override
    public MessageRespResult<List<ExchangeReleaseTask>> taskReleaseRecord() {
        QueryWrapper<ExchangeReleaseTask> queryWrapper = new QueryWrapper<ExchangeReleaseTask>().eq(ExchangeReleaseTask.RELEASE_STATUS, "0")
                .lt(ExchangeReleaseTask.RELEASE_TIME, new Date());
        List<ExchangeReleaseTask> exchangeReleaseTasks = this.baseMapper.selectList(queryWrapper);
        if (exchangeReleaseTasks == null && exchangeReleaseTasks.size() < 1) {
            throw new MessageCodeException(CommonMsgCode.FAILURE);
        }
        return MessageRespResult.success4Data(exchangeReleaseTasks);
    }

    @Override
    public void releaseTasks(List<Object> exchangeReleaseRecord) {
        ExchangeReleaseTask exchangeReleaseTask = null;
        //循环释放锁仓记录,增加可用余额
        for (Object taskRecord : exchangeReleaseRecord) {
            exchangeReleaseTask = JSONObject.parseObject(JSON.toJSONString(taskRecord), ExchangeReleaseTask.class);

            ExchangeReleaseTask releaseTask = this.baseMapper.selectById(exchangeReleaseTask.getId());
            try {
                this.releaseTask(releaseTask);
            } catch (Exception e) {
                log.error("释放任务失败", e);
            }
        }
    }

    /**
     * 释放任务
     *
     * @param releaseTask
     * @return
     */
    @Override
    public boolean releaseTask(ExchangeReleaseTask releaseTask) {
//        AssertUtil.isTrue(releaseTask.getType().equals(ReleaseTaskType.RELEASE_4_FREEZE),
//                ExchangeReleaseMsgCode.ERROR_RELEASE_TASK_TYPE);

        // 1、幂等性校验，校验任务是否已经完成
        if (releaseTask.getReleaseStatus().equals(ReleaseTaskStatus.RELEASED)) {
            log.warn("任务已释放。exchangeReleaseTask={}", releaseTask);
            return true;
        }

        // 2、释放时间校验，校验是否到已到释放时间
        AssertUtil.isTrue(new Date().compareTo(releaseTask.getReleaseTime()) >= 0,
                ExchangeReleaseMsgCode.ERROR_RELEASE_TIME);

        if (releaseTask.getType().equals(ReleaseTaskType.RELEASE_4_FREEZE)) {
            // 3.1 释放冻结任务

            // 构建释放流水
            ExchangeWalletWalRecord record = this.builderFreezeReleaseExchangeWalletWalRecord(releaseTask);
            dscContext.getDscEntityResolver(record).update();

            // 事务保证
            if (this.getService().releaseFreezeTask(releaseTask, record)) {
                // 发送账户同步消息
                delayWalSyncJob.sync(record.getMemberId(), record.getCoinUnit());

                return true;
            } else {
                return false;
            }
        } else {
            // 3.2 释放锁仓任务
            return this.getService().releaseLockTask(releaseTask);
        }
    }

    /**
     * 释放锁仓任务
     *
     * @param releaseTask 待释放记录
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean releaseLockTask(ExchangeReleaseTask releaseTask) {
        Long walletChangeRecordId = null;
        boolean tccFlag = false;

        // 资金账户释放记录
        try {
            WalletTradeEntity trade = this.builderReleaseLockBalanceWalletTradeEntity(releaseTask);
            // try
            MessageRespResult<WalletChangeRecord> tradeResult = memberWalletApiService.tradeTccTry(trade);
            log.info("提交释放冻结 [ task_id = {}, member_id = {}, trade = {} ] 结果 -> {}",
                    releaseTask.getId(), releaseTask.getMemberId(), trade, tradeResult.getData());

            ExceptionUitl.throwsMessageCodeExceptionIfFailed(tradeResult);
            AssertUtil.notNull(tradeResult.getData(), CommonMsgCode.ERROR);

            // 流水记录ID
            walletChangeRecordId = tradeResult.getData().getId();


            // 更改释放记录的状态和更新时间
            int flag = this.baseMapper.updateReleaseTaskStatus(releaseTask.getId(), ReleaseTaskStatus.UNRELEASE, ReleaseTaskStatus.RELEASED);
            AssertUtil.isTrue(SqlHelper.retBool(flag), ExchangeReleaseMsgCode.ERROR_UPDATE_RELEASE_TASK_STATUS);


            // confirm
            MessageRespResult<Boolean> resultConfirm = memberWalletApiService.tradeTccConfirm(releaseTask.getMemberId(), walletChangeRecordId);
            log.info("提交释放冻结 [ task_id  = {}, member_id = {}, wallet_change_id = {} ] 结果 -> {}",
                    releaseTask.getId(), releaseTask.getMemberId(), walletChangeRecordId, resultConfirm.getData());

            ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultConfirm);
            AssertUtil.isTrue(resultConfirm.getData(), CommonMsgCode.ERROR);

            tccFlag = true;
        } catch (MessageCodeException ex) {
            log.error("处理失败 [ task_id = {}, code = {}, err = '{}' ]", releaseTask.getId(), ex.getCode(), ex.getMessage());
            throw ExceptionUitl.newMessageException(ex.getCode(), ex.getMessage());
        } catch (Exception ex) {
            log.error("处理失败 [ task_id = {},  err = '{}' ]", releaseTask.getId(), ex.getMessage());
            log.error("操作失败", ex);
            throw new MessageCodeException(CommonMsgCode.FAILURE);
        } finally {
            if (!tccFlag && walletChangeRecordId != null) {
                // cancel
                try {
                    MessageRespResult<Boolean> resultCancel = memberWalletApiService.tradeTccCancel(releaseTask.getMemberId(), walletChangeRecordId);
                    log.info("取消释放冻结 [ record_id = {}, member_id = {}, wallet_change_id = {} ] 结果 -> {}",
                            releaseTask.getId(), releaseTask.getMemberId(), walletChangeRecordId, resultCancel.getData());
                    // throw
                    ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultCancel);
                    AssertUtil.isTrue(resultCancel.getData(), CommonMsgCode.ERROR);
                } catch (Exception ex) {
                    try {
                        log.warn("账户变动业务取消失败，改为发送撤销消息。memberId={}, walletChangeRecordId={}",
                                releaseTask.getMemberId(), walletChangeRecordId);

                        pushMessage.push("acct-trade-tcc-cancel", "tcc-cancel",
                                new TradeTccCancelEntity(releaseTask.getMemberId(), walletChangeRecordId));
                    } catch (Exception e) {
                        log.error("账户变动业务取消失败", e);
                    }
                }
            }
        }

        return tccFlag;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean releaseFreezeTask(ExchangeReleaseTask releaseTask, ExchangeWalletWalRecord record) {
        // 更改释放记录的状态和更新时间
        int flag = this.baseMapper.updateReleaseTaskStatus(releaseTask.getId(), ReleaseTaskStatus.UNRELEASE, ReleaseTaskStatus.RELEASED);
        AssertUtil.isTrue(SqlHelper.retBool(flag), ExchangeReleaseMsgCode.ERROR_UPDATE_RELEASE_TASK_STATUS);

        // 生成账户的解冻流水记录（减冻结余额，加可用余额）
        return recordService.save(record);
    }

    /**
     * 资金账户 锁仓 -> 冻结
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean freezeFromLock(ExchangeReleaseFreezeRule rule, ExchangeReleaseWallet releaseWallet,
                                  ExchangeWalletWalRecord freezeRecord, BigDecimal releasAmount) {
        Long walletChangeRecordId = null;
        boolean tccFlag = false;

        // 资金 锁仓 -> 冻结 操作
        try {
            WalletTradeEntity trade = this.builderfreezeFromLockWalletTradeEntity(freezeRecord, releasAmount);
            // try
            MessageRespResult<WalletChangeRecord> tradeResult = memberWalletApiService.tradeTccTry(trade);
            log.info("提交锁定到冻结的交易 [ task_id = {}, member_id = {}, trade = {} ] 结果 -> {}",
                    freezeRecord.getId(), freezeRecord.getMemberId(), trade, tradeResult.getData());

            ExceptionUitl.throwsMessageCodeExceptionIfFailed(tradeResult);
            AssertUtil.notNull(tradeResult.getData(), CommonMsgCode.ERROR);

            // 流水记录ID
            walletChangeRecordId = tradeResult.getData().getId();


            // 扣除钱包待释放的币
            if (releaseWallteService.decreaseLockAmount(freezeRecord.getMemberId(), freezeRecord.getCoinUnit(), releasAmount)) {
                // 添加释放 解锁任务
                this.baseMapper.insert(this.builderLockReleaseTask(freezeRecord, releasAmount, rule.getFreezeDuration()));
            } else {
                // todo 释放失败，需要重试
                log.warn("扣除钱包待释放的币失败。freezeId={}, memberId={}, releasAmount={}, remainAmount={}",
                        freezeRecord.getId(), freezeRecord.getMemberId(), releasAmount, releaseWallet.getLockAmount());
            }

            // confirm
            MessageRespResult<Boolean> resultConfirm = memberWalletApiService.tradeTccConfirm(freezeRecord.getMemberId(), walletChangeRecordId);
            log.info("确认锁定到冻结的交易 [ task_id  = {}, member_id = {}, wallet_change_id = {} ] 结果 -> {}",
                    freezeRecord.getId(), freezeRecord.getMemberId(), walletChangeRecordId, resultConfirm.getData());

            ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultConfirm);
            AssertUtil.isTrue(resultConfirm.getData(), CommonMsgCode.ERROR);

            tccFlag = true;
        } catch (MessageCodeException ex) {
            log.error("处理失败 [ task_id = {}, code = {}, err = '{}' ]", freezeRecord.getId(), ex.getCode(), ex.getMessage());
            throw ExceptionUitl.newMessageException(ex.getCode(), ex.getMessage());
        } catch (Exception ex) {
            log.error("处理失败 [ task_id = {},  err = '{}' ]", freezeRecord.getId(), ex.getMessage());
            log.error("操作失败", ex);
            throw new MessageCodeException(CommonMsgCode.FAILURE);
        } finally {
            if (!tccFlag && walletChangeRecordId != null) {
                // cancel
                try {
                    MessageRespResult<Boolean> resultCancel = memberWalletApiService.tradeTccCancel(freezeRecord.getMemberId(), walletChangeRecordId);
                    log.info("取消提交锁定到冻结的交易 [ record_id = {}, member_id = {}, wallet_change_id = {} ] 结果 -> {}",
                            freezeRecord.getId(), freezeRecord.getMemberId(), walletChangeRecordId, resultCancel.getData());
                    // throw
                    ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultCancel);
                    AssertUtil.isTrue(resultCancel.getData(), CommonMsgCode.ERROR);
                } catch (Exception ex) {
                    try {
                        log.warn("账户变动业务取消失败，改为发送撤销消息。memberId={}, walletChangeRecordId={}",
                                freezeRecord.getMemberId(), walletChangeRecordId);

                        pushMessage.push("acct-trade-tcc-cancel", "tcc-cancel",
                                new TradeTccCancelEntity(freezeRecord.getMemberId(), walletChangeRecordId));
                    } catch (Exception e) {
                        log.error("账户变动业务取消失败", e);
                    }
                }
            }
        }

        return tccFlag;
    }

    /**
     * 构建资金账户锁仓->冻结的交易信息
     *
     * @return entity
     */
    private WalletTradeEntity builderfreezeFromLockWalletTradeEntity(ExchangeWalletWalRecord freezeRecord, BigDecimal releasAmount) {
        // 交易实体
        WalletTradeEntity trade = new WalletTradeEntity();

        trade.setType(TransactionType.RELEASE_ESP);
        trade.setChangeType(WalletChangeType.TRADE);
        trade.setMemberId(freezeRecord.getMemberId());
        trade.setCoinUnit(freezeRecord.getCoinUnit());
        trade.setTradeBalance(BigDecimal.ZERO);
        trade.setTradeFrozenBalance(WalletUtils.positiveOf(releasAmount));
        trade.setTradeLockBalance(WalletUtils.negativeOf(releasAmount));
        trade.setServiceCharge(new ServiceChargeEntity());
        trade.setRefId(String.valueOf(freezeRecord.getId()));
        trade.setComment(TransactionType.RELEASE_ESP.getCnName());

        return trade;
    }


    public ExchangeReleaseTaskServiceImpl getService() {
        return SpringContextUtil.getBean(ExchangeReleaseTaskServiceImpl.class);
    }

    /**
     * 构建冻结释放记录
     *
     * @param releaseTask
     * @return
     */
    protected ExchangeWalletWalRecord builderFreezeReleaseExchangeWalletWalRecord(ExchangeReleaseTask releaseTask) {
        ExchangeWalletWalRecord release = new ExchangeWalletWalRecord();
        release.setId(IdWorker.getId());
        release.setMemberId(releaseTask.getMemberId());
        release.setRefId(String.valueOf(releaseTask.getId()));
        release.setCoinUnit(releaseTask.getCoinSymbol());

        // + 余额
        release.setTradeBalance(WalletUtils.positiveOf(releaseTask.getAmount()));
        // - 冻结
        release.setTradeFrozen(WalletUtils.negativeOf(releaseTask.getAmount()));
        release.setTradeType(WalTradeType.RELEASE);

        // 手续费
        release.setFee(BigDecimal.ZERO);
        release.setFeeDiscount(BigDecimal.ZERO);
        // 此处仅记录流水
        release.setStatus(ExchangeProcessStatus.NOT_PROCESSED);
        release.setCreateTime(new Date());
        release.setSyncId(0L);

        release.setRate(rateService.gateUsdRate(releaseTask.getCoinSymbol()));
        release.setRemark("释放操作：关联的账户流水ID=" + releaseTask.getRefId() + ", 任务ID=" + releaseTask.getId());
        return release;
    }

    /**
     * 构建释放锁仓交易信息
     *
     * @return entity
     */
    private WalletTradeEntity builderReleaseLockBalanceWalletTradeEntity(ExchangeReleaseTask releaseTask) {
        // 交易实体
        WalletTradeEntity trade = new WalletTradeEntity();

        trade.setType(TransactionType.RELEASE_ESP);
        trade.setChangeType(WalletChangeType.TRADE);
        trade.setMemberId(releaseTask.getMemberId());
        trade.setCoinUnit(releaseTask.getCoinSymbol());
        trade.setTradeBalance(WalletUtils.positiveOf(releaseTask.getAmount()));
        trade.setTradeFrozenBalance(WalletUtils.negativeOf(releaseTask.getAmount()));
        trade.setTradeLockBalance(BigDecimal.ZERO);
        trade.setServiceCharge(new ServiceChargeEntity());
        trade.setRefId(String.valueOf(releaseTask.getId()));
        trade.setComment(TransactionType.RELEASE_ESP.getCnName());

        return trade;
    }
}