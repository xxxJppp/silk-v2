package com.spark.bitrade.service;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeTrade;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 *  交易明细处理接口
 *
 * @author young
 * @time 2019.09.03 14:36
 */
public interface CywTradeService {
    /**
     * 处理交易明细
     *
     * @param trade     交易明细
     * @param direction 交易方向
     * @return
     */
    ExchangeOrder processTrade(final ExchangeTrade trade, ExchangeOrderDirection direction);

    /**
     * 异步处理交易明细
     *
     * @param trade     交易明细
     * @param direction 交易方向
     * @return
     */
    void asyncProcessTrade(final ExchangeTrade trade, ExchangeOrderDirection direction);

    void processTrade(ConsumerRecord<String, String> record);

    /**
     * 重试接口
     *
     * @param trade
     * @param direction
     */
    void retryProcessTrade(ExchangeTrade trade, ExchangeOrderDirection direction);
}
