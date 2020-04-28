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
 *  @time 2019.08.30 10:18  
 */
@Getter
@AllArgsConstructor
public enum IncubatorsOpType implements BaseEnum {
    /**
     * 上币申请 0
     */
    UP_COIN_APPLY("上币申请"),
    /**
     * 退出上币 1
     */
    EXIT_COIN_APPLY("退出上币")
    ;

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
