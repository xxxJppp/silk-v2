package com.spark.bitrade.constants;

/**
 * 模式锁仓消息定义
 *
 * @author archx
 * @since 2019/5/8 20:23
 */
public enum DemoMsgCode implements MsgCode {

    /**
     * 二维码获取失败（OSS二维码解析失败）
     */
    ERROR_PARSE_OSS_QRCODE_URL(6100, "ERROR_PARSE_OSS_QRCODE_URL")
    ;

    private final int    code;
    private final String message;

    DemoMsgCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
