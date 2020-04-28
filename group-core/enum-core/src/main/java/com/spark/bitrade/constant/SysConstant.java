package com.spark.bitrade.constant;

import javax.swing.*;

/**
 * 系统常量
 *
 * @author Zhang Jinwei
 * @date 2017年12月18日
 */
public class SysConstant {

    //最新交易缓存key
    public static final String TRADE_PLATE_MAP = "entity:trade:plateMap:";

    //第三方平台token
    public static final String THIRD_TOKEN = "entity:third:token:";

    //C2C防止重复提交订单标志
    public static final String C2C_DEALING_ORDER = "entity:otcOrder:";

    //C2C中的订单监控key
    public static final String C2C_MONITOR_ORDER = "busi:monitor:otcOrder:";

    //C2C中的订单数
    public static final String C2C_ONLINE_NUM = "onlineNum";

    //C2C未处理事件消息列表前缀
    public static final String NOTICE_OTC_EVENT_PREFIX = "notice:otc:envent:";
    //C2C未读聊天消息列表前缀
    public static final String NOTICE_OTC_CHAT_PREFIX = "notice:otc:chat:";
    //系统通知列表前缀
    public static final String NOTICE_SYS_PREFIX = "notice:sys:";

    //中文语言
    public static final String ZH_LANGUAGE = "zh_CN";

    //英文语言
    public static final String EN_LANGUAGE = "en_US";

    //中文
    public static final String CHINESE_LANGUAGE = "zh";

    //英文
    public static final String ENGLISH_LANGUAGE = "en";

    //中国
    public static final String CHINA = "中国";


    /**
     * 匹配超时订单通知
     */
    public final static String MSG_MATCH_TIMEOUT_ORDER = "msg-match-timeout-order";

    // by  shenzucai 时间： 2019.06.02  原因：用于付款超时取消
    /**
     * otc付款超时订单通知
     */
    public final static String MSG_OTC_TIMEOUT_PAY = "msg_otc_timeout_pay";

    /**
     * online付款超时订单通知
     */
    public final static String MSG_ONLINE_TIMEOUT_PAY = "msg_online_timeout_pay";


    /**
     * otc或online匹配成功消息
     */
    public final static String MSG_OTC_MATCH_OR_ONLINE_ORDER_CREATED = "msg_otc_match_or_online_order_created";

    /**
     * 订单消息
     */
    public final static String MSG_EXCHANGE_ORDER = "exchange-order";
    /**
     * 订单匹配成功消息
     */
    public final static String MSG_EXCHANGE_ORDER_COMPLETED = "exchange-order-completed";

    /**
     * 交易匹配消息
     */
    public final static String MSG_EXCHANGE_TRADE = "exchange-trade";
    /**
     * 订单盘口消息
     */
    public final static String MSG_EXCHANGE_TRADE_PLATE = "exchange-trade-plate";

    /**
     * 撤销订单消息
     */
    public final static String MSG_EXCHANGE_ORDER_CANCEL = "exchange-order-cancel";

    /**
     * 订单人工匹配成功消息
     */
    public final static String MSG_EXCHANGE_ORDER_MANUAL_MATCHED = "exchange-order-manual-matched";

    /**
     * 订单取消成功消息
     */
    public final static String MSG_EXCHANGE_ORDER_CANCEL_SUCCESS = "exchange-order-cancel-success";

    /**
     * 管理指定交易币的匹配器
     */
    public final static String EXCHANGE_TRADER_MANAGER = "exchange-trader-manager";

    /**
     * 管理指定交易币的处理器
     */
    public final static String EXCHANGE_PROCESSOR_MANAGER = "exchange-processor-manager";


    /**
     * 区块链hash事件通知
     */
    public final static String MSG_TXHASH_EVENT_NOTIFICATION  = "msg-txhash-event-notification";

    //用户登录信息,0,表示已登录，1表示已经退出
    public static final String MEMBER_LOGOUT = "spring:session:logout:";

    /**
     * 验证码
     */
    public static final String PHONE_REG_CODE_PREFIX = "PHONE_REG_CODE_";           // 收集注册
    public static final String EMAIL_REG_CODE_PREFIX = "EMAIL_REG_CODE_";           // 邮箱注册
    public static final String RESET_PASSWORD_CODE_PREFIX = "RESET_PASSWORD_CODE_"; // 重置密码
    public static final String USER_BIND_PROMOTION_PREFIX = "USER_BIND_PROMOTION_"; // 推荐关系绑定
}
