package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.ExchangeReleaseAwardTotal;

import java.math.BigDecimal;

/**
 * 币币交易-推荐人累计买入奖励累计表(ExchangeReleaseAwardTotal)表服务接口
 *
 * @author yangch
 * @since 2020-02-05 17:31:07
 */
public interface ExchangeReleaseAwardTotalService extends IService<ExchangeReleaseAwardTotal> {
    /**
     * 累计直推用户的买币数量
     *
     * @param memberId  推荐人ID
     * @param buyAmount 待累计的卖币数量
     * @return
     */
    boolean addTotalBuyAmount(long memberId, BigDecimal buyAmount);

    /**
     * 更新奖励
     *
     * @param memberId          推荐人ID
     * @param buyAmount         待累计的卖币数量
     * @param awardAmount       奖励数量
     * @param awardTimes        条件：奖励次数
     * @param minTotalBuyAmount 条件：满足的最低累计数量
     * @return
     */
    boolean updateAward(long memberId,
                        BigDecimal buyAmount,
                        BigDecimal awardAmount,
                        int awardTimes,
                        BigDecimal minTotalBuyAmount);
}