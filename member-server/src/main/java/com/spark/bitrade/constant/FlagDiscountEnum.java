package com.spark.bitrade.constant;

import lombok.AllArgsConstructor;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.20 16:11
 */
@AllArgsConstructor
public enum FlagDiscountEnum {

    /**
     * 开
     */
    OPENING(1,"开"),

    /**
     * 关
     */
    CLOSE(0,"关");

    private final int code;

    private final String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
