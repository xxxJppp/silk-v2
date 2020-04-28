package com.spark.bitrade.constants;

import com.spark.bitrade.exception.MessageCodeException;

/**
 * 机器人交易服务消息定义
 *
 * @author yangch
 * @since 2019-08-30 17:26:48
 */
public enum ExchangeOrderMsgCode implements MsgCode {
    //币币交易模块=8300 - 8500 - 8700

    /**
     * 保存订单失败
     */
    SAVE_ORDER_FAILED(8500, "SAVE_ORDER_FAILED"),

    /**
     * 更新已完成订单失败
     */
    UPDATE_COMPLETED_ORDER_FAILED(8501, "UPDATE_COMPLETED_ORDER_FAILED"),

    /**
     * 更新已撤单订单失败
     */
    UPDATE_CANCELED_ORDER_FAILED(8503, "UPDATE_CANCELED_ORDER_FAILED"),

    /**
     * 无效的订单
     */
    BAD_ORDER(8507, "BAD_ORDER"),


    /**
     * 余额不足
     */
    ERROR_BALANCE_NOT_ENOUGH(8301, "ERROR_BALANCE_NOT_ENOUGH"),


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
    ERROR_EXCHANGE_TRADE(8315, "ERROR_EXCHANGE_TRADE"),


    /**
     * 非法的价格
     */
    ILLEGAL_PRICE(8400, "ILLEGAL_PRICE"),

    /**
     * 无效的数量
     */
    ILLEGAL_QUANTITY(8401, "ILLEGAL_QUANTITY"),

    /**
     * 不支持的交易
     */
    UNSUPPORTED(8402, "UNSUPPORTED"),

    /**
     * 无效的用户
     */
    ILLEGAL_USER(8403, "ILLEGAL_USER"),

    /**
     * 该帐号已经被禁用，请联系客服
     */
    ACCOUNT_DISABLE(8404, "ACCOUNT_DISABLE"),

    /**
     * 已限制交易，请联系客服
     */
    LIMIT_TRAD(8405, "LIMIT_TRAD"),
    /**
     * 成交额小于最低成交额的限制要求
     */
    TURNOVER_LIMIT(8406, "TURNOVER_LIMIT"),
    /**
     * 交易数量小于最低委托数量的限制要求
     */
    NUMBER_LIMIT(8407, "NUMBER_LIMIT"),
    /**
     * 不支持买入
     */
    NOT_SUPPORT_BUY(8408, "NOT_SUPPORT_BUY"),

    /**
     * 不支持卖出
     */
    NOT_SUPPORT_SELL(8409, "NOT_SUPPORT_SELL"),
    /**
     * 最低价格限制
     */
    CANNOT_LOWER(8410, "CANNOT_LOWER"),
    /**
     * 最大委托订单数量限制
     */
    MAXIMUM_TRADING_LIMIT(8411, "MAXIMUM_TRADING_LIMIT"),

    /**
     * 无效的交易验证码
     */
    INVALID_TRADE_CAPTCHA(8412, "INVALID_TRADE_CAPTCHA"),



    /* /////////////////////////////////////////////
     * 8800 ~ 8900 额外补充定义
     * /////////////////////////////////////////////
     */


    /**
     * 未支持的操作
     */
    UNSUPPORTED_OPERATION(8800, "UNSUPPORTED_OPERATION"),

    /**
     * 未实现的操作
     */
    NOT_IMPLEMENTED(8801, "NOT_IMPLEMENTED");


    private final int code;
    private final String message;

    ExchangeOrderMsgCode(int code, String message) {
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
