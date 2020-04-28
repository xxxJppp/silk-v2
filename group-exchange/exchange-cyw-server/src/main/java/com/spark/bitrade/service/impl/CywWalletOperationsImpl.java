package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constants.ExchangeCywMsgCode;
import com.spark.bitrade.entity.CywWalletSyncRecord;
import com.spark.bitrade.entity.CywWalletWalRecord;
import com.spark.bitrade.entity.constants.CywProcessStatus;
import com.spark.bitrade.entity.constants.CywRedisKeys;
import com.spark.bitrade.entity.dto.WalletSyncCountDto;
import com.spark.bitrade.job.DelayWalSyncJob;
import com.spark.bitrade.redis.IncrDecrScriptResultExecutor;
import com.spark.bitrade.redis.ScriptResult;
import com.spark.bitrade.redis.ScriptResultExecutor;
import com.spark.bitrade.service.CywWalletOperations;
import com.spark.bitrade.service.CywWalletService;
import com.spark.bitrade.service.CywWalletSyncRecordService;
import com.spark.bitrade.service.CywWalletWalRecordService;
import com.spark.bitrade.uitl.CywWalletUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * CywWalletOperationsImpl
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/2 16:06
 */
@Slf4j
@Service
public class CywWalletOperationsImpl implements CywWalletOperations, InitializingBean {

    private CywWalletService walletService;
    private CywWalletWalRecordService walRecordService;
    private CywWalletSyncRecordService syncRecordService;
    private StringRedisTemplate redisTemplate;

    private ScriptResultExecutor<Long> decreaseExecutor;

