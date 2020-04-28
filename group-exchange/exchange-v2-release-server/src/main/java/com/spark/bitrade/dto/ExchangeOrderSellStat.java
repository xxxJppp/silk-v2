package com.spark.bitrade.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 *  
 *
 * @author young
 * @time 2019.12.16 20:02
 */
@Data
public class ExchangeOrderSellStat {
    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private BigDecimal amount;
}
