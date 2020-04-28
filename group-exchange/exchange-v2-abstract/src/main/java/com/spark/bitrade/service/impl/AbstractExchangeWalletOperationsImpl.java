package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constants.ExchangeOrderMsgCode;
import com.spark.bitrade.entity.ExchangeWallet;
import com.spark.bitrade.entity.ExchangeWalletSyncRecord;
import com.spark.bitrade.entity.ExchangeWalletWalRecord;
import com.spark.bitrade.entity.constants.ExchangeProcessStatus;
import com.spark.bitrade.entity.dto.WalletSyncCountDto;
import com.spark.bitrade.job.DelayWalSyncJob;
import com.spark.bitrade.service.*;
import com.spark.bitrade.uitl.WalletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * ExchangeWalletOperationsImpl
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/2 16:06
 */
@Slf4j
public abstract class AbstractExchangeWalletOperationsImpl implements ExchangeWalletOperations {

    protected ExchangeWalletService walletService;
    protected ExchangeWalletWalRecordService walRecordService;
    protected ExchangeWalletSyncRecordService syncRecordService;

    protected DelayWalSyncJob delayWalSyncJob;
    protected ExchangeRateService rateService;

    @Override
    public Optional<ExchangeWallet> balance(Long memberId, String coinUnit) {
        // 从db获取
        return walletService.findOne(memberId, coinUnit); //value.map(ExchangeWallet::getBalance);
    }

    @Transactional
    @Override
    public Optional<ExchangeWalletWalRecord> booking(ExchangeWalletWalRecord record) {

        // @see IdWorkerConfiguration
        record.setId(IdWorker.getId());

        record.setRate(rateService.gateUsdRate(record.getCoinUnit()));
        record.setSyncId(0L); // 签名需要

        // 增减判断
        boolean isDecrease = WalletUtils.isNegative(record.getTradeBalance());
        // 同步判断 省略该步骤，由前置条件 balance(Long memberId, String coinUnit) 判断
        // boolean isSynchronized = WalletUtils.isTrue(redisTemplate.hasKey(walletKey));

        // 扣除操作
        if (isDecrease) {

            // long delta = WalletUtils.toAccuracyValue(record.getTradeBalance());
            // 尝试扣除, 从db扣除 record.getTradeBalance().abs()
            // 余额不足
            BigDecimal amount = record.getTradeBalance().abs();
            BigDecimal frozen = BigDecimal.ZERO;
            if (record.getTradeFrozen() != null) {
                frozen = record.getTradeFrozen().abs(); // 1.转出扣款不加入冻结余额
            }
            if (!walletService.freeze(record.getMemberId(), record.getCoinUnit(), amount, frozen)) {
                log.error("booking >> 账户余额不足 member_id = {}, coin_unit = {}, delta = {}", record.getMemberId(),
                        record.getCoinUnit(), amount);

                throw ExchangeOrderMsgCode.ERROR_BALANCE_NOT_ENOUGH.asException();
            }

            try {
                record.setStatus(ExchangeProcessStatus.PROCESSED);
                if (!walRecordService.signAndSave(record)) {
                    // 未成功
                    throw ExchangeOrderMsgCode.ERROR_WRITE_TO_DB.asException();
                }
            } catch (Exception ex) {
                log.error("booking >> 写入数据库失败 record = {}", record);
                log.error("booking >> 写入数据库失败", ex);
                throw ExchangeOrderMsgCode.ERROR_WRITE_TO_DB.asException();
            }

            // 延时同步
            delayWalSyncJob.sync(record.getMemberId(), record.getCoinUnit());
            return Optional.of(record);
        }


        boolean save = walRecordService.signAndSave(record);
        if (!save) {
            // 未成功
            log.error("booking >> 写入数据库失败 record = {}", record);
            throw ExchangeOrderMsgCode.ERROR_WRITE_TO_DB.asException();
        }

        // 延时同步
        delayWalSyncJob.sync(record.getMemberId(), record.getCoinUnit());
        return Optional.of(record);
    }

