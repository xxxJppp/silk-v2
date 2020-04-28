package com.spark.bitrade.constant;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 释放任务状态  
 *
 * @author young
 * @time 2019.12.16 15:08
 */
@AllArgsConstructor
@Getter
public enum ReleaseTaskStatus implements BaseEnum, IEnum {
    /**
     * 0=未释放
     */
    UNRELEASE("未释放"),
    /**
     * 1=已释放
     */
    RELEASED("已释放");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
