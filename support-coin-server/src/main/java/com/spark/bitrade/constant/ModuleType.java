package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.05 16:29  
 */
@AllArgsConstructor
@Getter
public enum ModuleType implements BaseEnum {
    /**
     * 模块名称{0:引流交易码管理,1:交易对管理,2:转版管理}
     */
    DRAINAGE_MANAGE("引流交易码管理"),

    EXCHANGE_MANAGE("交易对管理"),

    CHANGE_SECTION_MANAGE("转版管理"),

    OPEN_RED_PACK("红包开通"),

    APPLY_RED_PACK("红包申请"),

    PRIORITY_APPLY("红包优先级申请"),
    ;

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
