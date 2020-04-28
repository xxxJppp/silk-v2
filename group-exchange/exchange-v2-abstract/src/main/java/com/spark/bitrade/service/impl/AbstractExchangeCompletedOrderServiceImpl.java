package com.spark.bitrade.service.impl;

import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.constants.ExchangeOrderMsgCode;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.constants.BusinessErrorMonitorType;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.service.*;
import com.spark.bitrade.trans.OrderSettleDelta;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 *  
 *
 * @author young
 * @time 2019.09.19 19:00
 */
@Slf4j
public abstract class AbstractExchangeCompletedOrderServiceImpl implements ExchangeCompletedOrderService {
    @Autowired
    protected ExchangeOrderService exchangeOrderService;
    @Autowired
    protected ExchangeWalletProvider exchangeWalletProvider;
    @Autowired
    protected BusinessErrorMonitorService businessErrorMonitorService;
    @Autowired
    protected ExchangeCheckOrderService checkOrderService;
    @Autowired
    protected PushMessage pushMessage;

    @Override
    public ExchangeOrder completedOrder(Long memberId, String orderId, BigDecimal tradedAmount, BigDecimal turnover) {
        ExchangeOrder order = exchangeOrderService.queryOrderWithMaster(memberId, orderId);
        if (order == null) {
            log.info("交易队列中未获取到订单。orderId={}", orderId);
            return null;
        }

        // 更新订单的成交数量、成交额、订单状态、完成时间等
        order.setTradedAmount(tradedAmount);
        order.setTurnover(turnover);
        order.setStatus(ExchangeOrderStatus.COMPLETED);
        order.setCompletedTime(System.currentTimeMillis());

        this.confidentCompletedOrder(order);
        return order;
    }

    @Override
    public void redo(ExchangeOrder order) {
        log.info("redo >>> 执行已完成的订单入库任务，orderId={}", order.getOrderId());
        if (order != null) {
            this.getService().completedOrder(order);
        } else {
            log.info("redo >>> 订单为null");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ExchangeOrder completedOrder(ExchangeOrder order) {
        // 保存订单，成交订单入库保存
        AssertUtil.isTrue(exchangeOrderService.updateByOrderIdAndStatus(order, ExchangeOrderStatus.TRADING),
                ExchangeOrderMsgCode.UPDATE_COMPLETED_ORDER_FAILED);

        // 退回多余冻结的余额，记录wal日志
        OrderSettleDelta orderSettleDelta = OrderSettleDelta.settle(order);
        log.info(">>> 订单完成结算 :{}", orderSettleDelta);
        if (BigDecimalUtil.gt0(orderSettleDelta.getReturnAmount())) {
            log.info(">>> 退还多冻结的余额,memberId={}, orderId={}, returnAmount={}",
                    orderSettleDelta.getMemberId(), orderSettleDelta.getOrderId(), orderSettleDelta.getReturnAmount());
            exchangeWalletProvider.giveBackFrozen(orderSettleDelta, WalTradeType.DEAL);
        }

        // 添加校验任务
        checkOrderService.addCheckTask(order.getOrderId());

        return order;
    }

    /**
     * 处理完成的订单，处理失败则记录错误
     *
     * @param order
     */
    protected void confidentCompletedOrder(ExchangeOrder order) {
        try {
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

    public AbstractExchangeCompletedOrderServiceImpl getService() {
        return SpringContextUtil.getBean(AbstractExchangeCompletedOrderServiceImpl.class);
    }

}
