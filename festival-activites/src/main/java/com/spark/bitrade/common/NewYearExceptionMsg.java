package com.spark.bitrade.common;

import com.spark.bitrade.constants.MsgCode;

/**
 * @author: Zhong Jiang
 * @date: 2019-12-31 9:54
 */
public enum NewYearExceptionMsg implements MsgCode {

    /**
     * 1 活动不存在
     */
    ACTIVITY_IS_NULL(20200101, "ACTIVITY_IS_NULL"),

    /**
     * 2 矿石活动未开始
     */
    ACTIVITY_IS_NOT_START(20200102, "ACTIVITY_IS_NOT_START"),

    /**
     * 3 矿石活动已结束
     */
    ACTIVITY_IS_END(20200103, "ACTIVITY_IS_END"),

    /**
     * 4 矿石已挖完
     */
    MINERAL_IS_NULL(20200104, "MINERAL_IS_NULL"),

    /**
     * 5 保存失败
     */
    SAVA_ERORR(20200105, "SAVA_ERORR"),

    /**
     * 6 挖矿失败
     */
    MINING_FAIL(20200106, "MINING_FAIL"),

    /**
     * 7 挖矿次数 0
     */
    MINING_NUMBER_IS_ZERO(20200107, "MINING_NUMBER_IS_ZERO"),

    /**
     * 8 每日任务更新失败
     */
    UPDATE_DAILYTASK_FAIL(20200108, "UPDATE_DAILYTASK_FAIL"),

    /**
     * 9 挖矿次数更新失败
     */
    UPDATE_MINING_FAIL(20200109, "UPDATE_MINING_FAIL"),
    /**
     * 10 被赠送人编号错误
     */
    CUSTOMER_ID_ERROR(20200110, "CUSTOMER_ID_ERROR"),
    /**
     * 11 赠送需要至少一个矿石
     */
    MINING_COUNT_Min_ONE(20200111, "赠送需要至少一个矿石"),
    /**
     * 12 请不要重复消费矿石
     */
    RE_CONSUME_ORE(20200112, "请不要重复消费矿石"),
    /**
     * 13 缺少合成令牌的矿石
     */
    LACK_OF_ORE(20200113, "缺少合成令牌的矿石"),
    /**
     * 14 未发现矿石配置！
     */
    NO_ORE_CONFIGURATION_FOUND(20200114, "未发现矿石配置！"),
    /**
     * 15 赠送矿石存量不足
     */
    INSUFFICIENT_ORE_STOCK(20200115, "赠送矿石存量不足"),
    /**
     * 16 一个账号仅可以合成一次令牌
     */
    RE_SYNTHESIZE(20200116, "一个账号仅可以合成一次令牌"),
    /**
     * 17 不能给自己赠送
     */
    GIVE_IT_TO_YOURSELF(20200117, "不能给自己赠送"),

    /**
     * 18 开奖没有令牌
     */
    HAS_NOT_SYNTHESIS_ORE(20200118 , "没有可用的令牌"),
    
    /**
     * 19 已有开奖记录
     */
    RE_SYNTHESIS_ORE(20200119 , "仅可以开奖一次") ,
    
    /**
     * 20 开奖时间不匹配
     */
    SYNTHESIS_TIME_OUT(20200120 , "当前不在开奖时间段") ,
    
    /**
     * 21 超过合成令牌时间
     */
    PUT_SYNTHESIS_TIME_OUT(20200121 , "超过令牌合成时间"), 
    
    /**
     * 22 没有奖励金额配置
     */
    HAS_NOT_MONEY(20200122 , "没有奖励金额配置") ,
    /**
     * 可用余额不足
     */
    ACCOUNT_BALANCE_INSUFFICIENT(6010, "ACCOUNT_BALANCE_INSUFFICIENT"),

    /**
     * 冻结余额不足
     */
    ACCOUNT_FROZEN_BALANCE_INSUFFICIENT(6011, "ACCOUNT_FROZEN_BALANCE_INSUFFICIENT"),
    /**
     * 锁仓余额不足
     */
    ACCOUNT_LOCK_BALANCE_INSUFFICIENT(6012, "ACCOUNT_LOCK_BALANCE_INSUFFICIENT"),
    ;

    private final int code;

    private final String message;

    NewYearExceptionMsg(int code, String message) {
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
