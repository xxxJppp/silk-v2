package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 对应后台限制事件类型
 *
 * @author archx
 * @since 2019/5/30 15:49
 */
@AllArgsConstructor
@Getter
public enum RiskLimitEvent implements BaseEnum {

    FORBID_EXCHANGE("EXCHANGE", "禁止币币交易"), // 0

    FORBID_ADVERTISE("OTC", "禁止发布广告"), // 1

    FORBID_COIN("COIN", "禁止充值提币"), // 2

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
