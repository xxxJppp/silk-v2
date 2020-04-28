package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.config.ExchangeForwardStrategyConfiguration;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.entity.ExchangeTrade;
import com.spark.bitrade.service.ForwardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 *  交易明细处理
 *
 * @author young
 * @time 2019.09.03 14:44
 */
@Slf4j
@Service
public class ExchangeTradeServiceImpl
        extends AbstractExchangeTradeServiceImpl
        implements CommandLineRunner {
    @Autowired
    private ForwardService forwardService;

    @Override
    public void run(String... strings) throws Exception {
        super.run(strings);
    }

    @Override
    public ExchangeTrade processTrade(ConsumerRecord<String, String> record) {
        log.info("成交明细,key={}, value={}", record.key(), record.value());
        ExchangeTrade trade = JSON.parseObject(record.value(), ExchangeTrade.class);

        // 转发请求
        if (forwardService.getStrategy(trade.getSymbol()).isPresent()) {
            Optional<ExchangeForwardStrategyConfiguration> optional = forwardService.getStrategy(trade.getSymbol());
            if (record.key().equalsIgnoreCase("BUY")) {
                if (optional.get().getEnableTradeBuy()) {
                    getService().asyncProcessTrade(trade, ExchangeOrderDirection.BUY);
                    return trade;
                }
            } else {
                if (optional.get().getEnableTradeSell()) {
                    getService().asyncProcessTrade(trade, ExchangeOrderDirection.SELL);
                    return trade;
                }
            }
        }

        // 正常交易
        return super.processTrade(record, trade);
    }

    @Override
    @Async("trade")
    public void asyncProcessTrade(ExchangeTrade trade, ExchangeOrderDirection direction) {
        try {
            // 转发请求
            if (forwardService.getStrategy(trade.getSymbol()).isPresent()) {
                Optional<ExchangeForwardStrategyConfiguration> optional = forwardService.getStrategy(trade.getSymbol());
                if (direction.equals(ExchangeOrderDirection.BUY) && optional.get().getEnableTradeBuy()) {
                    forwardService.tradeBuy(trade);
                    return;
                }

                if (direction.equals(ExchangeOrderDirection.SELL) && optional.get().getEnableTradeSell()) {
                    forwardService.tradeSell(trade);
                    return;
                }
            }
        } catch (Exception e) {
            // 远程调用失败，则记录到告警表
            addWarnRecord(trade, direction, e);
        }

        // 正常交易
        super.asyncProcessTrade(trade, direction);
    }
}
