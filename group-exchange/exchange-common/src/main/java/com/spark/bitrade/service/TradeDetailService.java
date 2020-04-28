package com.spark.bitrade.service;

import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeOrderDetail;
import com.spark.bitrade.trans.OrderAggregation;
import com.spark.bitrade.trans.TradeSettleDelta;

/**
 *  交易明细服务接口
 *
 * @author young
 * @time 2019.09.05 10:15
 */
public interface TradeDetailService {

    /**
     * 交易明细是否已存在
     *
     * @param orderId    交易订单号
     * @param refOrderId 交易关联订单号
     * @return
     */
    boolean existsByOrderIdAndRefOrderId(String orderId, String refOrderId);

    /**
     * 保存交易明细
     *
     * @param entity
     * @return
     */
    ExchangeOrderDetail save(ExchangeOrderDetail entity);

    /**
     * 删除交易明细
     *
     * @param orderId    交易订单号
     * @param refOrderId 交易关联订单号
     * @return
     */
    int deleteByOrderIdAndRefOrderId(String orderId, String refOrderId);

    /**
     * 获取指定订单号的交易数量和成交额
     *
     * @param exchangeOrder 币币交易订单
     * @return
     */
    OrderAggregation aggregation(ExchangeOrder exchangeOrder);

    /**
     * 保存交易明细
     *
     * @param delta
     * @return
     */
    ExchangeOrderDetail saveTradeDetail(TradeSettleDelta delta);

    /**
     * 删除交易明细
     * @param delta
     */
    void deleteTradeDetail(TradeSettleDelta delta);
}
