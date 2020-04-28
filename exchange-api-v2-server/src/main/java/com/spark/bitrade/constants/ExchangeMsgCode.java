package com.spark.bitrade.constants;

/**
 * 币币交易消息定义
 *
 * @author yangch
 * @since 2019/5/8 20:23
 */
public enum ExchangeMsgCode implements MsgCode {

    //币币交易模块=8

    /**
     * 未支持的闪兑币种
     */
    NONSUPPORT_FAST_EXCHANGE_COIN(8050, "NONSUPPORT_FAST_EXCHANGE_COIN"),
    /**
     * 无闪兑总账户
     */
    MISSING_FAST_EXCHANGE_ACCOUNT(8051, "MISSING_FAST_EXCHANGE_ACCOUNT"),

    /**
     * 无效的兑换汇率
     */
    INVALID_EXCHANGE_RATE(8100, "INVALID_EXCHANGE_RATE"),
    /**
     * 无效的兑换数量
     */
    INVALID_EXCHANGE_AMOUNT(8102, "INVALID_EXCHANGE_AMOUNT"),

    /**
     * 闪兑订单保存失败
     */
    SAVE_EXCHANGE_ORDER_FAILED(8103, "SAVE_EXCHANGE_ORDER_FAILED"),

    /**
     * 订单不存在
     */
    NONEXISTENT_ORDER(8104, "NONEXISTENT_ORDER"),

    /**
     * 状态不匹配
     */
    UNMATCHED_STATUS(8105, "UNMATCHED_STATUS"),

    /**
     * 更新闪兑订单状态失败
     */
    UPDATE_EXCHANGE_ORDER_STATUS_FAILED(8106, "UPDATE_EXCHANGE_ORDER_STATUS_FAILED"),

    ;

    private final int    code;
    private final String message;

    ExchangeMsgCode(int code, String message) {
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
