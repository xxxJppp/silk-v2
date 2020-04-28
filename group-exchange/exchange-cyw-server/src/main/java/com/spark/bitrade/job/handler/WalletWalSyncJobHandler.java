package com.spark.bitrade.job.handler;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.CywWalletSyncRecord;
import com.spark.bitrade.entity.CywWalletWalRecord;
import com.spark.bitrade.entity.constants.CywProcessStatus;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.CywWalletOperations;
import com.spark.bitrade.service.CywWalletWalRecordService;
import com.spark.bitrade.uitl.CywWalletUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Wal日志同步任务
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/3 18:06
 */
@Slf4j
@Component
public class WalletWalSyncJobHandler {

    private CywWalletWalRecordService walRecordService;
    private CywWalletOperations walletOperations;

    public void execute() {

        // 查询所有未同步的日志
        for (SyncDto syncDto : unSynchronized()) {

            List<Long> ids = syncDto.getIds();
            CywWalletSyncRecord record = syncDto.getRecord();

            try {
                Optional<CywWalletSyncRecord> sync = walletOperations.sync(ids, record);

                if (sync.isPresent()) {
                    log.info("WAL 同步成功 [ wallet_id = {}, balance = {}, frozen = {}, increment = {} ]", record.getWalletId(),
                            record.getSumAmount(), record.getSumFrozenAmount(), record.getIncreasedAmount());
                }
                throw new MessageCodeException(CommonMsgCode.FAILURE);
            } catch (RuntimeException ex) {
                int code = 500;
                if (ex instanceof MessageCodeException) {
                    code = ((MessageCodeException) ex).getCode();
                }
                log.error("WAL 同步失败 [ wallet_id = {}, code = {},  err = '{}' ]", record.getWalletId(), code, ex.getMessage());
            }
        }

    }

    /**
     * 获取所有未同步日志汇总
     *
     * @return dto
     */
    private List<SyncDto> unSynchronized() {

        final List<SyncDto> returnValue = new ArrayList<>();

        // 查找所有未同步的记录
        QueryWrapper<CywWalletWalRecord> query = new QueryWrapper<>();
        // ??? 待确定
        query.eq("status", CywProcessStatus.NOT_PROCESSED);
        query.and(wrapper -> wrapper.isNull("sync_id").or().eq("sync_id", 0));

        Map<String, List<CywWalletWalRecord>> groupByWalletId = walRecordService.list(query).stream()
                .collect(Collectors.groupingBy(r -> r.getMemberId() + ":" + r.getCoinUnit()));

        // 按钱包ID分组
        groupByWalletId.forEach((key, records) -> returnValue.add(convertToSyncDto(key, records)));

        return returnValue;
    }

    private SyncDto convertToSyncDto(final String key, final List<CywWalletWalRecord> records) {
        List<Long> ids = new ArrayList<>();

        // 汇总统计

        // 余额
        BigDecimal balance = BigDecimal.ZERO;
        // 冻结
        BigDecimal frozen = BigDecimal.ZERO;
        // + 缓存增量
        BigDecimal increment = BigDecimal.ZERO;

        for (CywWalletWalRecord record : records) {
            balance = balance.add(record.getTradeBalance());
            frozen = frozen.add(record.getTradeFrozen());

            if (CywWalletUtils.isPositive(balance)) {
                increment = increment.add(balance);
            }

            ids.add(record.getId());
        }

        CywWalletSyncRecord sync = new CywWalletSyncRecord();
        CywWalletWalRecord record = records.get(0);

        // sync.setWalletId(record.getMemberId() + ":" + record.getCoinUnit());
        sync.setWalletId(key);
        sync.setMemberId(record.getMemberId());
        sync.setCoinUnit(record.getCoinUnit());
        sync.setSumAmount(balance);
        sync.setIncreasedAmount(increment);
        sync.setSumFrozenAmount(frozen);
        sync.setStatus(CywProcessStatus.NOT_PROCESSED);
        // sync.setCreateTime(Calendar.getInstance().getTime());

        return new SyncDto(ids, sync);
    }

    @Data
    @AllArgsConstructor
    private class SyncDto {
        private List<Long> ids;
        private CywWalletSyncRecord record;
    }

    // -------------------------------------------
    // SETTERS ...
    // -------------------------------------------

    @Autowired
    public void setWalRecordService(CywWalletWalRecordService walRecordService) {
        this.walRecordService = walRecordService;
    }

    @Autowired
    public void setWalletOperations(CywWalletOperations walletOperations) {
        this.walletOperations = walletOperations;
    }
}
