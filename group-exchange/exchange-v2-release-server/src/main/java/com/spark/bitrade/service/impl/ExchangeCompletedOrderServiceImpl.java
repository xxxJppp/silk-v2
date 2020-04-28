package com.spark.bitrade.service.impl;

import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.service.ExchangeReleaseReferrerOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *  
 *
 * @author young
 * @time 2019.09.19 19:00
 */
@Slf4j
@Service
public class ExchangeCompletedOrderServiceImpl extends AbstractExchangeCompletedOrderServiceImpl {
    @Autowired
    private ExchangeReleaseReferrerOrderService referrerOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExchangeOrder completedOrder(ExchangeOrder order) {
        super.completedOrder(order);

        // 发起兑换任务
        referrerOrderService.preExchange(order);

        return order;
    }
}
