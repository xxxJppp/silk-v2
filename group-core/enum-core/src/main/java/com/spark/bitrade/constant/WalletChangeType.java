package com.spark.bitrade.constant;

import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  用户钱包资金变更类型
 *
 * @author yangch
 * @time 2019.01.23 10:36
 */
@AllArgsConstructor
@Getter
public enum WalletChangeType implements BaseEnum {
    TRADE(0, "正常变更"),
    ROLLBACK(1, "业务回滚");

    private Integer value;
    private String desc;

    @Override
    public int getOrdinal() {
        return 0;
    }
}
