package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.entity.BusinessErrorMonitor;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeTrade;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.OrderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 *  
 *
 * @author young
 * @time 2019.09.30 11:11
 */
@Slf4j
public abstract class AbstractExchangeRedoServiceImpl implements ExchangeRedoService {

    @Autowired
    protected BusinessErrorMonitorService businessErrorMonitorService;
    @Autowired
    protected ExchangeTradeService tradeService;
    @Autowired
    protected ExchangeCompletedOrderService completedOrderService;
    @Autowired
    protected ExchangeCancelOrderService cancelOrderService;
    @Autowired
    protected ExchangeOrderValidator orderValidator;

    @Override
    public boolean redo(long id) {
        BusinessErrorMonitor warn = businessErrorMonitorService.getById(id);
        if (warn == null || warn.getMaintenanceStatus() == BooleanEnum.IS_TRUE) {
            return true;
        }

        try {
            switch (warn.getType()) {
                case EXCHANGE__USER_TRADE_BUY:
                    this.redoTradeBuy(warn);
                    break;
                case EXCHANGE__USER_TRADE_SELL:
                    this.redoTradeSell(warn);
                    break;
                case EXCHANGE__USER_COMPLETED:
                    this.redoCompletedOrder(warn);
                    break;
                case EXCHANGE__USER_CANCEL_FAIL:
                    this.redoCancelOrder(warn);
                    break;
                case EXCHANGE__USER_CHECK_FAIL:
                    this.redoCheckOrder(warn);
                    break;
                default:
                    throw new Exception("无效的业务类型");
            }

            this.redoSuccess(warn);
            return true;
        } catch (Exception ex) {
            this.redoFail(warn, ex.getMessage());
        }

        return false;
    }

    protected void redoTradeBuy(BusinessErrorMonitor warn) {
        ExchangeTrade trade = JSON.parseObject(warn.getInData(), ExchangeTrade.class);
        tradeService.processTrade(trade, ExchangeOrderDirection.BUY);
    }

    protected void redoTradeSell(BusinessErrorMonitor warn) {
        ExchangeTrade trade = JSON.parseObject(warn.getInData(), ExchangeTrade.class);
        tradeService.processTrade(trade, ExchangeOrderDirection.SELL);
    }

    protected void redoCompletedOrder(BusinessErrorMonitor warn) {
        ExchangeOrder order = JSON.parseObject(warn.getInData(), ExchangeOrder.class);
        completedOrderService.redo(order);
    }

    protected void redoCancelOrder(BusinessErrorMonitor warn) {
        ExchangeOrder order = JSON.parseObject(warn.getInData(), ExchangeOrder.class);
        OrderUtil.checkOrderIdFormat(order.getOrderId());
        /// AssertUtil.isTrue(order.getOrderId().startsWith(ExchangeConstants.ORDER_PREFIX), ExchangeOrderMsgCode.BAD_ORDER);
        cancelOrderService.redo(order);
    }

    protected void redoCheckOrder(BusinessErrorMonitor warn) throws Exception {
        String msg = orderValidator.redo(warn.getInData());
        if (msg != null) {
            throw new Exception(msg);
        }
    }


    protected void redoSuccess(BusinessErrorMonitor warn) {
        warn.setMaintenanceTime(new Date());
        warn.setMaintenanceResult("重做成功");
        warn.setMaintenanceStatus(BooleanEnum.IS_TRUE);
        businessErrorMonitorService.updateById(warn);
    }

    protected void redoFail(BusinessErrorMonitor warn, String msg) {
        warn.setMaintenanceTime(new Date());
        warn.setMaintenanceResult(msg);
        businessErrorMonitorService.updateById(warn);
    }
}
