package com.spark.bitrade.constant;

import lombok.AllArgsConstructor;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.20 10:48
 */
@AllArgsConstructor
public enum OperateTypeEnum {

    /**
     * 1 挖矿
     */
    MINING(1,"挖矿"),

    /**
     * 2 送出
     */
    SEND_OUT(2,"送出"),

    /**
     * 3 获得
     */
    ACQUIRE(3,"获得"),

    /**
     * 4 合成钥匙
     */
    COMPOSE(4,"合成钥匙")
    ;

    private final int code;

    private final String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
