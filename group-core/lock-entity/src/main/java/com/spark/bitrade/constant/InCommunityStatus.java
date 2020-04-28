package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.07.16 14:21  
 */
@AllArgsConstructor
@Getter
public enum InCommunityStatus implements BaseEnum {

    IN_COMMUNITY("在社区中"), //0
    EXIT_COMMUNITY("已退出社区")

    ;

    private String name;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
