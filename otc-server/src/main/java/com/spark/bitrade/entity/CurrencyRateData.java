package com.spark.bitrade.entity;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CurrencyRateData {
//    //法币币种缩写
//    private String coin;
    //交易币种的简称 
    private String symbol;
    //最新价格
    private BigDecimal price;

    //更新时间(10位unix时间戳)
    private Long lastUpdated;


}
