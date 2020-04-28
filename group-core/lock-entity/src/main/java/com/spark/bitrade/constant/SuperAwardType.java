package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.07.18 14:05  
 */
@Getter
@AllArgsConstructor
public enum SuperAwardType implements BaseEnum {

    FEE_AWARD("手续费奖励"), //0
    ACTIVE_AWARD("活跃用户奖励"),//1

    ;

    private String name;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}