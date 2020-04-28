package com.spark.bitrade.service.impl;

import com.spark.bitrade.entity.ExchangeTrade;
import com.spark.bitrade.service.SellService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *  交易明细处理
 *
 * @author young
 * @time 2019.09.03 14:44
 */
@Slf4j
@Service
public class ExchangeTradeServiceImpl extends AbstractExchangeTradeServiceImpl {

    @Autowired
    private SellService sellService;

    @Override
    public ExchangeTrade processTrade(ConsumerRecord<String, String> record) {
        ExchangeTrade trade = super.processTrade(record);

        //更新 最新卖1价格
        sellService.updateSell1NewestPrice(trade.getSymbol(), trade.getPrice());

        return trade;
    }
}
