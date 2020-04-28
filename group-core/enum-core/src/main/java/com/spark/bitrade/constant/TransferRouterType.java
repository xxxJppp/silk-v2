package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransferRouterType implements BaseEnum {
    APPLY("商家申请管理"), //0
    APPLY_CANCEL("商家认证取消申请"),
    OTC("场外（O2C）订单"),
    OTC_SELL_UNFILLED_CANCEL("场外（O2C）卖单未成交取消"),
    RECEIVE("接单区块链转账记录"),
    ONLINE("线上订单"),
    GAS("燃料赠送");


    private String cnName;
    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }

}
