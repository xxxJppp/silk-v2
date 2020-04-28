package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 广告类型
 *
 * @author daring5920
 * @date 2017年12月07日
 */
@AllArgsConstructor
@Getter
public enum OrderBuildType implements BaseEnum {

    /**
     * 系统创建
     */
    SYSTEM("系统创建"),

    /**
     * 用户创建
     */
    USER("用户创建");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }

}
