package com.spark.bitrade.constant;

/**
 * WalletType
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/11/21 15:32
 */
public enum WalletType {

    NONE(0, "未知账户"),
    FUND(1, "资金账户"),
    EXCHANGE(8, "币币账户"),
    OTC(9, "法币账户"),
    HQB(10, "活期宝账户");

    private final int code;
    private final String description;

    WalletType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static WalletType of(int code) {
        for (WalletType value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return NONE;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
