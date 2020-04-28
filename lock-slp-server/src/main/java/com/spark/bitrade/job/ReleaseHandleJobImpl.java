package com.spark.bitrade.job;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.spark.bitrade.constant.SlpReleaseType;
import com.spark.bitrade.constant.SlpStatus;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constant.WalletChangeType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.dto.SlpReleaseRecordDto;
import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.entity.LockSlpReleasePlanRecord;
import com.spark.bitrade.entity.LockSlpReleaseTaskRecord;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.job.util.FuncWrapUtil;
import com.spark.bitrade.mapper.SlpReleaseOperationMapper;
import com.spark.bitrade.service.*;
import com.spark.bitrade.trans.ServiceChargeEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * ReleaseHandleJobImpl
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/15 14:10
 */
@Slf4j
@Service
public class ReleaseHandleJobImpl implements ReleaseHandleJob {

    private LockSlpReleasePlanRecordService slpReleasePlanRecordService;
    private LockSlpReleaseTaskRecordService slpReleaseTaskRecordService;
    private LockSlpJackpotService slpJackpotService;

    private SlpReleaseOperationService slpReleaseOperationService;

    private SlpReleaseOperationMapper releaseOperationMapper;

    private IMemberWalletApiService memberWalletApiService;

    @Override
    @Transactional
    public void execute(LockSlpReleasePlanRecord record) {

        if (record.getStatus() != SlpStatus.NOT_PROCESSED) {
            log.info("每日释放记录 [ record_id = {} ] 已处理", record.getId());
            return;
        }

        log.info("开始处理每日释放记录 [ record_id = {} ] ", record.getId());
        handle(new SlpReleaseRecordDto(
                record.getId(),
                record.getRefPlanId(),
                record.getMemberId(),
                SlpReleaseType.RELEASE_DAILY,
                record.getReleaseAmount(),
                record.getJackpotAmount(),
                record.getAllocationProportion(),
                record.getComment()));
    }

    @Override
    @Transactional
    public void execute(LockSlpReleaseTaskRecord record) {

        if (record.getStatus() != SlpStatus.NOT_PROCESSED) {
            log.info("加速释放记录 [ record_id = {} ] 已处理", record.getId());
            return;
        }

        log.info("开始处理加速释放记录 [ record_id = {}, type = {} ] ", record.getId(), record.getType());
        // 返回记录表Id
        Long planRecordId = record.getRefPlanId();
        Long memberId = record.getMemberId();
        String coinUnit = record.getCoinUnit();

        Long planId = releaseOperationMapper.findPlanId(memberId, coinUnit);

        if (planId == null) {
            log.error("未找到加速释放记录 [ task_record_id = {}] 对应的 本金返回记录 [ plan_record_id = {} ]", record.getId(), planRecordId);
            return;
        }

        handle(new SlpReleaseRecordDto(
                record.getId(),
                planId,
                record.getMemberId(),
                record.getType(),
                record.getReleaseAmount(),
                record.getJackpotAmount(),
                record.getAllocationProportion(),
                record.getComment()));
    }

    /**
     * 处理释放记录
     *
     * @param record 记录包装
     */
    private void handle(SlpReleaseRecordDto record) {

        // 悲观锁
        LockSlpReleasePlan plan = releaseOperationMapper.findByIdWithLock(record.getPlanId());


        // 计划已完成
        if (plan.getStatus() != SlpStatus.NOT_PROCESSED) { // plan.getStatus() == SlpStatus.PROCESSED || plan.getStatus() == SlpStatus.APPEND_PROCESSED) {
            record.setReleaseAmount(BigDecimal.ZERO);
            record.setJackpotAmount(BigDecimal.ZERO);
            invoke(record, "可用余额已释放完成");
            return;
        }

        // 审查余额是否足够
        BigDecimal remainAmount = plan.getRemainAmount();

        // 已经释放完毕
        if (FuncWrapUtil.LessThanOrEqual(remainAmount, BigDecimal.ZERO)) {
            record.setReleaseAmount(BigDecimal.ZERO);
            record.setJackpotAmount(BigDecimal.ZERO);
            invoke(record, "可用余额已释放完成");
            return;
        }


        // 活动余额小于理论释放余额
        if (FuncWrapUtil.LessThan(remainAmount, record.getTotalAmount())) {
            // 重新计算释放比例
            record.redistribution(remainAmount);

            String comment = String.format("余额不足理论释放，重新调整 [ releaseAmount = '%s', jackpotAmount = '%s' ]",
                    record.getReleaseAmount().toString(), record.getJackpotAmount().toString());
            invoke(record, comment);
            return;
        }

        // 正常释放
        invoke(record, "正常释放");
    }

