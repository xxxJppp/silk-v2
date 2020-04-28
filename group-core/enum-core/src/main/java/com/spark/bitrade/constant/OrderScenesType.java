package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 订单场景类型
 *
 * @author daring5920
 * @date 2017年12月07日
 */
@AllArgsConstructor
@Getter
public enum OrderScenesType implements BaseEnum {

    /**
     * 线上
     */
    ONLINE("线上订单"),

    /**
     * 场外
     */
    OTC("场外订单");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }

}
