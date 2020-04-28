package com.spark.bitrade.constants;

/**
 * 支付模块消息
 *
 * @author daring5920
 * @since 2019/7/30 20:23
 */
public enum PSMsgCode implements MsgCode {
    //支付模块消息 = 6600 至 6700

    /**
     * 无效的币种
     */
    INVALID_UNIT(6600, "INVALID_UNIT"),
    /**
     * 金额超过最大限制
     */
    MAX_AMOUNT_LIMIT(6601, "MAX_AMOUNT_LIMIT"),
    /**
     * 金额低于最小限制
     */
    MIN_AMOUNT_LIMIT(6602, "MIN_AMOUNT_LIMIT"),
    /**
     * 获取币种汇率失败
     */
    GET_RATE_FAILED(6603, "GET_RATE_FAILED"),
    /**
     * 创建订单失败
     */
    BUILD_ORDER_FAILED(6604, "BUILD_ORDER_FAILED"),
    /**
     * 未找到合适的付款账号
     */
    MATCH_ACCOUNT_FAILED(6605, "MATCH_ACCOUNT_FAILED"),
    /**
     * 保存匹配记录失败
     */
    MATCH_RECORD_SAVE_FAILED(6606, "MATCH_RECORD_SAVE_FAILED"),
    /**
     * 获取账号设备串号失败
     */
    GET_DEVICE_SERIAL_NO_FAILED(6607, "GET_DEVICE_SERIAL_NO_FAILED"),
    /**
     * 远程调用失败
     */
    REMOTE_SERVICE_FAILED(6608, "REMOTE_SERVICE_FAILED"),
    /**
     * 用户余额不足
     */
    BALANCE_NOT_ENOUGH(6609, "BALANCE_NOT_ENOUGH"),
    /**
     * 未找到对应记录
     */
    RECORD_NOT_FOUND(6610, "RECORD_NOT_FOUND"),

    /**
     * 不是内测用户
     */
    NOT_INNER_MEMBER(6611, "NOT_INNER_MEMBER"),

    /**
     * 不是实名用户
     */
    NOT_REAL_NAME_MEMBER(6612, "NOT_REAL_NAME_MEMBER"),

    /**
     *没有绑定手机号
     */
    NOT_BIND_PHONE(6613, "NOT_BIND_PHONE"),

    /**
     * 支付宝付款功能关闭
     */
    PAY_CLOSE_AL_PAY(6614, "PAY_CLOSE_AL_PAY"),
    /**
     * 微信支付功能关闭
     */
    PAY_CLOSE_WX_PAY(6615, "PAY_CLOSE_WX_PAY"),
    /**
     * 达到交易上限
     */
    TRADE_UPPER_LIMIT(6616, "TRADE_UPPER_LIMIT"),
    /**
     * 关闭SilkPay支付功能
     */
    PAY_CLOSE_SILK_PAY(6617, "PAY_CLOSE_SILK_PAY"),
    /**
     * 今日可用支付额度不足，无法支付
     */
    TRADE_DAILY_AMOUNT_LIMIT(6618, "TRADE_DAILY_AMOUNT_LIMIT"),
    /**
     * 总可用支付额度不足，无法支付
     */
    TRADE_TOTAL_AMOUNT_LIMIT(6619, "TRADE_TOTAL_AMOUNT_LIMIT"),
    /**
     * 可用支付次数不足，无法支付
     */
    TRADE_TOTAL_NUMBER_LIMIT(6620, "TRADE_TOTAL_NUMBER_LIMIT"),
    ;

    private final int    code;
    private final String message;

    PSMsgCode(int code, String message) {
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
