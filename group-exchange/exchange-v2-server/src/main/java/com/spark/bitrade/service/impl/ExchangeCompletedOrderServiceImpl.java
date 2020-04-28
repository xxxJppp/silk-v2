package com.spark.bitrade.service.impl;

import com.spark.bitrade.config.ExchangeForwardStrategyConfiguration;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.constants.BusinessErrorMonitorType;
import com.spark.bitrade.service.ForwardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    private ForwardService forwardService;

    /**
     * 处理完成的订单，处理失败则记录错误
     *
     * @param order
     */
    @Override
    protected void confidentCompletedOrder(ExchangeOrder order) {
        try {

            // 转发请求
            if (forwardService.getStrategy(order.getSymbol()).isPresent()) {
                Optional<ExchangeForwardStrategyConfiguration> optional = forwardService.getStrategy(order.getSymbol());

                if (optional.get().getEnableCompleteOrderBuy()) {
                    forwardService.completedOrder(order.getSymbol(), order.getMemberId(), order.getOrderId(), order.getTradedAmount(), order.getTurnover());
                    return;
                } else if (optional.get().getEnableCompleteOrderSell()) {
                    forwardService.completedOrder(order.getSymbol(), order.getMemberId(), order.getOrderId(), order.getTradedAmount(), order.getTurnover());
                    return;
                }
            }

            this.getService().completedOrder(order);

            // 通过kafka推送已完成订单的消息
            pushMessage.pushOrderCompleted(order);

        } catch (Exception ex) {
            log.warn("已完成的订单入库失败,记录失败订单。订单号=" + order.getOrderId(), ex);
            try {
                //记录操作失败的订单
                businessErrorMonitorService.add(BusinessErrorMonitorType.EXCHANGE__USER_COMPLETED,
                        order.toString(), ex.getMessage());
            } catch (Exception ex1) {
                log.error("处理已完成的订单入库失败，需手工处理。订单号=" + order.getOrderId(), ex1);
            }
        }
    }
}
