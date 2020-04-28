package com.spark.bitrade.consumer;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeTrade;
import com.spark.bitrade.service.CywCancelOrderService;
import com.spark.bitrade.service.CywCompletedOrderService;
import com.spark.bitrade.service.CywTradeService;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 *  订单kafka消费
 *
 * @author young
 * @time 2019.09.17 14:02
 */
@Slf4j
@Component
public class OrderConsumer {
    @Autowired
    private CywCompletedOrderService completedOrderService;
    @Autowired
    private CywCancelOrderService cancelOrderService;
    @Autowired
    private CywTradeService tradeService;

    /**
     * 处理成交明细
     *
     * @param record
     */
    @KafkaListener(topics = "exchange-cyw-trade", group = "group-handle")
    public void handleTrade(ConsumerRecord<String, String> record) {
        tradeService.processTrade(record);
    }

    /**
     * 订单撮合完成
     *
     * @param record
     */
    @KafkaListener(topics = "exchange-cyw-order-completed", group = "group-handle")
    public void handleOrderCompleted(ConsumerRecord<String, String> record) {
        this.getService().orderCompleted(record);
    }

    /**
     * 订单取消成功（在交易内存交易队列中）
     *
     * @param record
     */
    @KafkaListener(topics = "exchange-cyw-order-cancel-success", group = "group-handle")
    public void handleOrderCanceled4Success(ConsumerRecord<String, String> record) {
        getService().orderCanceled4Success(record);
    }

    /**
     * 订单取消失败（不在内存交易队列中）
     *
     * @param record
     */
    @KafkaListener(topics = "exchange-cyw-order-cancel-fail", group = "group-handle")
    public void handleOrderCanceled4Fail(ConsumerRecord<String, String> record) {
        getService().orderCanceled4Fail(record);
    }

    /**
     * 重试
     */
    @KafkaListener(topics = "exchange-cyw-retry", group = "group-handle")
    public void handleRetry(ConsumerRecord<String, String> record) {
        if (record.key().equalsIgnoreCase("BUY")) {
            ExchangeTrade trade = JSON.parseObject(record.value(), ExchangeTrade.class);
            tradeService.retryProcessTrade(trade, ExchangeOrderDirection.BUY);
        } else if (record.key().equalsIgnoreCase("SELL")) {
            ExchangeTrade trade = JSON.parseObject(record.value(), ExchangeTrade.class);
            tradeService.retryProcessTrade(trade, ExchangeOrderDirection.SELL);
        }
    }

    @Async("order")
    void orderCompleted(ConsumerRecord<String, String> record) {
        ExchangeOrder order = JSON.parseObject(record.value(), ExchangeOrder.class);
        completedOrderService.completedOrder(order.getMemberId(), order.getOrderId(),
                order.getTradedAmount(), order.getTurnover());
    }

    @Async("order")
    void orderCanceled4Success(ConsumerRecord<String, String> record) {
        ExchangeOrder order = JSON.parseObject(record.value(), ExchangeOrder.class);
        cancelOrderService.canceledOrder(order.getMemberId(), order.getOrderId(),
                order.getTradedAmount(), order.getTurnover());
    }

    @Async("order")
    void orderCanceled4Fail(ConsumerRecord<String, String> record) {
        ExchangeOrder order = JSON.parseObject(record.value(), ExchangeOrder.class);
        cancelOrderService.canceledOrder(order.getMemberId(), order.getOrderId());
    }

    public OrderConsumer getService() {
        return SpringContextUtil.getBean(OrderConsumer.class);
    }
}
