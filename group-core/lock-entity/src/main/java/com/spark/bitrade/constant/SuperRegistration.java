package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.08.29 16:43  
 */
@Getter
@AllArgsConstructor
public enum  SuperRegistration implements BaseEnum {
    /**
     *主动注册 0
     */
    INITIATIVE("主动注册"),
    /**
     * 孵化区项目合作方注册 1
     */
    INCUBATORS("孵化区项目合作方注册"),

    ;

    private String name;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}