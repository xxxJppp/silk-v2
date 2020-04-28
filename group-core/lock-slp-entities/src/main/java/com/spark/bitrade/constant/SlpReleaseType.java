package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 释放类型
 *
 * @author yangch
 * @time 2019-06-18 23:05:07
 */
@AllArgsConstructor
@Getter
public enum SlpReleaseType implements BaseEnum {
    /**
     * 0-直推奖
     */
    RELEASE_INVITE("直推奖"),
    /**
     * 1=级差奖
     */
    RELEASE_CROSS("级差奖"),
    /**
     * 2=平级奖
     */
    RELEASE_PEERS("平级奖"),
    /**
     * 3=太阳奖
     */
    RELEASE_SUN("太阳奖"),

    /**
     * 4=每日释放
     */
    RELEASE_DAILY("每日释放");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
