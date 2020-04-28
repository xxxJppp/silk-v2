package com.spark.bitrade.entity.constants;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * CywOrderValidateStatus
 *
 * @author Archx[archx@foxmail.com]
 * at 2019-09-30 11:00
 */
@AllArgsConstructor
@Getter
public enum CywOrderValidateStatus implements BaseEnum {

    //0
    NONE("未验证"),
    //1
    SUCCESS("验证通过"),
    //2
    FAILED("验证失败");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
