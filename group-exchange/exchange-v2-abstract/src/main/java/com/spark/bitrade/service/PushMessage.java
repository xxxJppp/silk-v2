package com.spark.bitrade.service;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeTrade;

/**
 *  推送消息
 *
 * @author young
 * @time 2019.10.23 17:15
 */
public interface PushMessage {
    /**
     * 推送下单订单消息
     *
     * @param order
     */
    void pushOrderCreate(ExchangeOrder order);

    /**
     * 推送撤单订单请求消息
     *
     * @param order
     */
    void pushOrderCancelApply(ExchangeOrder order);

    /**
     * 推送部分成交订单消息
     *
     * @param order
     */
    void pushOrderTrade(ExchangeOrder order);

    /**
     * 推送已成交订单消息
     *
     * @param order
     */
    void pushOrderCompleted(ExchangeOrder order);

    /**
     * 推送已撤单订单消息
     *
     * @param order
     */
    void pushOrderCanceled(ExchangeOrder order);

    /**
     * 重试
     *
     * @param trade
     * @param direction
     */
    void pushTradeRetry(ExchangeTrade trade, ExchangeOrderDirection direction);

    /**
     * 推送消息
     *
     * @param topic 主题
     * @param key   key
     * @param data  数据
     */
    void push(String topic, String key, Object data);
}