    /**
     * 调用其他接口
     *
     * @param record  记录
     * @param comment 备注
     */
    private void invoke(SlpReleaseRecordDto record, String comment) {

        // 总额
        BigDecimal total = record.getTotalAmount();

        // SLP
        String coinInUnit = slpReleaseOperationService.getCoinInUnit();
        BigDecimal rate = slpReleaseOperationService.getYesterdayExchangeRate2Usdt(coinInUnit);
        // record.setCoinInUnit(coinInUnit);
        // record.setReleaseInRate(rate);
        // record.setReleaseInAmount(releaseAmount.multiply(rate));
        // record.setJackpotInAmount(jackpotAmount.multiply(rate));
        // record.setComment(comment);


        // 标记
        boolean tccFlag = false;
        Long walletChangeRecordId = null;
        // 需要处理账务接口
        if (FuncWrapUtil.GreaterThan(total, BigDecimal.ZERO)) {

            // 实体
            Long memberId = record.getMemberId();
            WalletTradeEntity entity = newTradeEntity(record.getId().toString(), memberId, coinInUnit,
                    BigDecimalUtil.div2down(record.getReleaseAmount(), rate), record.getType().getCnName() + "，释放奖励");

            try {

                // try
                MessageRespResult<WalletChangeRecord> tradeResult = memberWalletApiService.tradeTccTry(entity);
                log.info("提交账户变动 [ record_id = {}, member_id = {}, trade = {} ] 结果 -> {}",
                        record.getId(), memberId, entity, tradeResult.getData());

                ExceptionUitl.throwsMessageCodeExceptionIfFailed(tradeResult);
                AssertUtil.notNull(tradeResult.getData(), CommonMsgCode.ERROR);

                // 流水记录ID
                walletChangeRecordId = tradeResult.getData().getId();

                // 更新数据
                if (!doUpdate(record, coinInUnit, rate, comment)) {
                    throw new RuntimeException("数据更新失败");
                }

                // confirm
                MessageRespResult<Boolean> resultConfirm = memberWalletApiService.tradeTccConfirm(memberId, walletChangeRecordId);
                log.info("确认账户变动 [ record_id = {}, member_id = {}, wallet_change_id = {} ] 结果 -> {}",
                        record.getId(), memberId, walletChangeRecordId, resultConfirm.getData());

                ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultConfirm);
                AssertUtil.isTrue(resultConfirm.getData(), CommonMsgCode.ERROR);

                tccFlag = true;
            } catch (MessageCodeException ex) {
                log.error("处理失败 [ record_id = {}, code = {}, err = '{}' ]", record.getId(), ex.getCode(), ex.getMessage());
                throw ExceptionUitl.newMessageException(ex.getCode(), ex.getMessage());
            } catch (Exception ex) {
                log.error("处理失败 [ record_id = {},  err = '{}' ]", record.getId(), ex.getMessage());
                log.error("操作失败", ex);
                throw new MessageCodeException(CommonMsgCode.FAILURE);
            } finally {
                if (!tccFlag && walletChangeRecordId != null) {
                    // cancel
                    Long id = record.getId();
                    try {
                        MessageRespResult<Boolean> resultCancel = memberWalletApiService.tradeTccCancel(memberId, walletChangeRecordId);
                        log.info("取消账户变动 [ record_id = {}, member_id = {}, wallet_change_id = {} ] 结果 -> {}",
                                id, memberId, walletChangeRecordId, resultCancel.getData());
                        // throw
                        ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultCancel);
                        AssertUtil.isTrue(resultCancel.getData(), CommonMsgCode.ERROR);
                    } catch (Exception ex) {
                        log.error("账户变动业务取消失败", ex);
                    }
                }
            }
            return;
        }

        // 无账变动处理
        if (!doUpdate(record, coinInUnit, rate, comment)) {
            log.error("执行数据更新失败 [ record_id = {}]", record.getId());
            throw new MessageCodeException(CommonMsgCode.FAILURE);
        }
    }

    /**
     * 执行数据更新
     *
     * @param record     记录
     * @param coinInUnit 币种
     * @param rate       汇率
     * @param comment    备注
     * @return bool
     */
    private boolean doUpdate(SlpReleaseRecordDto record, String coinInUnit, BigDecimal rate, String comment) {

        SlpReleaseType type = record.getType();

        boolean step1 = false;

        // 更新记录
        if (type == SlpReleaseType.RELEASE_DAILY) {
            UpdateWrapper<LockSlpReleasePlanRecord> update = record.newUpdateWrapper(coinInUnit, rate, comment);
            step1 = slpReleasePlanRecordService.update(update);
        } else {
            UpdateWrapper<LockSlpReleaseTaskRecord> update = record.newUpdateWrapper(coinInUnit, rate, comment);
            step1 = slpReleaseTaskRecordService.update(update);
        }
        log.info("更新释放记录 [ record_id = {}, type = {} ] 结果 -> {}", record.getId(), type, step1);

        // 更新待释放余额
        BigDecimal totalAmount = record.getTotalAmount();
        if (step1 && FuncWrapUtil.GreaterThan(totalAmount, BigDecimal.ZERO)) {

            // 更新待释放余额
            boolean update = releaseOperationMapper.updateRemainAmount(record.getPlanId(), totalAmount) > 0;
            log.info("更新待释放余额 [ plan_id = {}, subtract_amount = {} ] 结果 -> {}", record.getPlanId(), totalAmount, update);

            // 加入奖池
            BigDecimal jackpotInAmount = BigDecimalUtil.div2down(record.getJackpotAmount(), rate);
            boolean add = slpJackpotService.add(coinInUnit, jackpotInAmount);
            log.info("加入奖池 [ record_id = {}, type = {}, jackpot_in_amount = {} ] 结果 -> {}", record.getId(), type, jackpotInAmount, add);

            return update && add;
        }


        return step1;
    }

    /**
     * 构建账实体
     *
     * @param refId      记录ID
     * @param memberId   会员id
     * @param coinInUnit 币种
     * @param amount     数量
     * @param comment    备注
     * @return trade
     */
    private WalletTradeEntity newTradeEntity(String refId, Long memberId,
                                             String coinInUnit, BigDecimal amount,
                                             String comment) {
        WalletTradeEntity trade = new WalletTradeEntity();

        trade.setType(TransactionType.SLP_LOCK_RELEASE);
        trade.setRefId(refId);
        trade.setChangeType(WalletChangeType.TRADE);
        trade.setMemberId(memberId);
        trade.setCoinUnit(coinInUnit);
        trade.setTradeBalance(amount);
        trade.setTradeFrozenBalance(BigDecimal.ZERO);
        trade.setTradeLockBalance(BigDecimal.ZERO);
        trade.setComment(comment);
        trade.setServiceCharge(new ServiceChargeEntity());

        return trade;
    }

    // -----------------------------------------
    // SETTERS ...
    // -----------------------------------------

    @Autowired
    public void setSlpReleasePlanRecordService(LockSlpReleasePlanRecordService slpReleasePlanRecordService) {
        this.slpReleasePlanRecordService = slpReleasePlanRecordService;
    }

    @Autowired
    public void setSlpReleaseTaskRecordService(LockSlpReleaseTaskRecordService slpReleaseTaskRecordService) {
        this.slpReleaseTaskRecordService = slpReleaseTaskRecordService;
    }

    @Autowired
    public void setSlpJackpotService(LockSlpJackpotService slpJackpotService) {
        this.slpJackpotService = slpJackpotService;
    }

    @Autowired
    public void setSlpReleaseOperationService(SlpReleaseOperationService slpReleaseOperationService) {
        this.slpReleaseOperationService = slpReleaseOperationService;
    }


    @Autowired
    public void setReleaseOperationMapper(SlpReleaseOperationMapper releaseOperationMapper) {
        this.releaseOperationMapper = releaseOperationMapper;
    }

    @Autowired
    public void setMemberWalletApiService(IMemberWalletApiService memberWalletApiService) {
        this.memberWalletApiService = memberWalletApiService;
    }
}
