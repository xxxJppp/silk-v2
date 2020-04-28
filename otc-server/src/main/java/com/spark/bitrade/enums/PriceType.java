package com.spark.bitrade.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 价格类型
 * @author ss
 * @date 2017年12月23日
 */
@AllArgsConstructor
@Getter
public enum PriceType {

    /**
     * 固定的
     */
    REGULAR(0,"固定的"),

    /**
     * 变化的
     */
    MUTATIVE(1,"变化的");

    private Integer code;
    private String msg;

    PriceType(Integer code){
        this.code = code;
    }
}
