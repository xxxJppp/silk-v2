package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *  TCC机制状态
 *  @author yangch
 *  @time 2019-07-11 16:24:28
 *  
 */
@AllArgsConstructor
@Getter
public enum TccStatus implements BaseEnum {
    /**
     * 0=无操作
     */
    NONE("无操作"),
    /**
     * 1=预留业务资源
     */
    TRY("预留业务资源"),

    /**
     * 2=确认执行业务操作
     */
    CONFIRM("确认执行业务操作"),

    /**
     * 取消执行业务操作
     */
    CANCEL("取消执行业务操作");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }

}
