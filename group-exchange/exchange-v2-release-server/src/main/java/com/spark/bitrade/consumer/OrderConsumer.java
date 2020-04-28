package com.spark.bitrade.consumer;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.entity.ExchangeTrade;
import com.spark.bitrade.service.ExchangeTradeService;
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
    private ExchangeTradeService tradeService;

    /**
     * 处理成交明细
     *
     * @param record
     */
    @KafkaListener(topics = "exchange-user-trade-r", group = "group-handle")
    public void handleTrade(ConsumerRecord<String, String> record) {
        if (record.key().equalsIgnoreCase(ExchangeOrderDirection.BUY.name())) {
            // 处理买入后，冻结24小时到账
            tradeService.processTrade(record);
        } else {
            log.error("不支持的操作。record={}", record);
        }
    }

    /**
     * 重试
     */
    @KafkaListener(topics = "exchange-retry-r", group = "group-handle")
    public void handleRetry(ConsumerRecord<String, String> record) {
        getService().tradeRetry(record);
    }

    @Async
    void tradeRetry(ConsumerRecord<String, String> record) {
        if (record.key().equalsIgnoreCase(ExchangeOrderDirection.BUY.name())) {
            ExchangeTrade trade = JSON.parseObject(record.value(), ExchangeTrade.class);
            tradeService.retryProcessTrade(trade, ExchangeOrderDirection.BUY);
        } else {
            log.error("不支持的操作。record={}", record);
        }
    }

    public OrderConsumer getService() {
        return SpringContextUtil.getBean(OrderConsumer.class);
    }
}
