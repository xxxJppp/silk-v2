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
 *  @time 2019.11.05 08:54  
 */
@AllArgsConstructor
@Getter
public enum AuditStatusEnum implements BaseEnum {

    PENDING("待审核"),

    APPROVED("审核通过"),

    REJECT("审核拒绝")
    ;

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}