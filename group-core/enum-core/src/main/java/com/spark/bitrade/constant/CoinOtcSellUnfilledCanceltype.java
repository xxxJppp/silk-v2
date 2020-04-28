package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 退还原因，0 长时间未成交手动取消，1 部分成交
 *
 * @author daring5920
 * @date 2017年12月07日
 */
@AllArgsConstructor
@Getter
public enum CoinOtcSellUnfilledCanceltype implements BaseEnum {

    /**
     * 长时间未成交手动取消
     */
    TIME_OUT_CANCEL("长时间未成交手动取消"),

    /**
     * 部分成交
     */
    SECTION_DEAL("部分成交");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }

}
