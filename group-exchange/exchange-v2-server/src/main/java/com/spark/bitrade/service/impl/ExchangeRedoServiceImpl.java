package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.config.ExchangeForwardStrategyConfiguration;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.entity.BusinessErrorMonitor;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeTrade;
import com.spark.bitrade.service.ForwardService;
import com.spark.bitrade.service.IExchange2ReleaseService;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.OrderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 *  
 *
 * @author young
 * @time 2019.09.30 11:11
 */
@Slf4j
@Service
public class ExchangeRedoServiceImpl extends AbstractExchangeRedoServiceImpl {
    @Autowired
    private IExchange2ReleaseService releaseService;
    @Autowired
    private ForwardService forwardService;


    @Override
    protected void redoTradeBuy(BusinessErrorMonitor warn) {
        ExchangeTrade trade = JSON.parseObject(warn.getInData(), ExchangeTrade.class);
        if (trade.getBuyOrderId().startsWith("R")) {
            // 适配 新币上线的买入重做
            ExceptionUitl.throwsMessageCodeExceptionIfFailed(releaseService.tradeBuy(trade));
            return;
        }

        // 转发请求
        if (forwardService.getStrategy(trade.getSymbol()).isPresent()) {
            Optional<ExchangeForwardStrategyConfiguration> optional = forwardService.getStrategy(trade.getSymbol());
            if (optional.get().getEnableTradeBuy()) {
                ExceptionUitl.throwsMessageCodeExceptionIfFailed(forwardService.tradeBuy(trade));
                return;
            }

            if (optional.get().getEnableTradeSell()) {
                ExceptionUitl.throwsMessageCodeExceptionIfFailed(forwardService.tradeBuy(trade));
                return;
            }
        }

        tradeService.processTrade(trade, ExchangeOrderDirection.BUY);
    }

    @Override
    protected void redoTradeSell(BusinessErrorMonitor warn) {
        ExchangeTrade trade = JSON.parseObject(warn.getInData(), ExchangeTrade.class);

        // 转发请求
        if (forwardService.getStrategy(trade.getSymbol()).isPresent()) {
            Optional<ExchangeForwardStrategyConfiguration> optional = forwardService.getStrategy(trade.getSymbol());
            if (optional.get().getEnableTradeBuy()) {
                ExceptionUitl.throwsMessageCodeExceptionIfFailed(forwardService.tradeSell(trade));
                return;
            }

            if (optional.get().getEnableTradeSell()) {
                ExceptionUitl.throwsMessageCodeExceptionIfFailed(forwardService.tradeSell(trade));
                return;
            }
        }

        tradeService.processTrade(trade, ExchangeOrderDirection.SELL);
    }


    @Override
    protected void redoCompletedOrder(BusinessErrorMonitor warn) {
        ExchangeOrder order = JSON.parseObject(warn.getInData(), ExchangeOrder.class);
        // 转发请求
        if (redo(warn, order)) {
            return;
        }

        completedOrderService.redo(order);
    }

    @Override
    protected void redoCancelOrder(BusinessErrorMonitor warn) {
        ExchangeOrder order = JSON.parseObject(warn.getInData(), ExchangeOrder.class);

        // 转发请求
        if (redo(warn, order)) {
            return;
        }

        OrderUtil.checkOrderIdFormat(order.getOrderId());
        /// AssertUtil.isTrue(order.getOrderId().startsWith(ExchangeConstants.ORDER_PREFIX), ExchangeOrderMsgCode.BAD_ORDER);
        cancelOrderService.redo(order);
    }

    private boolean redo(BusinessErrorMonitor warn, ExchangeOrder order) {
        if (forwardService.getStrategy(order.getSymbol()).isPresent()) {
            Optional<ExchangeForwardStrategyConfiguration> optional = forwardService.getStrategy(order.getSymbol());

            if (optional.get().getEnableCompleteOrderBuy()) {
                ExceptionUitl.throwsMessageCodeExceptionIfFailed(forwardService.redo(warn.getId(), order.getSymbol()));
                return true;
            } else if (optional.get().getEnableCompleteOrderSell()) {
                ExceptionUitl.throwsMessageCodeExceptionIfFailed(forwardService.redo(warn.getId(), order.getSymbol()));
                return true;
            }
        }
        return false;
    }
}
