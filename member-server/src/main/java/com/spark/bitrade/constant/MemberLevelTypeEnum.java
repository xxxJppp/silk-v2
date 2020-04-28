package com.spark.bitrade.constant;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum MemberLevelTypeEnum {


    NORMAL(1,"普通会员"),


    VIP1(2,"VIP1"),


    VIP2(3,"VIP2"),
    
    
    VIP3(4,"VIP3"),
    
    AGENT(5,"经纪人")
    
    ;

    private final int code;

    private final String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
