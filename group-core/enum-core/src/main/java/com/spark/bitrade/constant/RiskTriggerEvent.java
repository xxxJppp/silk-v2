package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 对应后台风控事件类型
 *
 * @author archx
 * @since 2019/5/30 15:49
 */
@AllArgsConstructor
@Getter
public enum RiskTriggerEvent implements BaseEnum {

    OTC_APPEAL_FAILED("OTC", "C2C订单申诉失败"), // 0

    OTC_CANCEL_ORDER("OTC", "撤销C2C订单"), // 1

    NONE("NONE", "无操作");

    @Setter
    private String cnType;  //类型
    @Setter
    private String cnName;  //中文描述

    @Override
    @JsonValue
    public int getOrdinal() {
        return ordinal();
    }
}
