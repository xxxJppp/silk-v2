package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wsy
 * @since 2019/8/1 17:37
 */
@AllArgsConstructor
@Getter
public enum MqttPayStateType implements BaseEnum {

    accept("接受成功"),
    success("支付成功"),
    failure("支付失败");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return ordinal();
    }
}
