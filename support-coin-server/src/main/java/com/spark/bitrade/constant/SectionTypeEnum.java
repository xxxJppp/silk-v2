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
 *  @time 2019.11.05 09:10  
 */
@AllArgsConstructor
@Getter
public enum SectionTypeEnum implements BaseEnum {
    /**
     * 0
     */
    MAIN_ZONE("主版上币"),
    /**
     * 1
     */
    INNOVATION_ZONE("创新区上币"),

    /**
     * 2
     */
    SUPPORT_UP_ZONE("扶持上币")

    ;

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
