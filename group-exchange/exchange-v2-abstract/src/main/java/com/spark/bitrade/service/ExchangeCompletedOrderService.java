package com.spark.bitrade.service;

import com.spark.bitrade.entity.ExchangeOrder;

import java.math.BigDecimal;

/**
 *  已完成订单服务
 *
 * @author young
 * @time 2019.09.19 18:59
 */
public interface ExchangeCompletedOrderService {

    /**
     * 成交订单
     *
     * @param memberId     用户ID
     * @param orderId      订单号
     * @param tradedAmount 交易数量
     * @param turnover     交易额
     * @return
     */
    ExchangeOrder completedOrder(Long memberId, String orderId, BigDecimal tradedAmount, BigDecimal turnover);


    /**
     * 重做接口
     *
     * @param order
     */
    void redo(ExchangeOrder order);
}
