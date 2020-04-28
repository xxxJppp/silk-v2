package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *  变更流水延迟同步到账户的状态:0=实时同步（默认），1=已同步，2=延迟同步，3=同步中
 *  @author yangch
 *  @time 2019-07-11 16:24:28
 *  
 */
@AllArgsConstructor
@Getter
public enum WalletSyncStatus implements BaseEnum {
    /**
     * 0=实时同步（默认），1=已同步，2=延迟同步，3=同步中
     */
    SYNC_REALTIME("实时同步"),
    /**
     * 1=已同步：已同步到账户余额
     */
    SYNC_COMPLETED("已同步"),

    /**
     * 2=待同步：延迟同步到账户余额
     */
    SYNC_DELAY("待同步"),

    /**
     * 3=同步中：正在同步到账户余额
     */
    SYNC_UNDERWAY("同步中");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }

}
