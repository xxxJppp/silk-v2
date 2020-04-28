package com.spark.bitrade.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@AllArgsConstructor
@Getter
public enum BullStatusEnum implements BaseEnum {

    /**
     * 未开始 0
     */
    NOT_START("未开始"),
    /**
     * 选牛中 1
     */
    CHOOSING_BULL("选牛中"),
    /**
     * 赛牛中 2
     */
    IN_THE_GAME("赛牛中"),
    /**
     * 已结束 3
     */
    END("已结束")

    ;

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }

}