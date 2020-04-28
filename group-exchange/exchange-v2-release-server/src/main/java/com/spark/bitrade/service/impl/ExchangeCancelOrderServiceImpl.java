package com.spark.bitrade.service.impl;

import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.service.ExchangeReleaseReferrerOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *  撤单服务
 *
 * @author young
 * @time 2019.09.19 14:17
 */
@Slf4j
@Service
public class ExchangeCancelOrderServiceImpl extends AbstractExchangeCancelOrderServiceImpl {
    @Autowired
    private ExchangeReleaseReferrerOrderService referrerOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void canceledOrder(ExchangeOrder order) {
        super.canceledOrder(order);

        // 发起兑换任务
        referrerOrderService.preExchange(order);
    }
}
