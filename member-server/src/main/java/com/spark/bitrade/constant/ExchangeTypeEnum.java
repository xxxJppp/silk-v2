package com.spark.bitrade.constant;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum ExchangeTypeEnum {


    NULL(0,"NULL"),

    BUY_TAKER(1,"买入吃单(Taker)"),


    BUY_MAKER(2,"买入挂单(Maker)"),

    SELL_TAKER(3,"买入吃单(Taker)"),

    SELL_MAKER( 4,"买入挂单(Maker)")
    
    ;

    private final int code;

    private final String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
