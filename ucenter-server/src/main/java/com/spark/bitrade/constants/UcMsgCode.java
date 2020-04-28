package com.spark.bitrade.constants;

/**
 * 用户中心消息定义
 *
 * @author archx
 * @since 2019/5/8 20:23
 */
public enum UcMsgCode implements MsgCode {
    //用户模块=5

    /**
     * 二维码获取失败（OSS二维码解析失败）
     */
    ERROR_PARSE_OSS_QRCODE_URL(6100, "ERROR_PARSE_OSS_QRCODE_URL"),

    /**
     * 激活失败,因为用户名已存在
     */
    ACTIVATION_FAILS_USERNAME(51000, "ACTIVATION_FAILS_USERNAME"),

    /**
     * 请输入手机号或邮箱
     */
    MISSING_USERNAME(51001, "MISSING_USERNAME"),

    /**
     * 请输入密码
     */
    MISSING_PASSWORD(51002, "MISSING_PASSWORD"),

    /**
     * 账号或密码错误
     */
    LOGIN_FALSE(51003, "LOGIN_FALSE"),

    /**
     * 该帐号已经被禁用，请联系客服
     */
    ACCOUNT_DISABLE(51004, "ACCOUNT_DISABLE"),

    /**
     * 手机号为空或格式错误
     */
    PHONE_EMPTY_OR_INCORRECT(51005, "PHONE_EMPTY_OR_INCORRECT"),

    /**
     * 请先获取验证码
     */
    VERIFICATION_CODE_NOT_EXISTS(51006, "VERIFICATION_CODE_NOT_EXISTS"),

    /**
     * 该用户名已存在
     */
    USERNAME_ALREADY_EXISTS(51007, "USERNAME_ALREADY_EXISTS"),

    /**
     * 验证码错误
     */
    VERIFICATION_CODE_INCORRECT(51008, "VERIFICATION_CODE_INCORRECT"),

    /**
     * 推荐码错误，推荐人不存在
     */
    PROMOTION_CODE_ERRO(51009, "PROMOTION_CODE_ERRO"),

    /**
     * 验证失败
     */
    GEETEST_FAIL(51010, "GEETEST_FAIL"),

    /**
     * 该邮箱已经被绑定
     */
    EMAIL_ALREADY_BOUND(51011, "EMAIL_ALREADY_BOUND"),

    /**
     * 邮箱格式错误
     */
    WRONG_EMAIL(51012, "WRONG_EMAIL"),

    /**
     * Silktrader交易平台——验证码
     */
    REGISTRATION_EMAIL_TITLE(51013, "REGISTRATION_EMAIL_TITLE"),

    /**
     * 发送成功，验证码十分钟后过期
     */
    SENT_SUCCESS_TEN(51014, "SENT_SUCCESS_TEN"),

    /**
     * 注册激活邮件已经发送至您邮箱，请勿重复请求
     */
    LOGIN_EMAIL_ALREADY_SEND(51015, "LOGIN_EMAIL_ALREADY_SEND"),

    /**
     * 注册激活邮件已发送至您邮箱,请在12小时内完成注册
     */
    SEND_LOGIN_EMAIL_SUCCESS(51016, "SEND_LOGIN_EMAIL_SUCCESS"),

    /**
     * 注册成功
     */
    REGISTRATION_SUCCESS(51017, "REGISTRATION_SUCCESS"),

    /**
     * 注册失败
     */
    REGISTRATION_FAILED(51018, "REGISTRATION_FAILED"),

    /**
     * 该手机已经注册
     */
    PHONE_ALREADY_EXISTS(51019, "PHONE_ALREADY_EXISTS"),

    /**
     * 非法请求
     */
    REQUEST_ILLEGAL(51020, "REQUEST_ILLEGAL"),

    /**
     * 请求过于频繁，请稍候重试
     */
    FREQUENTLY_REQUEST(51021, "FREQUENTLY_REQUEST"),

    /**
     * 操作成功
     */
    SEND_SMS_SUCCESS(51022, "SEND_SMS_SUCCESS"),

    /**
     * 无此用户
     */
    MEMBER_NOT_EXISTS(51023, "MEMBER_NOT_EXISTS"),

    /**
     * 绑定推荐关系失败
     */
    BIND_PROMOTION_FAIL(51024, "BIND_PROMOTION_FAIL"),

    /**
     * 绑定推荐关系成功
     */
    BIND_PROMOTION_SUCCESS(51025, "BIND_PROMOTION_SUCCESS"),

    /**
     * 已绑定过推荐关系
     */
    BIND_PROMOTION_PASSWORD_ERROR(51026, "BIND_PROMOTION_PASSWORD_ERROR"),
    /**
     * 账号已存在，且已绑定上级推荐人，无法再次绑定
     */
    BIND_PROMOTION_EXISTS(51027, "BIND_PROMOTION_EXISTS"),
    /**
     * 存在循环绑定推荐关系
     */
    PROMOTION_BIND_CYCLE(51028, "PROMOTION_BIND_CYCLE");

    private final int code;
    private final String message;

    UcMsgCode(int code, String message) {
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
