package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.07.16 14:24  
 */
@Getter
@AllArgsConstructor
public enum CommunityApplyType implements BaseEnum {

    APPLYING_SUPER_PARTNER("申请成为合伙人"), //0
    EXITING_APPLY_SUPER_PARTNER("申请退出合伙人"),//1
    EXIT_COMMUNITY("退出社区"),//2
    JOIN_COMMUNITY("加入社区"),//3

    ;

    private String name;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}