package com.spark.bitrade.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广告上下架状态
 * @author ss
 * @date 2017年12月23日
 */
@AllArgsConstructor
@Getter
public enum AdvertiseControlStatus {

    /**
     * 上架
     */
    PUT_ON_SHELVES(0,"上架"),
    /**
     * 下架
     */
    PUT_OFF_SHELVES(1,"下架"),
    /**
     * 已关闭（删除）
     */
    TURNOFF(2,"已关闭"),
    /**
     * 已失效（切换默认法币）
     */
    FAILURE(3,"已失效");

    private Integer code;
    private String msg;

    AdvertiseControlStatus(Integer code) {
        this.code = code;
    }

    /**
     * 通过枚举值码查找枚举值。
     *
     * @param code 查找枚举值的枚举值码。
     * @return 枚举值码对应的枚举值。
     * @throws IllegalArgumentException 如果 code 没有对应的 枚举 。
     */
    public static AdvertiseControlStatus find(Integer code) {
        for (AdvertiseControlStatus eu : values()) {
            if (eu.getCode().equals(code)) {
                return eu;
            }
        }
        return null;
    }
}
