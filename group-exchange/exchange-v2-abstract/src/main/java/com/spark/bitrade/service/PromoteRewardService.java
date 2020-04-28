package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.ExchangeWalletWalRecord;
import com.spark.bitrade.entity.RewardPromotionSetting;

/**
 * 返佣服务
 *
 * @author yangch
 * @since 2019-10-31 17:41:26
 */
public interface PromoteRewardService extends IService<RewardPromotionSetting> {
    void reword(ExchangeWalletWalRecord income);
}