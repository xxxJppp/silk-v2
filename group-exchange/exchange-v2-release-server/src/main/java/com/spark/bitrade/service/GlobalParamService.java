package com.spark.bitrade.service;

import java.math.BigDecimal;

/**
 * GlobalParamService
 *
 * @author Archx[archx@foxmail.com]
 * @since 2020/1/19 14:08
 */
public interface GlobalParamService {

    String EXCHANGE_RELEASE_ID = "exchange-release";

    /**
     * 手续费奖励比例：交易手续费的奖励比例，默认为0.8，用小数表示
     *
     * @return
     */
    BigDecimal getFeeAwardRatio();

    /**
     * 累计奖购买数量：直推用户累计购买ESP消耗的USDT数量
     *
     * @return
     */
    BigDecimal getAccumulationBuyTotalAmount();

    /**
     * 累计奖励数量：直推用户达到累计奖购买数量后，奖励推荐人USDT的数量
     *
     * @return
     */
    BigDecimal getAccumulationBuyAwardAmount();

    /**
     * 闪兑比例：买单最大闪兑比例，默认为0.5，用小数表示
     *
     * @return
     */
    BigDecimal getBuyMaxExchangeRatio();

    /**
     * 闪兑手续费：闪兑手续费比例，默认为0.01，用小数表示
     *
     * @return
     */
    BigDecimal getExchangeRate();

    /**
     * 归集总帐号：奖励赠送账户
     *
     * @return
     */
    Long getAwardAccount();
}
