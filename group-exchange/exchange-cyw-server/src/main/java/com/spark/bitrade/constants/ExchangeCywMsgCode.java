package com.spark.bitrade.constants;

import com.spark.bitrade.exception.MessageCodeException;

/**
 * 机器人交易服务消息定义
 *
 * @author yangch
 * @since 2019-08-30 17:26:48
 */
public enum ExchangeCywMsgCode implements MsgCode {
    //币币交易模块=8300 - 8500

    /**
     * 余额未同步
     */
    ERROR_BALANCE_NOT_SYNC(8300, "ERROR_BALANCE_NOT_SYNC"),

    /**
     * 余额不足
     */
    ERROR_BALANCE_NOT_ENOUGH(8301, "ERROR_BALANCE_NOT_ENOUGH"),

    /**
     * 保存已完成订单失败
     */
    SAVE_COMPLETED_ORDER_FAILED(8302, "SAVE_COMPLETED_ORDER_FAILED"),
    /**
     * 保存已撤单订单失败
     */
    SAVE_CANCELED_ORDER_FAILED(8303, "SAVE_CANCELED_ORDER_FAILED"),

    /**
     * 交易信息为null
     */
    EXCHANGE_TRADE_IS_NULL(8304, "EXCHANGE_TRADE_IS_NULL"),
    /**
     * exchangeCoin 为null
     */
    EXCHANGE_COIN_IS_NULL(8305, "EXCHANGE_COIN_IS_NULL"),
    /**
     * 订单为null
     */
    EXCHANGE_ORDER_IS_NULL(8306, "EXCHANGE_ORDER_IS_NULL"),
    /**
     * 无效的机器人订单
     */
    BAD_CYW_ORDER(8307, "BAD_CYW_ORDER"),

    /**
     * 同步至缓存失败
     */
    ERROR_SYNC_TO_CACHE(8308, "ERROR_SYNC_TO_CACHE"),

    /**
     * 写入数据库失败
     */
    ERROR_WRITE_TO_DB(8309, "ERROR_WRITE_TO_DB"),

    /**
     * 交易结算失败
     */
    ERROR_TRADE_SETTLE(8310, "ERROR_TRADE_SETTLE"),

    /**
     * 非法的划转数量
     */
    ILLEGAL_TRANS_AMOUNT(8311, "ILLEGAL_TRANS_AMOUNT"),

    /**
     * 钱包未找到
     */
    WALLET_NOT_FOUNT(8312, "WALLET_NOT_FOUNT"),

    /**
     * 钱包已锁定
     */
    WALLET_LOCKED(8313, "WALLET_LOCKED"),

    /**
     * 创建钱包失败
     */
    CREATE_WALLET_FAILED(8314, "CREATE_WALLET_FAILED"),
    /**
     * 错误的撮合明细
     */
    ERROR_EXCHANGE_TRADE(8315, "ERROR_EXCHANGE_TRADE");

    private final int code;
    private final String message;

    ExchangeCywMsgCode(int code, String message) {
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

    public MessageCodeException asException() {
        return new MessageCodeException(this);
    }
}
