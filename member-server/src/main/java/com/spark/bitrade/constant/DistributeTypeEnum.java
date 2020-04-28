package com.spark.bitrade.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DistributeTypeEnum {


    WAIT_DISTRIBUTE(10,"未分配"),

  
    DISTRIBUTED(20,"已分配");

    private final int code;

    private final String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
