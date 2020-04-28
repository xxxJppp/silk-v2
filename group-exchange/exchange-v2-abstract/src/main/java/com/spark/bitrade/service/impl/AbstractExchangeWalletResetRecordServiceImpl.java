package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.spark.bitrade.entity.ExchangeWallet;
import com.spark.bitrade.entity.ExchangeWalletResetRecord;
import com.spark.bitrade.mapper.ExchangeWalletResetRecordMapper;
import com.spark.bitrade.service.ExchangeWalletResetRecordService;
import org.springframework.stereotype.Service;

/**
 * 用户币币账户重置记录(ExchangeWalletResetRecord)表服务实现类
 *
 * @author yangch
 * @since 2019-11-21 11:14:51
 */
public abstract class AbstractExchangeWalletResetRecordServiceImpl
        extends ServiceImpl<ExchangeWalletResetRecordMapper, ExchangeWalletResetRecord> implements ExchangeWalletResetRecordService {

    @Override
    public boolean addResetRecord(ExchangeWallet wallet) {
        ExchangeWalletResetRecord record = new ExchangeWalletResetRecord();
        record.setId(IdWorker.getId());
        record.setWalletId(wallet.getId());
        record.setMemberId(wallet.getMemberId());
        record.setCoinUnit(wallet.getCoinUnit());
        record.setBalance(wallet.getBalance());
        record.setFrozenBalance(wallet.getFrozenBalance());

        return SqlHelper.retBool(this.baseMapper.insert(record));
    }
}