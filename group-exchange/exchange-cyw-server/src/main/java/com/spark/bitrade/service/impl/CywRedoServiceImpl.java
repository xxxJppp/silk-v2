package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constants.ExchangeCywMsgCode;
import com.spark.bitrade.entity.BusinessErrorMonitor;
import com.spark.bitrade.entity.ExchangeCywOrder;
import com.spark.bitrade.entity.ExchangeTrade;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 *  
 *
 * @author young
 * @time 2019.09.30 11:11
 */
@Slf4j
@Service
public class CywRedoServiceImpl implements CywRedoService {

    @Autowired
    private BusinessErrorMonitorService businessErrorMonitorService;
    @Autowired
    private CywTradeService tradeService;
    @Autowired
    private CywCompletedOrderService completedOrderService;
    @Autowired
    private CywCancelOrderService cancelOrderService;
    @Autowired
    private CywOrderValidator orderValidator;

    @Override
    public boolean redo(long id) {
        BusinessErrorMonitor warn = businessErrorMonitorService.getById(id);
        if (warn == null || warn.getMaintenanceStatus() == BooleanEnum.IS_TRUE) {
            return true;
        }

        try {
            switch (warn.getType()) {
                case EXCHANGE__CYW_TRADE_BUY:
                    this.redoTradeBuy(warn);
                    break;
                case EXCHANGE__CYW_TRADE_SELL:
                    this.redoTradeSell(warn);
                    break;
                case EXCHANGE__CYW_COMPLETED:
                    this.redoCompletedOrder(warn);
                    break;
                case EXCHANGE__CYW_CANCEL_FAIL:
                    this.redoCancelOrder(warn);
                    break;
                case EXCHANGE__CYW_CHECK_FAIL:
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

    private void redoTradeBuy(BusinessErrorMonitor warn) {
        ExchangeTrade trade = JSON.parseObject(warn.getInData(), ExchangeTrade.class);
        tradeService.processTrade(trade, ExchangeOrderDirection.BUY);
    }

    private void redoTradeSell(BusinessErrorMonitor warn) {
        ExchangeTrade trade = JSON.parseObject(warn.getInData(), ExchangeTrade.class);
        tradeService.processTrade(trade, ExchangeOrderDirection.SELL);
    }

    private void redoCompletedOrder(BusinessErrorMonitor warn) {
        ExchangeCywOrder order = JSON.parseObject(warn.getInData(), ExchangeCywOrder.class);
        completedOrderService.redo(order.getOrderId());
    }

    private void redoCancelOrder(BusinessErrorMonitor warn) {
        ExchangeCywOrder order = JSON.parseObject(warn.getInData(), ExchangeCywOrder.class);
        AssertUtil.isTrue(order.getOrderId().startsWith("S"), ExchangeCywMsgCode.BAD_CYW_ORDER);
        cancelOrderService.redo(order.getOrderId());
    }

    private void redoCheckOrder(BusinessErrorMonitor warn) throws Exception {
        String msg = orderValidator.redo(warn.getInData());
        if (msg != null) {
            throw new Exception(msg);
        }
    }


    private void redoSuccess(BusinessErrorMonitor warn) {
        warn.setMaintenanceTime(new Date());
        warn.setMaintenanceResult("重做成功");
        warn.setMaintenanceStatus(BooleanEnum.IS_TRUE);
        businessErrorMonitorService.updateById(warn);
    }

    private void redoFail(BusinessErrorMonitor warn, String msg) {
        warn.setMaintenanceTime(new Date());
        warn.setMaintenanceResult(msg);
        businessErrorMonitorService.updateById(warn);
    }
}
