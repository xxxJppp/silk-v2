package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  
 *     权益状态
 *  @author liaoqinghui  
 *  @time 2019.07.16 14:28  
 */
@Getter
@AllArgsConstructor
public enum  EquityStatus implements BaseEnum {

    NORMAL("正常"), //0
    FORBIDDEN_EQUITY("禁用权益"),//1

    ;

    private String name;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
