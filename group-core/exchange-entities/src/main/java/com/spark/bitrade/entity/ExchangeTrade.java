package com.spark.bitrade.entity;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 撮合交易信息
 *
 * @author yangch
 * @since 2019-09-03 13:44:40
 */
@Data
public class ExchangeTrade {
    /**
     * 交易对
     */
    private String symbol;

    /**
     * 成交价
     */
    private BigDecimal price;

    /**
     * 成交数量
     */
    private BigDecimal amount;

    /**
     * 基币USD汇率
     */
    private BigDecimal baseUsdRate;

    /**
     * 订单方向
     */
    private ExchangeOrderDirection direction;

    /**
     * 卖单用户ID
     */
    private Long buyMemberId;

    /**
     * 买单订单号
     */
    private String buyOrderId;

    /**
     * 买单成交额
     */
    private BigDecimal buyTurnover;


    /**
     * 卖单用户ID
     */
    private Long sellMemberId;

    /**
     * 卖单订单号
     */
    private String sellOrderId;

    /**
     * 卖单成交额
     */
    private BigDecimal sellTurnover;


    /**
     * 未完成订单号
     */
    private String unfinishedOrderId;
    /**
     * 未完成订单的交易数量
     */
    private BigDecimal unfinishedTradedAmount;
    /**
     * 未完成订单的成交额
     */
    private BigDecimal unfinishedTradedTurnover;

    private Long time;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
