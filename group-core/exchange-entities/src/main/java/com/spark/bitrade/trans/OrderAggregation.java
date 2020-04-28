package com.spark.bitrade.trans;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 聚合订单最终的撮单结果
 *
 * @author yangch
 * @date 2019-09-05 15:37:28
 */
@Data
public class OrderAggregation {
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 交易数量
     */
    private BigDecimal tradedAmount;
    /**
     * 交易额
     */
    private BigDecimal tradedTurnover;
}
