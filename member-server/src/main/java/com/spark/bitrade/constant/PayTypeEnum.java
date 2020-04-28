package com.spark.bitrade.constant;

import lombok.AllArgsConstructor;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.20 10:48
 */
@AllArgsConstructor
public enum PayTypeEnum {

    /**
     * 购买
     */
    BUY(10,"购买"),

    /**
     * 锁仓
     */
    LOCK(20,"锁仓"),

    /**
     * 社区人数
     */
    COMMUNITY_SIZE(30, "社区人数"),

    /**
     * 年终活动开奖赠送
     */
    NEW_YEAR_GIVE(40, "免费赠送")
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
