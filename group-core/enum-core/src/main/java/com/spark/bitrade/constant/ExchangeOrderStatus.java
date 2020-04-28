package com.spark.bitrade.constant;

import com.spark.bitrade.core.BaseEnum;

/**
 * 订单状态
 */
public enum ExchangeOrderStatus implements BaseEnum {
    TRADING, COMPLETED, CANCELED, OVERTIMED;

    @Override
    public int getOrdinal() {
        return this.ordinal();
    }
}
