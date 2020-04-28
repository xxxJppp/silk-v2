package com.spark.bitrade.enums;


import com.spark.bitrade.constants.MsgCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: otc异常消息枚举
 * @author: ss
 * @date: 2020/3/19
 */
@AllArgsConstructor
@Getter
public enum  OtcExceptionMsg implements MsgCode {

    MEMBER_HAS_NO_BASE_CURRENCY(2500,"尚未设置默认法币") ,
    INVALID_CURRENCY(2501 , "无效法币") ,
    HAS_NO_BASE_CHANGE_SETTING(2502 , "没有修改法币相关配置") ,
    CHANGE_CURRENCY_ERROR(2503 , "修改默认法币余额支付失败"),
    UNLOCK_CAP_SETTING_NO(2504 , "当前未绑定该支付方式") ,
    INVALID_UNIT(2505 , "无效货币")  ,
    ACCOUNT_BALANCE_INSUFFICIENT(2506 , "账户余额不足"),
    /**
     *  获取汇率失败
     */
    USD_RATE_GET_FAILED(2307,"获取汇率失败"),
    /**
     * 普通用户不能发布广告
     */
    CAN_NOT_PUBLISH(2507,"普通用户不能发布广告"),
    /**
     * 最大交易额超出范围
     */
    max_trade_limit(2508,"最大交易额超出范围"),
    /**
     * CNYT价格不能为溢价
     */
    MUST_CHANGE(2509,"价格不能为溢价"),
    /**
     * CNYT价格必须为1.00
     */
    CNYT_MUST_EQ_ONE(2510,"价格必须为1.00"),
    /**
     * MISSING_PAY=请输入付款方式
     */
    MISSING_PAY(2511,"请输入付款方式"),
    /**
     * MISSING_PAY=请输入付款方式
     */
    SELL_NUM_TOO_LITTLE(2540,"出售数量太小"),
    /**
     * MISSING_PAY=请输入付款方式
     */
    BUY_NUM_TOO_LITTLE(2541,"购买数量太小"),
    /**
     * 请输入资金密码
     */
    MISSING_JYPASSWORD(2512,"请输入资金密码"),
    /**
     * 您已被限制C2C交易
     */
    NO_ALLOW_TRANSACT(2513,"您已被限制C2C交易"),
    /**
     * 您已被限制法币买入交易
     */
    NO_ALLOW_TRANSACT_BUY(2514,"您已被限制法币买入交易"),
    /**
     * 您已被限制法币卖出交易
     */
    NO_ALLOW_TRANSACT_SELL(2515,"您已被限制法币卖出交易"),
    /**
     * 禁止发布广告
     */
    NOT_ADVERTISING(2516,"禁止发布广告"),
    /**
     * 5101，请先设置资金密码
     */
    NO_SET_JYPASSWORD(5101,"请先设置资金密码"),
    /**
     * 5102 资金密码错误
     */
    ERROR_JYPASSWORD(5102,"资金密码错误"),
    /**
     * 6010 可用余额不足
     */
    INSUFFICIENT_BALANCE(6010,"可用余额不足"),
    /**
     * 冻结余额不足
     */
    ACCOUNT_FROZEN_BALANCE_INSUFFICIENT(6011, "冻结余额不足"),
    /**
     * 创建成功
     */
    CREATE_SUCCESS(2517,"创建成功"),
    /**
     *  创建失败
     */
    CREATE_FAILED(2518,"创建失败"),
    /**
     *  请先下架所有广告
     */
    AFTER_OFF_ALL_SHELVES(2519,"请先下架所有广告"),
    /**
     *  有正在进行中的订单
     */
    HAVE_ORDER_ON(2520,"有正在进行中的订单"),
    /**
     *  支付方式不支持，请检查支付方式
     */
    PAY_MODE_ERR(2521,"支付方式不支持，请检查支付方式"),
    /**
     *  用户没有设置该支付方式，请前往个人中心完善支付方式设置
     */
    PAY_MODE_NOT_SETTING(2522,"用户没有设置该支付方式，请前往个人中心完善支付方式设置"),
    /**
     *  下架失败
     */
    PUT_OFF_SHELVES_FAILED(2523,"下架失败"),
    /**
     *  下架成功
     */
    PUT_OFF_SHELVES_SUCCESS(2524,"下架成功"),
    /**
     *  上架失败
     */
    PUT_ON_SHELVES_FAILED(2525,"上架失败"),
    /**
     *  上架成功
     */
    PUT_ON_SHELVES_SUCCESS(2526,"上架成功"),
    /**
     *  修改失败
     */
    UPDATE_FAILED(2527,"修改失败"),
    /**
     *  修改成功
     */
    UPDATE_SUCCESS(2528,"修改成功"),
    /**
     *  请先下架广告再编辑
     */
    AFTER_OFF_SHELVES(2529,"请先下架广告再编辑"),
    /**
     *  法币ID必须输入
     */
    MISSING_CURRENCY_ID(2530,"法币ID必须输入"),
    /**
     *  OtcCoin_Id必须输入
     */
    MISSING_OTC_COIN_ID(2531,"OtcCoin_Id必须输入"),
    /**
     *  OtcCoin不存在
     */
    OTC_COIN_NOT_EXIST(2532,"OtcCoin不存在"),
    /**
     *  广告类型必须输入
     */
    MISSING_ADVERTISE_TYPE(2533,"广告类型必须输入"),
    /**
     *  法币不存在
     */
    CURRENCY_NOT_EXIST(2534,"法币不存在"),
    /**
     *  您有未完成的订单，请先完成
     */
    NO_ALLOW_TRADE(2535,"您有未完成的订单，请先完成"),
    /**
     *  数量错误
     */
    NUMBER_ERROR(2536,"数量错误"),
    /**
     *  请先绑定手机
     */
    NOT_BIND_PHONE(2537,"请先绑定手机"),
    /**
     *  请先完成实名认证
     */
    NO_REAL_NAME(2538,"请先完成实名认证"),
    /**
     *  交易数量不足
     */
    TRANSACTIONS_NOT_ENOUGH(2539,"交易数量不足"),
    /**
     *  广告删除失败
     */
    DELETE_ADVERTISE_FAILED(2542,"广告删除失败"),
    /**
     *  下架广告后才可删除
     */
    DELETE_AFTER_OFF_SHELVES(2543,"下架广告后才可删除"),
    /**
     *  广告删除成功
     */
    DELETE_ADVERTISE_SUCCESS(2544,"广告删除成功"),
    /**
     *  活动币种价格获取失败
     */
    PRICE_ERROR(2545,"活动币种价格获取失败"),
    /**
     *  默认法币已更改
     */
    BASE_CURRENCY_UPDATED(2546,"默认法币已更改"),

