package com.spark.bitrade.constants;

import com.spark.bitrade.exception.MessageCodeException;

/**
 * 账户消息定义
 *
 * @author yangch
 * @since 2019/5/8 20:23
 */
public enum AcctMsgCode implements MsgCode {

    //帐户模块=6
    /**
     * 账户不存在
     */
    MISSING_ACCOUNT(6000, "MISSING_ACCOUNT"),
    /**
     * 账户无效
     */
    INVALID_ACCOUNT(6001, "INVALID_ACCOUNT"),
    /**
     * 禁止提币
     */
    FORBID_COIN_OUT(6002, "FORBID_COIN_OUT"),
    /**
     * 禁止充币
     */
    FORBID_COIN_IN(6003, "FORBID_COIN_IN"),
    /**
     * 账户签名校验失败
     */
    BAD_ACCOUNT_SIGNATURE(6004, "BAD_ACCOUNT_SIGNATURE"),
    /**
     * 可用余额不足
     */
    ACCOUNT_BALANCE_INSUFFICIENT(6010, "ACCOUNT_BALANCE_INSUFFICIENT"),

    /**
     * 冻结余额不足
     */
    ACCOUNT_FROZEN_BALANCE_INSUFFICIENT(6011, "ACCOUNT_FROZEN_BALANCE_INSUFFICIENT"),
    /**
     * 锁仓余额不足
     */
    ACCOUNT_LOCK_BALANCE_INSUFFICIENT(6012, "ACCOUNT_LOCK_BALANCE_INSUFFICIENT"),

    /**
     * 钱包账户交易失败
     */
    ACCOUNT_BALANCE_TRADE_FAILED(6013, "ACCOUNT_BALANCE_TRADE_FAILED"),

    /**
     * 转账数量无效
     */
    ILLEGAL_TRANS_AMOUNT(6014, "ILLEGAL_TRANS_AMOUNT"),

    /**
     * 转账账户不匹配
     */
    TRANS_ACCOUNT_NOT_MATCH(6015, "TRANS_ACCOUNT_NOT_MATCH");

    private final int code;
    private final String message;

    AcctMsgCode(int code, String message) {
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
