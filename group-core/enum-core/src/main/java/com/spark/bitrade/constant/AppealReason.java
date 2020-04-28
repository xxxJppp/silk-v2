package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
  * 申述类型
  * @author daring5920
  * @time 2018/9/7 10:32 
  */
@AllArgsConstructor
@Getter
public enum AppealReason implements BaseEnum {

    LINKED_WAITING("已经联系上卖家，等待卖家放币"),

    CONFIRM_WAITING("卖家已确认到账，等待卖家放币"),

    PAIED("买家已付款"),

    OTHER("其他");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }

}