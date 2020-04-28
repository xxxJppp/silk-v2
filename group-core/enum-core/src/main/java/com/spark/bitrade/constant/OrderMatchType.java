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
public enum OrderMatchType implements BaseEnum {

    /**
     * 待匹配
     */
    MATCHING("待匹配"),

    /**
     * 已匹配
     */
    MATCHED("已匹配"),

    MATCH_FAILED("未匹配成功"),

    CANCELED("已取消");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }

}
