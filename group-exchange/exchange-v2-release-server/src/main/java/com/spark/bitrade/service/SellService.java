package com.spark.bitrade.service;

import com.spark.bitrade.dto.ExchangeOrderSellStat;

import java.math.BigDecimal;
import java.util.Optional;

/**
 *  卖单服务接口
 *
 * @author young
 * @time 2019.12.16 20:32
 */
public interface SellService {
    /**
     * 统计指定价格下有效的交易数量
     *
     * @param symbol 交易对
     * @param pric   价格
     * @return
     */
    Optional<ExchangeOrderSellStat> sellStatByPrice(String symbol, BigDecimal pric);

    /**
     * 清理缓存
     *
     * @param symbol 交易对
     * @param price  价格
     */
    void cleanCached(String symbol, BigDecimal price);

    /**
     * 更新最新的卖1价格
     *
     * @param symbol   交易对
     * @param price 价格
     */
    void updateSell1NewestPrice(String symbol, BigDecimal price);

    /**
     * 最新的卖1价格
     *
     * @param symbol 交易对
     * @return
     */
    Optional<BigDecimal> getSell1NewestPrice(String symbol) ;
}
