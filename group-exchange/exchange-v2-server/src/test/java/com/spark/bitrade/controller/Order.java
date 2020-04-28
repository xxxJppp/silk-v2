package com.spark.bitrade.controller;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderType;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  
 *
 * @author young
 * @time 2019.09.02 14:34
 */
@Data
public class Order {

    /**
     * 订单号，S开头的订单为星客机器人订单
     */
    private String orderId;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 交易数量
     */
    private BigDecimal amount;

    /**
     * 订单方向 0买，1卖
     */
    private ExchangeOrderDirection direction;

    /**
     * 挂单价格
     */
    private BigDecimal price;

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 挂单类型，0市价，1限价
     */
    private ExchangeOrderType type;


    /**
     * 交易币
     */
    private String coinSymbol;

    /**
     * 结算币
     */
    private String baseSymbol;

    /**
     * 买入或卖出量 对应的 冻结币数量
     */
    private BigDecimal freezeAmount;


    /**
     * 下单时间
     */
    private Long time = System.currentTimeMillis();
}
