package com.spark.bitrade.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * 广告级别
 *
 * @author ss
 */
@AllArgsConstructor
@Getter
public enum AdvertiseLevel {

    /**
     * 普通
     */
    ORDINARY(0,"普通"),

    /**
     * 优质
     */
    EXCELLENT(1,"优质");

    private Integer code;
    private String msg;

    AdvertiseLevel(Integer code){
        this.code = code;
    }

}
