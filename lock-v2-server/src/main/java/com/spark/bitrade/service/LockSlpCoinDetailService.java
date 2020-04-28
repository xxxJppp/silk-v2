package com.spark.bitrade.service;

import com.spark.bitrade.entity.LockCoinActivitieSetting;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.entity.Member;

import java.math.BigDecimal;

/**
 *  slp锁仓接口
 *
 * @author young
 * @time 2019.07.12 15:49
 */
public interface LockSlpCoinDetailService {

    /**
     * 锁仓操作
     *
     * @param member                   用户信息
     * @param lockCoinActivitieSetting 活动配置信息
     * @param amount                   活动参与份数(活动为USDT)
     * @param payCoinUnit              支付币种
     * @param payCoinUnitUsdtPrice     支付币种相对USDT价格
     * @param usdt2CnyPrice            usdt对cny的价格
     * @param jyPassword               可选，资金密码
     * @param limitCountValid          可选，当前有效的活动数量限制
     * @param limitCountInDay          可选，当日参与活动的次数限制（不考虑是否有效）
     * @return
     */
    LockCoinDetail lockSlpCoin(Member member,
                               LockCoinActivitieSetting lockCoinActivitieSetting,
                               BigDecimal amount,
                               String payCoinUnit,
                               BigDecimal payCoinUnitUsdtPrice,
                               BigDecimal usdt2CnyPrice,
                               String jyPassword,
                               Integer limitCountValid,
                               Integer limitCountInDay);

    /**
     * 升仓操作
     *
     * @param member                   用户信息
     * @param lockCoinActivitieSetting 活动配置信息
     * @param amount                   活动参与份数(活动为USDT)
     * @param payCoinUnit              支付币种
     * @param payCoinUnitUsdtPrice     支付币种相对USDT价格
     * @param usdt2CnyPrice            usdt对cny的价格
     * @param jyPassword               可选，资金密码
     * @param existLockCoinDetail      待升仓的活动参与记录
     * @return
     */
    LockCoinDetail upgradeSlpPackage(Member member,
                                     LockCoinActivitieSetting lockCoinActivitieSetting,
                                     BigDecimal amount,
                                     String payCoinUnit,
                                     BigDecimal payCoinUnitUsdtPrice,
                                     BigDecimal usdt2CnyPrice,
                                     String jyPassword, LockCoinDetail existLockCoinDetail);
}
