package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/***
  * 状态
  * @author yangch
  * @time 2018.08.02 11:53
  */

@AllArgsConstructor
@Getter
public enum SlpStatus implements BaseEnum {

    //0
    NOT_PROCESSED("进行中"),
    //1
    PROCESSED("已完成"),
    //2
    APPENDING("加仓中"),
    //3
    APPEND_PROCESSED("加仓完成");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