    @Transactional
    @Override
    @Deprecated
    public Optional<ExchangeWalletSyncRecord> sync(List<Long> ids, ExchangeWalletSyncRecord record) {

        Long id = IdWorker.getId();

        // 同步ID
        record.setId(id);
        record.setStatus(ExchangeProcessStatus.PROCESSED);
        record.setCreateTime(Calendar.getInstance().getTime());

        // 更新条件
        UpdateWrapper<ExchangeWalletWalRecord> update = new UpdateWrapper<>();
        update.set("sync_id", id).set("status", ExchangeProcessStatus.PROCESSED).in("id", ids);

        // 写入同步记录
        boolean sync = syncRecordService.save(record);

        // 更新记录
        boolean wal = walRecordService.update(update);

        // 同步到真实账户
        boolean sync1 = walletService.sync(record.getMemberId(), record.getCoinUnit(),
                record.getSumAmount(), record.getSumFrozenAmount());

        // 其中一步操作失败
        if (!sync || !wal || sync1) {
            log.error("sync >> 写入数据库失败 ids = {}, record = {}", ids, record);
            throw ExchangeOrderMsgCode.ERROR_WRITE_TO_DB.asException();
        }

        return Optional.of(record);
    }

    @Transactional
    @Override
    public Optional<ExchangeWalletSyncRecord> sync(Long memberId, String coinUnit) {

        // 流水号
        long seq = IdWorker.getId();

        // 预更新处理
        int affected = walRecordService.updateSetSyncId(memberId, coinUnit, seq);

        if (affected == 0) {
            return Optional.empty();
        }

        // 统计汇总
        WalletSyncCountDto dto = walRecordService.sumSyncId(seq);

        // 写入记录
        ExchangeWalletSyncRecord record = new ExchangeWalletSyncRecord();

        record.setId(seq);
        record.setWalletId(memberId + ":" + coinUnit);
        record.setMemberId(memberId);
        record.setCoinUnit(coinUnit);
        record.setSumAmount(dto.getAmount());
        record.setIncreasedAmount(dto.getIncrement());
        record.setSumFrozenAmount(dto.getFrozen());
        record.setStatus(ExchangeProcessStatus.PROCESSED);
        record.setCreateTime(Calendar.getInstance().getTime());

        // 保存
        boolean save = syncRecordService.save(record);

        // 同步至db
        boolean sync = walletService.sync(memberId, coinUnit, dto.getAmount(), dto.getFrozen());

        // 更新 wal
        UpdateWrapper<ExchangeWalletWalRecord> update = new UpdateWrapper<>();
        // 由于数据同步之后重签意义不大，且重签需要逐行更新可能存在性能问题
        // 同步之后，重设签名 signature = "0x0"，重放攻击将会实现
        update.eq("sync_id", seq).set("status", ExchangeProcessStatus.PROCESSED).set("signature", "0x0");
        boolean wal = walRecordService.update(update);

        // 其中一步操作失败
        if (!sync || !wal || !save) {
            log.error("sync >> 写入数据库失败 record = {}", record);
            throw ExchangeOrderMsgCode.ERROR_WRITE_TO_DB.asException();
        }

        return Optional.of(record);
    }

    // ----------------------------------------
    // SETTERS...
    // ----------------------------------------

    @Autowired
    public void setWalletService(ExchangeWalletService walletService) {
        this.walletService = walletService;
    }

    @Autowired
    public void setWalRecordService(ExchangeWalletWalRecordService walRecordService) {
        this.walRecordService = walRecordService;
    }

    @Autowired
    public void setSyncRecordService(ExchangeWalletSyncRecordService syncRecordService) {
        this.syncRecordService = syncRecordService;
    }

    @Autowired
    public void setDelayWalSyncJob(DelayWalSyncJob delayWalSyncJob) {
        this.delayWalSyncJob = delayWalSyncJob;
    }

    @Autowired
    public void setRateService(ExchangeRateService rateService) {
        this.rateService = rateService;
    }
}
