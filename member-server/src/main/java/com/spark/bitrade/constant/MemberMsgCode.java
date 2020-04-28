package com.spark.bitrade.constant;

import com.spark.bitrade.constants.MsgCode;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.19 18:46
 */
public enum MemberMsgCode implements MsgCode {

    /**
     * 输入的天数不是30的倍数
     */
    DAYS_IS_NOTMULTIPLE_OF_THIRTY(700010, "DAYS_IS_NOTMULTIPLE_OF_THIRTY"),

    /**
     * 社区人数不足
     */
    COMMUNITY_SIZE_INSUFFICIENT(700011, "COMMUNITY_SIZE_INSUFFICIENT"),

    /**
     * 订单保存失败
     */
    SAVE_BENEFITS_ORDER_FAILED(700012, "SAVE_BENEFITS_ORDER_FAILED"),

    /**
     * 结束时间小于当前时间
     */
    ENDTIME_IS_LESS_THAN_CURRENTTIME(700013, "SAVE_BENEFITS_ORDER_FAILED"),

    /**
     * 没有开通会员无法续费
     */
    CANNOT_RENEWALS(700014, "CANNOT_RENEWALS"),

    /**
     * 服务调用失败
     */
    SERVICE_CALL_FAILED(700015, "SERVICE_CALL_FAILED"),

    /**
     * 无法开通
     */
    CANNOT_OPENING(700016, "CANNOT_OPENING"),

    /**
     * 开通天数为0
     */
    OPEN_DAYS_NOT_ZREO(700017, "OPEN_DAYS_NOT_ZREO"),

    /**
     * 价格不正确
     */
    PRICE_MISTAKE(700018, "PRICE_MISTAKE"),

    /**
     * 总账号数据查询失败
     */
    TOTAL_ACCOUNT(700019, "TOTAL_ACCOUNT"),
    ;

    private final int code;

    private final String message;

    MemberMsgCode(int code, String message) {
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
