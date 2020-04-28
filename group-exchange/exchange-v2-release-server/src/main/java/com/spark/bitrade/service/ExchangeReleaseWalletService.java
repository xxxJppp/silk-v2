package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.dto.request.ExchangeReleaseWalletDTO;
import com.spark.bitrade.entity.ExchangeReleaseWallet;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 币币交易释放-锁仓释放总数表(ExchangeReleaseWallet)表服务接口
 *
 * @author yangch
 * @since 2019-12-16 14:52:16
 */
public interface ExchangeReleaseWalletService extends IService<ExchangeReleaseWallet> {
    Optional<ExchangeReleaseWallet> find(long memberId, String coinSymbol);
    /**
     * 锁仓释放总表记录更新
     * @return Boolean
     */
    boolean updateExchangeReleaseWalletRecord(ExchangeReleaseWalletDTO exchangeReleaseWalletDTO);

    /**
     * 减少锁仓余额
     *
     * @param memberId      会员ID
     * @param coinSymbol    币种
     * @param releaseAmount 释放数量
     * @return
     */
    Boolean decreaseLockAmount(long memberId, String coinSymbol, BigDecimal releaseAmount);
}