package com.spark.bitrade.entity;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CoinRateData {

    //法币币种与兑换币种:CNY_USD
    private String pair;

    //中文名称
    private String name;

    //法币符号
    private String symbol;

    //法币对USD的价格
    private BigDecimal price_usd;

    //更新时间
    private Long lastUpdated;

}
