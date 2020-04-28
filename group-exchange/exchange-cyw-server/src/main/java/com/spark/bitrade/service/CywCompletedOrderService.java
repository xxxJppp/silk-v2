package com.spark.bitrade.service;

import com.spark.bitrade.entity.ExchangeCywOrder;
import com.spark.bitrade.entity.ExchangeOrder;

import java.math.BigDecimal;

/**
 *  已完成订单服务
 *
 * @author young
 * @time 2019.09.19 18:59
 */
public interface CywCompletedOrderService {

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
     * 从Redis中获取已完成的订单
     *
     * @param orderId
     * @return
     */
    ExchangeCywOrder queryCompletedOrderFromRedis(String orderId);

    /**
     * 入库已完成的订单
     * 备注：失败的订单记录到告警表，如记录失败则告警到错误日志中
     *
     * @param orderId 订单号
     */
    void completedOrder(String orderId);

    /**
     * 重做接口
     *
     * @param orderId
     */
    void redo(String orderId);
}
