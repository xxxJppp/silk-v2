package com.spark.bitrade.constants;

/**
 * 通用消息定义
 *
 * @author archx
 * @since 2019/5/8 18:05
 */
public enum CommonMsgCode implements MsgCode {

    /**
     * 成功
     */
    SUCCESS(0, "SUCCESS"),
    /**
     * 失败
     */
    FAILURE(1, "FAILURE"),
    /**
     * 未知错误
     */
    ERROR(500, "UNKNOWN_ERROR"),
    /**
     * 未知错误
     */
    UNKNOWN_ERROR(500, "UNKNOWN_ERROR"),

    /**
     * 服务不可用。如：服务不可用或请求超时等
     */
    SERVICE_UNAVAILABLE(503, "SERVICE_UNAVAILABLE"),
    /**
     * 服务响应超时
     */
    SERVICE_RESPONSE_TIMEOUT(504, "SERVICE_RESPONSE_TIMEOUT"),


    // 权限类错误 = 3
    /**
     * 未知账户
     */
    UNKNOWN_ACCOUNT(3000, "UNKNOWN_ACCOUNT"),
    /**
     * 操作未授权
     */
    UNAUTHORIZED(30001, "UNAUTHORIZED"),

    // 请求类错误 = 4（参数、请求类型等）
    /**
     * 无效的请求
     */
    BAD_REQUEST(4000, "BAD_REQUEST"),
    /**
     * 参数无效
     */
    INVALID_PARAMETER(4001, "INVALID_PARAMETER"),
    /**
     * 参数不能为空
     */
    REQUIRED_PARAMETER(4002, "REQUIRED_PARAMETER"),
    /**
     * 无效的请求方式
     */
    INVALID_REQUEST_METHOD(4003, "INVALID_REQUEST_METHOD"),

    /**
     * 获取请求失败
     */
    GET_REQUEST_FAILED(4004, "GET_REQUEST_FAILED"),

    /**
     * apiKey为null
     */
    API_KEY_IS_NULL(4005, "API_KEY_IS_NULL"),

    /**
     * appId为null
     */
    APP_ID_IS_NULL(4006, "APP_ID_IS_NULL"),

    /**
     * apiTime为null
     */
    API_TIME_IS_NULL(4007, "API_TIME_IS_NULL"),

    /**
     * apiSign为null
     */
    API_SIGN_IS_NULL(4008, "API_SIGN_IS_NULL"),

    /**
     * 禁止重复提交
     */
    FORBID_RESUBMIT(4010, "FORBID_RESUBMIT"),


    //用户模块=5
    /**
     * 无资金密码
     */
    MISSING_JYPASSWORD(5100, "MISSING_JYPASSWORD"),
    /**
     * 请先设置资金密码
     */
    NO_SET_JYPASSWORD(5101, "NO_SET_JYPASSWORD"),
    /**
     * 资金密码错误
     */
    ERROR_JYPASSWORD(5102, "ERROR_JYPASSWORD"),

    /**
     * 无用户
     */
    MISSING_MEMBER(5103, "MISSING_MEMBER"),

    //帐户模块=6
    /**
     * 钱包账户交易失败
     */
    ACCOUNT_BALANCE_TRADE_FAILED(6013, "ACCOUNT_BALANCE_TRADE_FAILED"),

    // silkpay支付模块 6600-6700

    //场外交易模块=7

    //币币交易模块=8

    //活动模块=2


    // 其它错误 = 9
    /**
     * 未预见错误
     */
    UNCHECKED_ERROR(9000, "UNCHECKED_ERROR");

    private final int code;
    private final String message;

    CommonMsgCode(int code, String message) {
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

    /**
     * 构建MsgCode类
     *
     * @param code    编码
     * @param message 消息
     * @return
     */
    public static MsgCode of(final int code, final String message) {
        return new MsgCode() {
            @Override
            public int getCode() {
                return code;
            }

            @Override
            public String getMessage() {
                return message;
            }
        };
    }
}
