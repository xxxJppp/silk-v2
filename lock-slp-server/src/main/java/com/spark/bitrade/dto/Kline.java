package com.spark.bitrade.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 *  k线实体
 *
 * @author young
 * @time 2019.07.09 16:40
 */
@Data
public class Kline {
    /**
     * K线日期
     */
    Long time;
    /**
     * 开盘价
     */
    private BigDecimal openPrice = BigDecimal.ZERO;
    /**
     * 最高价
     */
    private BigDecimal highestPrice = BigDecimal.ZERO;
    /**
     * 最低价
     */
    private BigDecimal lowestPrice = BigDecimal.ZERO;
    /**
     * 收盘价
     */
    private BigDecimal closePrice = BigDecimal.ZERO;
}
