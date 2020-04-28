package com.spark.bitrade.constant;

import com.spark.bitrade.constants.MsgCode;

/**
 * 模式锁仓消息定义
 *
 * @author archx
 * @since 2019/5/8 20:23
 */
public enum SupportCoinMsgCode implements MsgCode {
    //上币申请模块=8

    /**
     * 你的上币申请未审核,或审批已通过!
     */
    APPLY_HAS_ALREADY_EXIST(2301,"APPLY_HAS_ALREADY_EXIST"),
    /**
     * 不是项目方
     */
    IS_NOT_PRJECT_PARTNER(2302,"IS_NOT_PRJECT_PARTNER"),

    /**
     * 配置未找到
     */
    CONFIG_LIST_NOT_FIND(2303,"CONFIG_LIST_NOT_FIND"),

    /**
     *支付金额高于配置金额
     */
    PAY_AMOUNT_MUST_BE_LOWER_CONFIG(2304,"PAY_AMOUNT_MUST_BE_LOWER_CONFIG"),
    /**
     *保存转版管理失败
     */
    SAVE_CHANGE_SECTION_FAILED(2305,"SAVE_CHANGE_SECTION_FAILED"),
    /**
     *存在未审核的转版管理申请
     */
    CHANGE_SECTION_HAS_ALREADY_EXIST(2306,"CHANGE_SECTION_HAS_ALREADY_EXIST"),
    /**
     * 获取汇率失败或汇率为0
     */
    USDT_RATE_GET_FAILED(2307,"USDT_RATE_GET_FAILED"),
    /**
     *存在未审核的打开引流申请
     */
    STREAM_SWITCH_APPLY_HAS_ALREADY_EXIST(2308,"STREAM_SWITCH_APPLY_HAS_ALREADY_EXIST"),
    /**
     *保存引流失败失败
     */
    SAVE_STREAM_FAILED(2305,"SAVE_STREAM_FAILED"),

    /**
     * 资讯信息新增还在审核中
     */
    NEWSINFO_IS_EXIST_PENDING(2309, "NEWSINFO_IS_EXIST_PENDING"),

    /**
     * 交易对新增还在审核中
     */
    COINMATCH_IS_EXIST_PENDING(2310, "COINMATCH_IS_EXIST_PENDING"),
    /**
     * 获取钱包失败
     */
    GET_WALLET_FAILED(2311,"GET_WALLET_FAILED"),
    /**
     * 可用余额不足
     */
    ACCOUNT_BALANCE_INSUFFICIENT(2312, "ACCOUNT_BALANCE_INSUFFICIENT"),

    /**
     * 币种基本信息审核修改
     */
    COINRECORD_IS_EXIST_PENDING(2313, "COINRECORD_IS_EXIST_PENDING"),
    /**
     *币种查询失败
     */
    COIN_FIND_FAILED(2314,"COIN_FIND_FAILED"),

    /**
     * 保存交易对失败
     */
    SAVE_COINMATCH_FAILED(2315,"SAVE_STREAM_FAILED"),
    /**
     *查询汇率失败
     */
    HUILV_FIND_FAILED(2316,"HUILV_FIND_FAILED"),

    /**
     * 服务调用失败
     */
    FEIGN_SERVER_FAILED(2317,"FEIGN_SERVER_FAILED"),

    /**
     * 无法增加相同交易对
     */
    CAN_NOT_ADD_COINMATCH_FAILED(2318,"CAN_NOT_ADD_COINMATCH_FAILED"),
    /**
     *当前不能转版
     */
    CANT_CHANGE_SECTION(2319,"CANT_CHANGE_SECTION"),
    /**
     * 上币币种已存在
     */
    COIN_HAS_ALREADY_EXIST(2320,"币种已存在"),
    /**
     * 存在必输参数未输入,或格式错误
     */
    PARAMETER_INCORRECT(2321,"存在必输参数未输入,或格式错误"),
    /**
     * 当前版块不可打开引流开关
     */
    CANT_OPEN_STREAM_SWITCH(2322,"当前版块不可打开引流开关"),
    /**
     * 请先到“引流和交易码管理”中打开引流开关，再申请转版
     */
    OPEN_STREAM_SWITCH_AFTER_CHANGE_SECTION(2323,"请先到“引流和交易码管理”中打开引流开关，再申请转版"),
    /**
     * 请先到“引流和交易码管理”中打开引流开关，再添加新交易对
     */
    OPEN_STREAM_SWITCH_AFTER_ADD_MATCH(2324,"请先到“引流和交易码管理”中打开引流开关，再添加新交易对"),
    /**
     *币种简介为必填内容，请填写后再保存
     */
    COIN_MESSAGE_INTRO_NOT_EMPTY(2325,"币种简介为必填内容，请填写后再保存"),
    /**
     *扶持上币不能新增交易对
     */
    CANT_ADD_MATCH_COIN(2326,"扶持上币不能新增交易对"),

    //红包相关异常
    HAS_EXIST_OPEN_APPLY(2327,"申请正在审核"),

    HAS_ALREADY_OPEN_REDPACK(2328,"审核已通过请勿重新申请"),

    RED_PACK_NOT_OPEN(2329,"红包功能未开通"),

    RED_PACK_NOT_FIND(2330,"红包不存在,或红包未审核通过"),

    TIME_IS_NOT_DONE(2331,"红包开始时间不能大于结束时间"),

    TIME_MUST_BE_VALID(2332,"红包结束时间必须大于当前时间"),

    MAX_AMOUNT_MUST_BE_LOWER_TOTAL(2333,"红包最大值必须小于等于总金额"),
    ;


    private final int    code;
    private final String message;

    SupportCoinMsgCode(int code, String message) {
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
