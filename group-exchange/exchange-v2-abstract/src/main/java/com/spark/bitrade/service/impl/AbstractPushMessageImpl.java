package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeTrade;
import com.spark.bitrade.service.PushMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *  
 *
 * @author young
 * @time 2019.10.23 17:22
 */
@Slf4j
public abstract class AbstractPushMessageImpl implements PushMessage {
    @Autowired
    protected KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void pushOrderCreate(ExchangeOrder order) {
        // 提交下单消息
        kafkaTemplate.send("exchange-order", order.getSymbol(), JSON.toJSONString(order));
    }

    @Override
    public void pushOrderCancelApply(ExchangeOrder order) {
        // 提交撤单消息
        kafkaTemplate.send("exchange-order-cancel", order.getSymbol(), JSON.toJSONString(order));
    }

    @Override
    public void pushOrderTrade(ExchangeOrder order) {
        kafkaTemplate.send("push-order-trade", order.getSymbol(), JSON.toJSONString(order));
    }

    @Override
    @Async
    public void pushOrderCompleted(ExchangeOrder order) {
        try {
            kafkaTemplate.send("push-order-completed", order.getSymbol(), JSON.toJSONString(order));
        } catch (Exception ex) {
            log.error("推送已成交订单消息失败。", ex);
        }
    }

    @Override
    @Async
    public void pushOrderCanceled(ExchangeOrder order) {
        try {
            kafkaTemplate.send("push-order-canceled", order.getSymbol(), JSON.toJSONString(order));
        } catch (Exception ex) {
            log.error("推送已成交订单消息失败。", ex);
        }
    }

    @Override
    public void pushTradeRetry(ExchangeTrade trade, ExchangeOrderDirection direction) {
        kafkaTemplate.send("exchange-retry", direction.name(), JSON.toJSONString(trade));
    }

    /**
     * 推送消息
     *
     * @param topic 主题
     * @param key   key
     * @param data  数据
     */
    @Override
    public void push(String topic, String key, Object data) {
        kafkaTemplate.send(topic, key, JSON.toJSONString(data));
    }
}
