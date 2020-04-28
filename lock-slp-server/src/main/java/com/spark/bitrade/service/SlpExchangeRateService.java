package com.spark.bitrade.service;

import com.spark.bitrade.dto.Kline;

import java.math.BigDecimal;

/**
 *  K线接口
 *
 * @author young
 * @time 2019.07.09 16:35
 */
public interface SlpExchangeRateService {
    /**
     * 昨日K线
     *
     * @param symbol 交易对，如SLP/USDT
     * @return 未获取到汇率返回null
     */
    Kline yesterdayKline(String symbol);

    /**
     * 昨日汇率
     *
     * @param symbol 交易对，如SLP/USDT
     * @return 未获取到汇率，则返回null
     */
    BigDecimal exchangeRate4Yesterday(String symbol);

    /**
     * USDT实时汇率
     *
     * @param coinUit 交易对，如SLP
     * @return 未获取到汇率，则抛出异常
     */
    BigDecimal exchangeUsdtRate(String coinUit);
}
