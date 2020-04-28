package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

    /**
     * 奖励类型
     * @author Zhang Yanjun
     * @time 2018.12.03 17:38
     */
    @AllArgsConstructor
    @Getter
    public enum LockRewardType implements BaseEnum{
        //0
        REFERRER("直推奖"),
        //1
        CROSS("级差奖"),
        //2
        TRAINING("培养奖"),
        //3
        PREMIUMS("额外奖励")
    ;

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
