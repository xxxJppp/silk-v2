package com.spark.bitrade.emums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wsy
 * @since 2019/8/16 9:45
 */
@AllArgsConstructor
@Getter
public enum MqttAnswerType implements BaseEnum {

    PAY_SUCCESS("支付成功应答");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }

}
