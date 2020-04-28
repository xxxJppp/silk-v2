package com.spark.bitrade.service;

import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.util.MessageRespResult;

import java.math.BigDecimal;

/**
 *  模式活动锁仓操作
 *
 * @author young
 * @time 2019.06.27 14:04
 */
public interface LockSlpService {

    /**
     * SLP锁仓操作
     * 注意：通过分布式事务，先进行闪兑操作，再进行锁仓操作
     *
     * @param apiKey
     * @param appId              应用ID
     * @param id                 活动ID
     * @param amount             参与活动数量
     * @param limitCountValid    当前有效的活动数量限制
     * @param limitCountInDay    当日参与活动的次数限制
     * @param holdCoinSymbol     活动参与币种
     * @param lockCoinSymbol     活动币种
     * @param coinSymbolRate     活动参与币种汇率
     * @param lockCoinSymbolRate 活动币种汇率
     * @return
     */
    MessageRespResult<LockCoinDetail> lockSlpCoin(String apiKey,
                                                  String appId,
                                                  Long id,
                                                  BigDecimal amount,
                                                  int limitCountValid,
                                                  int limitCountInDay,
                                                  String holdCoinSymbol,
                                                  String lockCoinSymbol,
                                                  BigDecimal coinSymbolRate,
                                                  BigDecimal lockCoinSymbolRate) ;
}
