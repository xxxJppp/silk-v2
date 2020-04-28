package com.spark.bitrade.service;

import com.spark.bitrade.entity.ExchangeCywOrder;

/**
 * 交易中的订单服务
 *
 * @author young
 * @time 2019.09.19 14:58
 */
public interface CywTradingOrderService {

    /**
     * 订单存放到Redis中
     *
     * @param exchangeCywOrder
     */
    void addTradingOrderToReids(ExchangeCywOrder exchangeCywOrder);

    /**
     * 从Redis中获取交易的订单
     *
     * @param memberId
     * @param orderId
     * @return
     */
    ExchangeCywOrder queryTradingOrderFromRedis(Long memberId, String orderId);

    /**
     * 从Redis中删除交易中的订单
     *
     * @param memberId
     * @param orderId
     * @return 返回删除的数量
     */
    long deleteTradingOrderFromReids(Long memberId, String orderId);
}
