package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.07.16 15:46  
 */
@AllArgsConstructor
@Getter
public enum SuperAuditStatus implements BaseEnum {
    //0
    PENDING("待处理"),
    //1
    APPROVED("已通过"),
    //2
    REJECTED("已驳回")

    ;

    private String name;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}