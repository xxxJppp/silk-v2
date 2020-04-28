package com.spark.bitrade.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 广告类型
 *
 * @author ss
 */
@AllArgsConstructor
@Getter
public enum AdvertiseType{

    /**
     * 购买
     */
    BUY(0,"购买"),

    /**
     * 出售
     */
    SELL(1,"出售");

    private Integer code;
    private String msg;

    /**
     * 通过枚举值码查找枚举值。
     *
     * @param code 查找枚举值的枚举值码。
     * @return 枚举值码对应的枚举值。
     * @throws IllegalArgumentException 如果 code 没有对应的 枚举 。
     */
    public static AdvertiseType find(Integer code) {
        for (AdvertiseType eu : values()) {
            if (eu.getCode().equals(code)) {
                return eu;
            }
        }
        return null;
    }

}
