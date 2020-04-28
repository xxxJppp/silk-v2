package com.spark.bitrade.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatisExchangeOrderDto {

    private Long memberId;

    private String baseSymbol;

    private Integer tradeCount;

    private BigDecimal tradeTurnover;

}
