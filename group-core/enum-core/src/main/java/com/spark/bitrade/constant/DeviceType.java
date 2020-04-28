package com.spark.bitrade.constant;

/**
 * @author daring5920
 * @time 2019.05.22 18:42
 */
public enum DeviceType {
    Android("android"),
    IOS("ios"),
    WinPhone("winphone"),
    ALL("all");

    private final String value;

    private DeviceType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
