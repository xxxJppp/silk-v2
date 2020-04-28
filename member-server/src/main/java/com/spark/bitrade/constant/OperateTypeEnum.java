package com.spark.bitrade.constant;

import lombok.AllArgsConstructor;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.20 10:48
 */
@AllArgsConstructor
public enum OperateTypeEnum {

    /**
     * 开通
     */
    OPENING(30,"开通"),

    /**
     * 升级
     */
    UPGRADE(20,"升级"),

    /**
     * 续费
     */
    RENEW(10,"续费"),

    /**
     * 开通
     */
    OPENING_EN(40,"open"),

    /**
     * 升级
     */
    UPGRADE_EN(50,"upgrade"),

    /**
     * 续费
     */
    RENEW_EN(60,"renew"),

    /**
     * 开通
     */
    OPENING_KO(70,"개통"),

    /**
     * 升级
     */
    UPGRADE_KO(80,"업그레이드"),

    /**
     * 续费
     */
    RENEW_KO(90,"속비"),

    /**
     * 开通
     */
    OPENING_HK(100,"開通"),

    /**
     * 升级
     */
    UPGRADE_HK(110,"陞級"),

    /**
     * 续费
     */
    RENEW_HK(120,"續費");

    private final int code;

    private final String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
