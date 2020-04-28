package com.spark.bitrade.entity.constants;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 处理状态
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/2 11:33
 */
@AllArgsConstructor
@Getter
public enum CywProcessStatus implements BaseEnum {

    //0
    NOT_PROCESSED("未处理"),
    //1
    PROCESSED("已处理");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}