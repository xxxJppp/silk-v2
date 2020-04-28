package com.spark.bitrade.constant;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@AllArgsConstructor
@Getter
public enum  BooleanStringEnum implements BaseEnum, IEnum {
    IS_FALSE(false,"0", "否"),
    IS_TRUE(true, "1","是");

    @Setter
    private boolean is;

    @Setter
    private String val;

    @Setter
    private String nameCn;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }

    @Override
    public Serializable getValue() {
        return this.val;
    }
}