    private DelayWalSyncJob delayWalSyncJob;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 加载脚本
        decreaseExecutor = new IncrDecrScriptResultExecutor(redisTemplate, "redis/decrby.lua");
    }

    @Override
    public Optional<BigDecimal> balance(Long memberId, String coinUnit) {

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String walletKey = CywRedisKeys.getCywWalletKey(memberId, coinUnit);

        // 同步判断
        boolean isSynchronized = CywWalletUtils.isTrue(redisTemplate.hasKey(walletKey));

        // 余额还未同步
        if (!isSynchronized) {
            log.error("balance >> 账户余额还未同步 member_id = {}, coin_unit = {}", memberId, coinUnit);
            throw ExchangeCywMsgCode.ERROR_BALANCE_NOT_SYNC.asException();
        }

        // 获取余额
        Long value = operations.increment(walletKey, 0L);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(CywWalletUtils.fromAccuracyValue(value));
    }

    @Override
    public Optional<BigDecimal> increment(Long memberId, String coinUnit, BigDecimal delta) {

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String walletKey = CywRedisKeys.getCywWalletKey(memberId, coinUnit);


        // 增加余额
        Long value = operations.increment(walletKey, CywWalletUtils.toAccuracyValue(delta));
        if (value == null) {
            return Optional.empty();
        }

        return Optional.of(new BigDecimal(value));
    }

    @Transactional
    @Override
    public Optional<CywWalletWalRecord> booking(CywWalletWalRecord record) {

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String walletKey = CywRedisKeys.getCywWalletKey(record.getMemberId(), record.getCoinUnit());

        // @see IdWorkerConfiguration
        record.setId(IdWorker.getId());

        // 增减判断
        boolean isDecrease = CywWalletUtils.isNegative(record.getTradeBalance());
        // 同步判断 省略该步骤，由前置条件 balance(Long memberId, String coinUnit) 判断
        // boolean isSynchronized = CywWalletUtils.isTrue(redisTemplate.hasKey(walletKey));

        // 扣除操作
        if (isDecrease) {

            long delta = CywWalletUtils.toAccuracyValue(record.getTradeBalance());

            // 尝试扣除
            ScriptResult<Long> execute = decreaseExecutor.execute(walletKey, Math.abs(delta));
            // 余额不足
            if (!execute.isSuccess()) {
                log.error("booking >> 账户余额不足 member_id = {}, coin_unit = {}, delta = {}", record.getMemberId(), record.getCoinUnit(), delta);
                throw ExchangeCywMsgCode.ERROR_BALANCE_NOT_ENOUGH.asException();
            }

            try {
                if (!walRecordService.save(record)) {
                    // 未成功
                    throw ExchangeCywMsgCode.ERROR_WRITE_TO_DB.asException();
                }
            } catch (Exception ex) {
                // 还回去，健壮性考虑
                operations.increment(walletKey, Math.abs(delta));
                log.error("booking >> 写入数据库失败 record = {}", record);
                log.error("booking >> 写入数据库失败", ex);
                throw ExchangeCywMsgCode.ERROR_WRITE_TO_DB.asException();
            }

            // 延时同步
            delayWalSyncJob.sync(record.getMemberId(), record.getCoinUnit());
            return Optional.of(record);
        }


        boolean save = walRecordService.save(record);
        if (!save) {
            // 未成功
            log.error("booking >> 写入数据库失败 record = {}", record);
            throw ExchangeCywMsgCode.ERROR_WRITE_TO_DB.asException();
        }

        // 延时同步
        delayWalSyncJob.sync(record.getMemberId(), record.getCoinUnit());
        return Optional.of(record);
    }

    @Transactional
    @Override
    public Optional<CywWalletSyncRecord> sync(List<Long> ids, CywWalletSyncRecord record) {

        Long id = IdWorker.getId();

        // 同步ID
        record.setId(id);
        record.setStatus(CywProcessStatus.PROCESSED);
        record.setCreateTime(Calendar.getInstance().getTime());

        // 更新条件
        UpdateWrapper<CywWalletWalRecord> update = new UpdateWrapper<>();
        update.set("sync_id", id).set("status", CywProcessStatus.PROCESSED).in("id", ids);

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
            throw ExchangeCywMsgCode.ERROR_WRITE_TO_DB.asException();
        }

        // 增量 + redis
        if (!CywWalletUtils.isNone(record.getIncreasedAmount())) {
            String walletKey = CywRedisKeys.getCywWalletKey(record.getMemberId(), record.getCoinUnit());
            Long increment = redisTemplate.opsForValue().increment(walletKey,
                    CywWalletUtils.toAccuracyValue(record.getIncreasedAmount()));

            if (increment == null) {
                log.error("sync >> 同步到缓存失败 record = {}", record);
                throw ExchangeCywMsgCode.ERROR_SYNC_TO_CACHE.asException();
            }
        }

        return Optional.of(record);
    }

    @Transactional
    @Override
    public Optional<CywWalletSyncRecord> sync(Long memberId, String coinUnit) {

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
        CywWalletSyncRecord record = new CywWalletSyncRecord();

        record.setId(seq);
        record.setWalletId(memberId + ":" + coinUnit);
        record.setMemberId(memberId);
        record.setCoinUnit(coinUnit);
        record.setSumAmount(dto.getAmount());
        record.setIncreasedAmount(dto.getIncrement());
        record.setSumFrozenAmount(dto.getFrozen());
        record.setStatus(CywProcessStatus.PROCESSED);
        record.setCreateTime(Calendar.getInstance().getTime());

        // 保存
        boolean save = syncRecordService.save(record);

        // 同步至db
        boolean sync = walletService.sync(memberId, coinUnit, dto.getAmount(), dto.getFrozen());

        // 更新 wal
        UpdateWrapper<CywWalletWalRecord> update = new UpdateWrapper<>();
        update.eq("sync_id", seq).set("status", CywProcessStatus.PROCESSED);
        boolean wal = walRecordService.update(update);

        // 其中一步操作失败
        if (!sync || !wal || !save) {
            log.error("sync >> 写入数据库失败 record = {}", record);
            throw ExchangeCywMsgCode.ERROR_WRITE_TO_DB.asException();
        }


        // 增量 + redis
        if (!CywWalletUtils.isNone(dto.getIncrement())) {
            String walletKey = CywRedisKeys.getCywWalletKey(memberId, coinUnit);
            Long increment = redisTemplate.opsForValue().increment(walletKey,
                    CywWalletUtils.toAccuracyValue(dto.getIncrement()));

            if (increment == null) {
                log.error("sync >> 同步到缓存失败 record = {}", dto);
                throw ExchangeCywMsgCode.ERROR_SYNC_TO_CACHE.asException();
            }
        }

        return Optional.of(record);
    }

    // ----------------------------------------
    // SETTERS...
    // ----------------------------------------

    @Autowired
    public void setWalletService(CywWalletService walletService) {
        this.walletService = walletService;
    }

    @Autowired
    public void setWalRecordService(CywWalletWalRecordService walRecordService) {
        this.walRecordService = walRecordService;
    }

    @Autowired
    public void setSyncRecordService(CywWalletSyncRecordService syncRecordService) {
        this.syncRecordService = syncRecordService;
    }

    @Autowired
    public void setRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        this.redisTemplate = new StringRedisTemplate(redisConnectionFactory);
    }

    @Autowired
    public void setDelayWalSyncJob(DelayWalSyncJob delayWalSyncJob) {
        this.delayWalSyncJob = delayWalSyncJob;
    }
}
