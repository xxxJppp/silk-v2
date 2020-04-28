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
 *  @time 2019.08.30 10:28  
 */
@Getter
@AllArgsConstructor
public enum IncubatorsDetailStatus implements BaseEnum {
    /**
     * 0-初始化锁仓
     */
    INIT_LOCK("初始化锁仓"),
    /**
     * 1-发起加仓
     */
    ADD_LOCK("发起加仓"),
    /**
     * 2-失效
     */
    IN_VALID("失效"),
    /**
     * 3-生效
     */
    ENABLED("生效"),
    ;

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}