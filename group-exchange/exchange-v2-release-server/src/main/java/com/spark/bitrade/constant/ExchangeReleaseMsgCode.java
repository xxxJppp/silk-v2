package com.spark.bitrade.constant;

import com.spark.bitrade.constants.MsgCode;

/**
 *  
 *
 * @author young
 * @time 2019.12.17 10:09
 */
public enum ExchangeReleaseMsgCode implements MsgCode {

    /**
     * 卖出价必须等于卖1价或高于卖1价+0.01 USDT
     */
    MUST_GREATER_SELL1(8901, "MUST_GREATER_SELL1"),

    /**
     * 该卖价已达到挂单和成交量上限，请更换价格重试
     */
    CHANGE_PRICE(8902, "CHANGE_PRICE"),
    /**
     * 您对该卖价的挂单量已超出最大挂单和成交量上限，您最多可挂xxx个
     */
    REMAIN_TRADE_AMOUNT(8903, "REMAIN_TRADE_AMOUNT"),

    /**
     * 错误的任务类型
     */
    ERROR_RELEASE_TASK_TYPE(8910, "ERROR_RELEASE_TASK_TYPE"),
    /**
     * 未达到释放时间
     */
    ERROR_RELEASE_TIME(8911, "ERROR_RELEASE_TIME"),
    /**
     * 更新任务状态失败
     */
    ERROR_UPDATE_RELEASE_TASK_STATUS(8912, "ERROR_UPDATE_RELEASE_TASK_STATUS"),
    /**
     * 任务不存在
     */
    ERROR_TASK_INEXISTENCE(8913, "ERROR_TASK_INEXISTENCE"),
    /**
     * 未获取到锁
     */
    FAILED_GET_LOCKED(8914, "FAILED_GET_LOCKED"),

    /**
     * 状态不匹配
     */
    FAILED_MATCH_STATUS(8915, "FAILED_MATCH_STATUS"),

    INEXISTENCE_ORDER(8916, "INEXISTENCE_ORDER"),
    ;

    private final int code;
    private final String message;

    ExchangeReleaseMsgCode(int code, String message) {
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
