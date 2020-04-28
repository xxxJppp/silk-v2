package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.ExchangeWallet;
import com.spark.bitrade.entity.ExchangeWalletResetRecord;

/**
 * 用户币币账户重置记录(ExchangeWalletResetRecord)表服务接口
 *
 * @author yangch
 * @since 2019-11-21 11:14:51
 */
public interface ExchangeWalletResetRecordService extends IService<ExchangeWalletResetRecord> {
    /**
     * 添加重置记录
     *
     * @param wallet
     * @return
     */
    boolean addResetRecord(ExchangeWallet wallet);
}