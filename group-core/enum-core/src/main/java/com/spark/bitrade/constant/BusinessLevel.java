package com.spark.bitrade.constant;

import com.spark.bitrade.core.BaseEnum;

import java.util.Arrays;
import java.util.Optional;

/**
 * 商家等级
 *
 * @author wsy
 * @date 2019/5/20 15:14
 */
public enum BusinessLevel implements BaseEnum {
    LEVEL_NONE(0, "未知等级"),
    LEVEL_ONE(1, "一级商家"),
    LEVEL_TWO(2, "二级商家"),
    LEVEL_THREE(3, "三级商家"),
    LEVEL_FOUR(4, "四级商家"),
    LEVEL_FIVE(5, "五级商家");

    private long level;
    private String name;

    BusinessLevel(long level, String name) {
        this.level = level;
        this.name = name;
    }

    public long getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public static BusinessLevel idToLevel(long id) {
        Optional<BusinessLevel> optional = Arrays.stream(BusinessLevel.values()).filter(i -> i.level == id).findFirst();
        return optional.orElse(BusinessLevel.LEVEL_NONE);
    }

    @Override
    public int getOrdinal() {
        return (int) level;
    }
}