    HAS_SET_THIS_CURRENCY_BEFOR(2547 , "请选择其他法币"),
    /**
     * 必须绑定银行卡
     */
    BANK_NOT_SETTING(2548,"必须绑定银行卡"),
    /**
     * 默认法币不支持与当前币种交易
     */
    BASE_CURRENCY_NOT_SUPPORT_OTC_COIN(2549,"默认法币不支持与当前币种交易"),
    /**
     * 法币与用户默认法币不符
     */
    CURRENCY_NOT_EQUALS_BASE_CURRENCY(2550,"法币与用户默认法币不符"),
    /**
     * 广告不存在
     */
    AD_NOT_EXIST(2551,"广告不存在"),
    /**
     * 未配置收款归集账号
     */
    RECEIVE_ACCOUNT_NOT_FOUND(2552,"未配置收款归集账号"),
    /**
     * 不是经纪人
     */
    NOT_AGENT(2553,"不是经纪人"),
    /**
     * 没有配置经纪人优惠兑换USDC额度
     */
    NOT_AGENT_PAY_USDC_MAX(2554,"没有配置经纪人优惠兑换USDC额度"),
    /**
     * 创建经纪人优惠兑换USDC额度失败
     */
    CREATE_AGENT_PAY_USDC_MAX_FAIL(2555,"创建经纪人优惠兑换USDC额度失败"),
    /**
     * 没有配置USDC兑换报价币种
     */
    NOT_USDC_UNIT(2556,"没有配置USDC兑换报价币种"),
    /**
     * 兑换数量操出限制
     */
    MAX_LIMIT(2557,"兑换数量超出限制"),
    /**
     * 兑换数量不足
     */
    MIN_LIMIT(2558,"兑换数量不足"),
    /**
     *  价格过期，请刷新重试
     */
    PRICE_EXPIRED(2559,"价格过期，请刷新重试"),
    /**
     *  经纪人优惠兑换USDC总账号未配置
     */
    NOT_AGENT_USDC_ACCOUNT(2560,"经纪人优惠兑换USDC总账号未配置"),
    /**
     *  USDC单次最大兑换数量未配置
     */
    NOT_USDC_MAX_PAY(2561,"USDC单次最大兑换数量未配置"),
    /**
     *  USDC单次最小兑换数量未配置
     */
    NOT_USDC_MIN_PAY(2562,"USDC单次最小兑换数量未配置"),
    /**
     *  经纪人优惠兑换USDC额度未配置
     */
    NOT_AGENT_PAY_USDC(2563,"经纪人优惠兑换USDC额度未配置"),
    /**
     *  法币规则未配置
     */
    NOT_CURRENTY_RULE(2564,"法币规则未配置"),
    /**
     *  订单保存失败
     */
    ORDER_FAIL(2565,"订单保存失败"),
    /**
     *  商家同时交易订单数已达上限
     */
    MAX_TRADING_ORDERS(1,"商家同时交易订单数已达上限"),
    /**
     *  该广告已下架
     */
    ALREADY_PUT_OFF(1,"该广告已下架"),
    /**
     *  金额不得低于
     */
    MONEY_MIN(1,"金额低于最小限额"),
    /**
     *  金额不得高于
     */
    MONEY_MAX(1,"金额高于最大限额"),
    /**
     *  商家之间不可进行交易
     */
    SELLER_ALLOW_TRADE(1,"商家之间不可进行交易"),
    /**
     *  不可通过自己的广告购买
     */
    NOT_ALLOW_BUY_BY_SELF(1,"不可通过自己的广告购买"),
    /**
     *  参数错误
     */
    PARAMETER_ERROR(1,"参数错误"),
    /**
     * 无此用户
     */
    MEMBER_NOT_EXISTS(51023, "无此用户");


    private Integer code;
    private String message;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
