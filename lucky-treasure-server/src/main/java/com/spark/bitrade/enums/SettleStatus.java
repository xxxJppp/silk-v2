package com.spark.bitrade.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public enum SettleStatus implements BaseEnum {

    /**
     *   0 未中奖   , 1:已退款 2:退款失败 3:已发放奖金 4:奖金发放失败
     */
    NOT_SETTLE("未中奖"),
    /**
     * 已退款 1
     */
    HAS_RETURED("已退款"),
    /**
     * 退款失败 2
     */
    RETURN_FAILED("退款失败"),
    /**
     * 已发放奖金 3
     */
    HAS_SEND("已发放奖金"),
    /**
     * 奖金发放失败 4
     */
    SEND_FAILED("奖金发放失败"),

    ;

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
