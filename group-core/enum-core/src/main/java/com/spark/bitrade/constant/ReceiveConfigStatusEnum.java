package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author daring5920
 * @date 2018年01月10日
 */
@AllArgsConstructor
@Getter
public enum ReceiveConfigStatusEnum implements BaseEnum {
    OFFLINE("离线"),
    ONLINE("上线"),
    LINING("上线中");

    @Setter
    private String nameCn;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
