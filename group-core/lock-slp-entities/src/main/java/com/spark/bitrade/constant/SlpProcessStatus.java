package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/***
  * 处理状态
  * @author yangch
  * @time 2018.08.02 11:53
  */

@AllArgsConstructor
@Getter
public enum SlpProcessStatus implements BaseEnum {

    //0
    NOT_PROCESSED("未处理"),
    //1
    PROCESSED("已处理");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
