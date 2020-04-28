package com.spark.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 交易明细
 *
 * @author yangch
 * @since 2019-09-05 10:23:16
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeTradeDetail {
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 交易关联的订单号
     */
    private String refOrderId;

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 交易价格
     */
    private BigDecimal price;

    /**
     * 基币USD汇率
     */
    private BigDecimal baseUsdRate;
    /**
     * 交易数量
     */
    private BigDecimal amount;
    /**
     * 成交额
     */
    private BigDecimal turnover;
    /**
     * 手续费
     */
    private BigDecimal fee;
    /**
     * 交易手续费优惠数量
     */
    private BigDecimal feeDiscount;
    /**
     * 成交时间
     */
    private long time;
}
