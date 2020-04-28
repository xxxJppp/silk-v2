package com.spark.bitrade.service;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.util.MessageRespResult;

import java.math.BigDecimal;

/**
 *  下单服务
 *
 * @author young
 * @time 2019.11.11 17:57
 */
public interface ExchangePlaceOrderService {

    /**
     * 委托订单
     *
     * @param memberId     会员ID
     * @param direction    交易方式：买币、卖币
     * @param symbol       交易对
     * @param price        委托价格
     * @param amount       委托数量
     * @param type         订单类型：市价、限价
     * @param tradeCaptcha 交易验证码
     * @return
     */
    MessageRespResult<ExchangeOrder> place(Long memberId,
                                           ExchangeOrderDirection direction,
                                           String symbol,
                                           BigDecimal price,
                                           BigDecimal amount,
                                           ExchangeOrderType type,
                                           String tradeCaptcha);
}
