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
 *  撤单服务
 *
 * @author young
 * @time 2019.09.19 14:17
 */
@Slf4j
@Service
public class ExchangeCancelOrderServiceImpl extends AbstractExchangeCancelOrderServiceImpl {
    @Autowired
    private ForwardService forwardService;

    /**
     * 处理撤销的订单，处理失败则记录错误
     *
     * @param order
     */
    @Override
    protected void confidentCanceledOrder(ExchangeOrder order) {
        try {
            // 转发请求
            if (forwardService.getStrategy(order.getSymbol()).isPresent()) {
                Optional<ExchangeForwardStrategyConfiguration> optional = forwardService.getStrategy(order.getSymbol());

                if (optional.get().getEnableCancelOrderBuy()) {
                    forwardService.canceledOrder(order.getSymbol(), order.getMemberId(), order.getOrderId(), order.getTradedAmount(), order.getTurnover());
                    return;
                } else if (optional.get().getEnableCancelOrderSell()) {
                    forwardService.canceledOrder(order.getSymbol(), order.getMemberId(), order.getOrderId(), order.getTradedAmount(), order.getTurnover());
                    return;
                }
            }

            this.getService().canceledOrder(order);

            // 通过kafka推送已撤销订单的消息
            pushMessage.pushOrderCanceled(order);
        } catch (Exception ex) {
            log.warn("撤销订单失败,记录失败订单。订单号=" + order.getOrderId(), ex);
            try {
                //记录操作失败的订单
                businessErrorMonitorService.add(BusinessErrorMonitorType.EXCHANGE__USER_CANCEL_FAIL,
                        order.toString(), ex.getMessage());
            } catch (Exception ex1) {
                log.error("处理完成的订单失败，需手工处理。订单号=" + order.getOrderId(), ex1);
            }
        }
    }
}
