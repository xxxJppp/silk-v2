package com.spark.bitrade.service.impl;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.entity.ExchangeTrade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *  
 *
 * @author young
 * @time 2019.10.23 17:22
 */
@Slf4j
@Service
public class PushMessageImpl extends AbstractPushMessageImpl {
    @Override
    public void pushTradeRetry(ExchangeTrade trade, ExchangeOrderDirection direction) {
        push("exchange-retry-r", direction.name(), trade);
    }
}
