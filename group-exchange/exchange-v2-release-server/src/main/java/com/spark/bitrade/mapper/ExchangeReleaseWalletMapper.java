package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.ExchangeReleaseWallet;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * 币币交易释放-锁仓释放总数表(ExchangeReleaseWallet)表数据库访问层
 *
 * @author yangch
 * @since 2019-12-16 14:52:16
 */
public interface ExchangeReleaseWalletMapper extends BaseMapper<ExchangeReleaseWallet> {

    /**
     * 减少锁仓余额
     *
     * @param id            ID
     * @param releaseAmount 释放数量
     * @return
     */
    int decreaseLockAmount(@Param("id") String id, @Param("amount") BigDecimal releaseAmount);

    /**
     *  更新锁仓释放总数记录
     *
     * @param id
     * @parm   am
     * @return
     */
    int updateExchangeWalletRecord(@Param("id") String id, @Param("lockAmount") BigDecimal lockAmount);

    /**
     *  添加锁仓释放总数记录
     *
     * @return int
     */
    int addExchangeWalletRecord(@Param("id")String id, @Param("memberId") Integer memberId, @Param("coinSymbol") String coinSymbol, @Param("lockAmount") BigDecimal lockAmount);

}