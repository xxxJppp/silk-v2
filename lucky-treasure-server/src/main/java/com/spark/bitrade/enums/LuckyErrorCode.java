package com.spark.bitrade.enums;

import com.spark.bitrade.constants.MsgCode;

public enum LuckyErrorCode implements MsgCode {
    //幸运宝异常

    /**
     * 状态不能为空
     */
    STATUS_MUST_NOT_BE_EMPTY(2401,"状态不能为空"),
    /**
     *活动不存在
     */
    ACT_NOT_FIND(2402,"活动不存在"),
    /**
     * 超过最大购买数量
     */
    OVER_MAX_TICKET(2403,"超过最大购买数量"),
    /**
     * 已封盘 不能选牛
     */
    HAS_ALREADY_END(2404,"活动已结束或已结束选牛,不能选牛"),
    /**
     * 参与失败
     */
    JOIN_FAILED(2405,"参与失败"),
    /**
     * 未配置收款账号
     */
    RECEIVE_ACCOUNT_NOT_FOUND(2406,"未配置收款账号"),
    /**
     * 购买量必须大于0
     */
    BUY_COUNT_MUST_GT_ZERO(2407,"购买量必须大于0"),
    /**
     * 该牛不存在
     */
    THIS_BULL_NOT_FIND(2408,"该牛不存在"),
    /**
     *分享失败
     */
    SHARE_FAILED(2409,"分享失败") ,
    /**
     *加入时间错误
     */
    JOIN_TIME_ERROR(2410,"该场次临近开奖已封盘，请查看其他场次") ,
    
    MONEY_SEND_ERRO(2411 , "奖金发放失败")
    ;


    private final int    code;
    private final String message;

    LuckyErrorCode(int code, String message) {
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
