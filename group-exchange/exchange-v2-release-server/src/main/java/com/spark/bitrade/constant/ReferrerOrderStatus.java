package com.spark.bitrade.constant;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 推荐人闪兑订单表状态  
 *
 * @author young
 * @time 2019.12.16 15:08
 */
@AllArgsConstructor
@Getter
public enum ReferrerOrderStatus implements BaseEnum, IEnum {
    /**
     * 0=交易中
     */
    TRADING("交易中"),
    /**
     * 1=待完成
     */
    WAIT_FOR_EXCHANGE("待完成"),
    /**
     * 2=已完成
     */
    COMPLETED("已完成");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
