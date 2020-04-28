package com.spark.bitrade.service;

import com.spark.bitrade.entity.ExchangeCywOrder;
import com.spark.bitrade.entity.ExchangeOrder;

import java.math.BigDecimal;

/**
 * 撤单服务
 *
 * @author young
 * @time 2019.09.19 14:16
 */
public interface CywCancelOrderService {

    /**
     * 撤销订单申请
     *
     * @param memberId 用户ID
     * @param orderId  订单号，格式=S雪花流水ID_交易对
     * @return
     */
    ExchangeOrder claimCancelOrder(Long memberId, String orderId);

    /**
     * 限制同一订单60秒内只能撤单请求的一次
     *
     * @param orderId
     * @return true=成功，false=失败
     */
    boolean isCancelOrderRequestLimit(String orderId);

    /**
     * 撤销订单（撮合器中存在的订单）
     * 备注：仅更改订单状态
     *
     * @param memberId     用户ID
     * @param orderId      订单号
     * @param tradedAmount 交易数量，可以为0
     * @param turnover     交易额，可以为0
     * @return
     */
    ExchangeOrder canceledOrder(Long memberId, String orderId, BigDecimal tradedAmount, BigDecimal turnover);

    /**
     * 撤销订单（撮合器中不存在的订单）
     * 备注：仅更改订单状态
     *
     * @param memberId 用户ID
     * @param orderId  订单号
     * @return
     */
    ExchangeOrder canceledOrder(Long memberId, String orderId);

    /**
     * 入库撤销订单
     *
     * @param orderId 订单号
     */
    void canceledOrder(String orderId);

    /**
     * 重做接口
     *
     * @param orderId
     */
    void redo(String orderId);

    /**
     * 从Redis中获取撤销的订单
     *
     * @param orderId
     * @return
     */
    ExchangeCywOrder queryCanceledOrderFromRedis(String orderId);

}
