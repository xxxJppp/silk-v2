package com.spark.bitrade.constant;

import com.spark.bitrade.core.BaseEnum;

/**
 * 订单交易方式
 */
public enum ExchangeOrderDirection implements BaseEnum {
    BUY, SELL;

    @Override
    public int getOrdinal() {
        return this.ordinal();
    }
}
