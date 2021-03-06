package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.ExchangeWalletSyncRecord;
import com.spark.bitrade.entity.dto.WalletSyncCountDto;
import com.spark.bitrade.mapper.ExchangeWalletSyncRecordMapper;
import com.spark.bitrade.service.ExchangeWalletSyncRecordService;

import java.util.Date;

/**
 * 账户WAL流水同步记录表(ExchangeWalletSyncRecord)表服务实现类
 *
 * @author archx
 * @since 2019-09-02 14:44:18
 */
public abstract class AbstractExchangeWalletSyncRecordServiceImpl
        extends ServiceImpl<ExchangeWalletSyncRecordMapper, ExchangeWalletSyncRecord>
        implements ExchangeWalletSyncRecordService {
    @Override
    public ExchangeWalletSyncRecord getNewest(Long memberId, String coinUnit) {
        return this.baseMapper.getNewest(memberId, coinUnit);
    }

    @Override
    public WalletSyncCountDto sum(Long memberId, String coinUnit, Date startTime, Date endTime) {
        return this.baseMapper.sum(memberId, coinUnit, startTime, endTime);
    }

}