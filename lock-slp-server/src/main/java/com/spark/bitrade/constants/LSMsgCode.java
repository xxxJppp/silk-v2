package com.spark.bitrade.constants;

/**
 * 模式锁仓消息定义
 *
 * @author archx
 * @since 2019/5/8 20:23
 */
public enum LSMsgCode implements MsgCode {
    //模式锁仓消息 = 2100 至 2150

    /**
     * 无效的数量
     */
    INVALID_AMOUNT(2100, "INVALID_AMOUNT"),

    /**
     * 无效的ioco
     */
    NULL_IOCO(2151, "活动不存在"),
    /**
     * 未找到推荐记录
     */
    NOT_FIND_SLP_PROMOTION(2152, "无上级推荐人"),
    /**
     * ioco异常，请联系客服
     */
    IOCO_EXCEPTION(2153, "活动异常,请联系客服"),
    /**
     * ioco用户活动剩余额度不足
     */
    IOCO_USER_BALANCE_NOT_ENOUGH(2154, "用户当前SLP额度不足"),
    /**
     * ioco请求参数不对，请勿使用非法参数
     */
    IOCO_PARAMETER_VERIFIED(2155, "参数错误"),
    /**
     * ioco用户推荐人数不足
     */
    IOCO_RECOMMEND_NOT_ENOUGH(2156, "推荐人数不足"),
    /**
     * ioco用户不存在
     */
    IOCO_USER_NOT_EXIST(2157, "用户不存在"),
    /**
     * ioco数量太低
     */
    IOCO_AMOUNT_SAMLL(2158, "购买份数太少"),
    /**
     * ioco当前活动余额不足
     */
    IOCO_CURRENT_ACTIVITY_BALANCE_NOT_ENOUGH(2159, "用户当前活动额度不足"),
    /**
     * ioco当前活动已失效
     */
    IOCO_CURRENT_ACTIVITY_ALREADY_INVALID(2160, "当前活动已失效,请刷新"),

    /**
     * ioco购买数量太低
     */
    IOCO_BUY_AMOUNT_TO_SMALL(2161, "购买数量不足"),

    /**
     * 记录保存失败
     */
    RECORD_TO_SAVE(2162,"记录保存失败"),
    /**
     * 无效的汇率
     */
    INVALID_EXCHANGE_RATE(2163,"无效的汇率")

    ;

    private final int    code;
    private final String message;

    LSMsgCode(int code, String message) {
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
