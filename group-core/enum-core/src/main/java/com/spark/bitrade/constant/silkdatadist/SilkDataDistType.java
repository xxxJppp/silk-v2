package com.spark.bitrade.constant.silkdatadist;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author daring5920
 * @description 系统广告位置，注意该枚举是存储具体的name到数据库中
 * @date 2018/1/6 15:59
 */
@Getter
public enum SilkDataDistType implements BaseEnum {

    /**
     * 赠送手续费
     */
    GAS_SEND(null,"GAS_SEND"),
    //一级商家
    GAS_ONE_BUSINESS(GAS_SEND,"GAS_ONE_BUSINESS"),
    //二级商家
    GAS_TWO_BUSINESS(GAS_SEND,"GAS_TWO_BUSINESS"),
    //三级商家
    GAS_THREEE_BUSINESS(GAS_SEND,"GAS_THREEE_BUSINESS"),
    //四级商家
    GAS_FOUR_BUSINESS(GAS_SEND,"GAS_FOUR_BUSINESS"),
    //五级商家
    GAS_FIVE_BUSINESS(GAS_SEND,"GAS_FIVE_BUSINESS"),
    //买币
    GAS_BUY_SUCCEED(GAS_SEND,"GAS_BUY_SUCCEED"),


    /**
     * 商家是否需要审核
     */
    REVIEW(null,"REVIEW"),
    //一级商家
    REVIEW_ONE_BUSINESS(REVIEW,"REVIEW_ONE_BUSINESS"),
    //二级商家
    REVIEW_TWO_BUSINESS(REVIEW,"REVIEW_TWO_BUSINESS"),
    //三级商家
    REVIEW_THREEE_BUSINESS(REVIEW,"REVIEW_THREEE_BUSINESS"),
    //四级商家
    REVIEW_FOUR_BUSINESS(REVIEW,"REVIEW_FOUR_BUSINESS"),
    //五级商家
    REVIEW_FIVE_BUSINESS(REVIEW,"REVIEW_FIVE_BUSINESS"),


    /**
     * 时间规则
     */
    TIME(null,"TIME"),
    // 卖单可以手动取消的时间限制
    TIME_OTC_SELL_CANCEL(TIME,"TIME_OTC_SELL_CANCEL"),
    // 付款超时自动取消的时间限制
    TIME_OTC_PAY_AUTO_CANCEL(TIME,"TIME_OTC_PAY_AUTO_CANCEL"),
    // 线上订单付款超时，自动取消时间限制
    TIME_ONLINE_PAY_AUTO_CANCEL(TIME,"TIME_ONLINE_PAY_AUTO_CANCEL"),

    /**
     * 币种全局信息
     */
    COIN(null,"COIN"),
    /**
     * 区块确认数
     */
    COIN_CONFIRM_TIMES(COIN,"COIN_CONFIRM_TIMES"),
    /**
     * 线上默认交易币种
     */
    COIN_DEFAULT_ONLINE_EXCHANG(COIN,"COIN_DEFAULT_ONLINE_EXCHANG"),
    /**
     * OTC默认交易币种
     */
    COIN_DEFAULT_OTC_EXCHANG(COIN,"COIN_DEFAULT_OTC_EXCHANG"),



    //用于匹配规则依据
    /**
     * 匹配限制（依据）
     */
    MATCH_LIMIT(null,"MATCH_LIMIT"),

    /**
     * otc alipay 可用交易次数
     */
    MATCH_LIMIT_OTC_ALIPAY(MATCH_LIMIT,"MATCH_LIMIT_OTC_ALIPAY"),
    /**
     * otc wechat 可用交易次数
     */
    MATCH_LIMIT_OTC_WECHAT(MATCH_LIMIT,"MATCH_LIMIT_OTC_WECHAT"),
    /**
     * otc bank 可用交易次数
     */
    MATCH_LIMIT_OTC_BANK(MATCH_LIMIT,"MATCH_LIMIT_OTC_BANK"),
    /**
     * online alipay 可用交易次数
     */
    MATCH_LIMIT_ONLINE_ALIPAY(MATCH_LIMIT,"MATCH_LIMIT_ONLINE_ALIPAY"),
    /**
     * online wechat 可用交易次数
     */
    MATCH_LIMIT_ONLINE_WECHAT(MATCH_LIMIT,"MATCH_LIMIT_ONLINE_WECHAT"),
    /**
     * online bank 可用交易次数
     */
    MATCH_LIMIT_ONLINE_BANK(MATCH_LIMIT,"MATCH_LIMIT_ONLINE_BANK"),

    //用于系统基本配置
    /**
     * 系统配置
     */
    SYSTEM(null,"SYSTEM"),
    /**
     * 线上订单异步回调地址
     */
    SYSTEM_SYNC_NOTIFY(SYSTEM, "SYSTEM_SYNC_NOTIFY"),
    /**
     * 线上订单交易用户
     */
    SYSTEM_ONLINE_MEMBER(SYSTEM, "SYSTEM_ONLINE_MEMBER");

    @Setter
    private SilkDataDistType type;

    @Setter
    private String name;


    SilkDataDistType(SilkDataDistType type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    @JsonValue
    public int getOrdinal() {
        return ordinal();
    }

}
