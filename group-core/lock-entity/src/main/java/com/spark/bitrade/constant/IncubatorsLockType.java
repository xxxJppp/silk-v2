package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.08.30 10:27  
 */
@Getter
@AllArgsConstructor
public enum  IncubatorsLockType implements BaseEnum {
    /**
     * 锁仓 0
     */
    LOCK("锁仓"),
    /**
     * 解仓 1
     */
    UN_LOCK("解仓")
    ;

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}