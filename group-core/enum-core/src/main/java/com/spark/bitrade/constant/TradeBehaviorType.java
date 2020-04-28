package com.spark.bitrade.constant;

import com.spark.bitrade.core.BaseEnum;

/**
 * 撮合交易类型
 */
public enum TradeBehaviorType implements BaseEnum {
    /**
     * 0 = null
     */
    NULL,
    /**
     * 1=买入吃单(BUY_TAKER)
     */
    BUY_TAKER,

    /**
     * 2=买入挂单(BUY_MAKER)
     */
    BUY_MAKER,
    /**
     * 3=卖出吃单(SELL_TAKER)
     */
    SELL_TAKER,

    /**
     * 4=卖出挂单(SELL_MAKER)
     */
    SELL_MAKER;

    @Override
    public int getOrdinal() {
        return this.ordinal();
    }
}
