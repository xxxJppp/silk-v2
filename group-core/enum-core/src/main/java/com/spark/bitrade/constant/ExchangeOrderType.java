package com.spark.bitrade.constant;

import com.spark.bitrade.core.BaseEnum;

public enum ExchangeOrderType implements BaseEnum {
    MARKET_PRICE,LIMIT_PRICE;

    @Override
    public int getOrdinal() {
        return this.ordinal();
    }
}
