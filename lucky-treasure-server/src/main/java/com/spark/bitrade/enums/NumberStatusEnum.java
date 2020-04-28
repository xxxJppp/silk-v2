package com.spark.bitrade.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@AllArgsConstructor
@Getter
public enum NumberStatusEnum implements BaseEnum {

    /**
     * 未开始 0
     */
    NOT_START("未开始"),
    /**
     * 进行中 1
     */
    HAVE_BEEN("进行中"),
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