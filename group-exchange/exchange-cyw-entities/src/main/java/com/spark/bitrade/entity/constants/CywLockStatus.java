package com.spark.bitrade.entity.constants;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 锁定状态
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/2 11:34
 */
@AllArgsConstructor
@Getter
public enum CywLockStatus implements BaseEnum {

    //0
    UNLOCK("未锁定"),
    //1
    LOCKED("已锁定");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
