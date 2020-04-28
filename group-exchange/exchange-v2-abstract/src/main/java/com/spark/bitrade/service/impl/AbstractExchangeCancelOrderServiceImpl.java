package com.spark.bitrade.service.impl;

import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.constants.ExchangeOrderMsgCode;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.constants.BusinessErrorMonitorType;
import com.spark.bitrade.entity.constants.ExchangeRedisKeys;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.redis.PalceService;
import com.spark.bitrade.service.*;
import com.spark.bitrade.service.optfor.RedisKeyService;
import com.spark.bitrade.trans.OrderAggregation;
import com.spark.bitrade.trans.OrderSettleDelta;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


/**
 *  撤单服务
 *
 * @author young
 * @time 2019.09.19 14:17
 */
@Slf4j
public abstract class AbstractExchangeCancelOrderServiceImpl implements ExchangeCancelOrderService {
    @Autowired
    protected ExchangeOrderService exchangeOrderService;
    @Autowired
    protected ExchangeWalletProvider exchangeWalletProvider;
    @Autowired
    protected TradeDetailService tradeDetailService;
    @Autowired
    protected PalceService palceService;
    @Autowired
    protected RedisKeyService redisKeyService;
    @Autowired
    protected PushMessage pushMessage;
    @Autowired
    protected BusinessErrorMonitorService businessErrorMonitorService;

    @Autowired
    protected ExchangeCheckOrderService checkOrderService;

    @Override
    public ExchangeOrder claimCancelOrder(Long memberId, String orderId) {
        ExchangeOrder order = this.queryTradingOrder(memberId, orderId);
        if (order != null && order.getStatus() == ExchangeOrderStatus.TRADING) {
            //提交撤单消息
            pushMessage.pushOrderCancelApply(order);
        } else {
            log.info("不是交易中的订单，不能撤销。订单={}", order);
        }

        return order;
    }

    @Override
    public ExchangeOrder canceledOrder(Long memberId, String orderId, BigDecimal tradedAmount, BigDecimal turnover) {
        // 从缓存中获取订单
        ExchangeOrder order = this.queryTradingOrder(memberId, orderId);
        if (order == null) {
            log.info("交易队列中未获取到订单。orderId={}", orderId);
            return null;
        }

        //更新订单的成交数量、成交额、订单状态、完成时间等
        order.setTradedAmount(tradedAmount);
        order.setTurnover(turnover);
        order.setStatus(ExchangeOrderStatus.CANCELED);
        order.setCanceledTime(System.currentTimeMillis());

        this.confidentCanceledOrder(order);
        return order;
    }

    @Override
    public ExchangeOrder canceledOrder(Long memberId, String orderId) {
        log.warn("撤销的订单不在撮合器中。memberId={}, orderId={}", memberId, orderId);

        // 从缓存中获取订单
        ExchangeOrder order = this.queryTradingOrder(memberId, orderId);
        if (order == null) {
            log.info("交易队列中未获取到订单。orderId={}", orderId);
            return null;
        }

        // 需要确保成交明细都已经保存，才能撤销订单
        if (this.hasMarkOrderTrading(orderId)) {
            log.warn("成交明细未处理完，撤销订单失败！订单号={}", orderId);
            return null;
        }

        OrderAggregation aggregation = tradeDetailService.aggregation(order);
        log.info(">>> 订单聚合数据 :{}", aggregation);

        // 更新订单的成交数量、成交额、订单状态、完成时间等
        order.setTradedAmount(aggregation.getTradedAmount());
        order.setTurnover(aggregation.getTradedTurnover());
        order.setStatus(ExchangeOrderStatus.CANCELED);
        order.setCanceledTime(System.currentTimeMillis());

        this.confidentCanceledOrder(order);

        return order;
    }

    /**
     * 限制同一订单60秒内只能撤单请求的一次
     *
     * @param orderId
     * @return true=限制访问，false=可以访问
     */
    @Override
    public boolean isCancelOrderRequestLimit(String orderId) {
        return !palceService.place(
                new StringBuilder("lock:req:cancelOrder:").append(orderId).toString(), 60);
    }

    @Override
    public void redo(ExchangeOrder order) {
        log.info("redo >>> 执行撤单任务，orderId={}", order.getOrderId());
        if (order != null) {
            getService().canceledOrder(order);
        } else {
            log.info("redo >>> 订单为null");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void canceledOrder(ExchangeOrder order) {
        WalTradeType tradeType = WalTradeType.CANCEL_ORDER;

        // 根据是否有成交额或成交量 修改流水类型
        if (BigDecimalUtil.gt0(order.getTradedAmount()) || BigDecimalUtil.gt0(order.getTurnover())) {
            tradeType = WalTradeType.PART_CANCEL_ORDER;
        }

        // 修改订单状态、成交额、成交量
        AssertUtil.isTrue(exchangeOrderService.updateByOrderIdAndStatus(order, ExchangeOrderStatus.TRADING),
                ExchangeOrderMsgCode.UPDATE_CANCELED_ORDER_FAILED);

        // 退回多余冻结的余额，记录wal日志
        OrderSettleDelta orderSettleDelta = OrderSettleDelta.settle(order);
        log.info(">>> 订单撤单结算 :{}", orderSettleDelta);
        if (BigDecimalUtil.gt0(orderSettleDelta.getReturnAmount())) {
            log.info(">>> 退还冻结余额,memberId={}, orderId={}, returnAmount={}",
                    orderSettleDelta.getMemberId(), orderSettleDelta.getOrderId(), orderSettleDelta.getReturnAmount());
            exchangeWalletProvider.giveBackFrozen(orderSettleDelta, tradeType);
        }

        // 添加校验任务
        checkOrderService.addCheckTask(order.getOrderId());
    }


    public AbstractExchangeCancelOrderServiceImpl getService() {
        return SpringContextUtil.getBean(AbstractExchangeCancelOrderServiceImpl.class);
    }

    /**
     * 处理撤销的订单，处理失败则记录错误
     *
     * @param order
     */
    protected void confidentCanceledOrder(ExchangeOrder order) {
        try {
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

    protected ExchangeOrder queryTradingOrder(Long memberId, String orderId) {
        return exchangeOrderService.queryOrderWithMaster(memberId, orderId);
    }

    /**
     * 检查订单是否正在交易
     *
     * @param orderId
     * @return true=订单正在交易/false=订单未交易
     */
    protected boolean hasMarkOrderTrading(String orderId) {
        return redisKeyService.hasKey(ExchangeRedisKeys.getOrderTradingKey(orderId));
    }
}
