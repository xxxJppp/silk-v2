package com.spark.bitrade.constant;

import com.spark.bitrade.core.BaseEnum;

/**
 * 币种区域
 */
public enum ExchangeCoinDisplayArea implements BaseEnum {
    /**
     * 0=主区
     */
    MASTER,
    /**
     * 1=创新区
     */
    INNOVATIVE;

    @Override
    public int getOrdinal() {
        return this.ordinal();
    }
}
