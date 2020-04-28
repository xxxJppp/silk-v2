package com.spark.bitrade.constants;

/**
 * 模式锁仓消息定义
 *
 * @author archx
 * @since 2019/5/8 20:23
 */
public enum LockMsgCode implements MsgCode {
    //活动模块=2

    /**
     * 活动已失效，欢迎关注我们的其他活动
     */
    NOT_HAVE_SET(2001,"NOT_HAVE_SET"),
    /**
     * 无效的活动
     */
    INVALID_ACTIVITY(2002,"INVALID_ACTIVITY"),
    /**
     * 该活动不存在
     */
    NOT_HAVE_ACTIVITY(2003,"NOT_HAVE_ACTIVITY"),
    /**
     * 锁仓保存失败
     */
    LOCK_SAVE_ERROR(2004,"LOCK_SAVE_ERROR"),

    /**
     *  低于最低的购买数量限制
     */
    LIMIT_MIN_BUY_AMOUNT(2005,"LIMIT_MIN_BUY_AMOUNT"),
    /**
     * 高于最大的购买数量限制
     */
    LIMIT_MAX_BUY_AMOUNT(2006,"LIMIT_MAX_BUY_AMOUNT"),
    /**
     * 已超出活动计划的购买数量
     */
    LIMIT_OVER_PLAN_AMOUNT(2007,"LIMIT_OVER_PLAN_AMOUNT"),

    /**
     * 已超出总活动计划的购买数量
     */
    LIMIT_OVER_PROJECT_PLAN_AMOUNT(2008,"LIMIT_OVER_PROJECT_PLAN_AMOUNT"),

    /**
     * 已超出活动次数限制
     */
    LIMIT_OVER_COUNT_VALID_CAMPAIGN(2009,"LIMIT_COUNT_VALID_CAMPAIGN"),

    /**
     * 已超出当日参与活动的次数限制
     */
    LIMIT_OVER_COUNT_IN_DAY(2010,"LIMIT_OVER_COUNT_IN_DAY"),

    /**
     * 无效的升仓套餐
     */
    INVALID_UPGRADE_PACKAGE(2011, "INVALID_UPGRADE_PACKAGE"),

    /**
     * 升仓保存失败
     */
    UPGRADE_PACKAGE_SAVE_ERROR(2012, "UPGRADE_PACKAGE_SAVE_ERROR"),

    /**
     * 未参与过锁仓活动
     */
    NOT_SLP_RECORD(2013, "NOT_SLP_RECORD"),

    /**
     * 有多个正在进行的活动，无法升仓
     */
    MULTIPLE_SLP_RECORD(2014, "MULTIPLE_SLP_RECORD"),

    //超级合伙人异常
    /**
     * 社区不存在
     */
    COMMUNITY_IS_NOT_FIND(2015,"COMMUNITY_IS_NOT_FIND"),
    /**
     * 已经加入了社区
     */
    YOU_HAS_BEEN_IN_COMMUNITY(2016,"YOU_HAS_BEEN_IN_COMMUNITY"),
    /**
     * 你已经加入或正在申请合伙人
     */
    YOU_HAS_OR_APPLY_COMMUNITY(2017,"YOU_HAS_OR_APPLY_COMMUNITY"),
    /**
     * 你还没有加入社区
     */
    YOU_HAS_NOT_JOIN_COMMUNITY(2018,"YOU_HAS_NOT_COMMUNITY"),
    /**
     * 缺少合伙人配置
     */
    SUPER_CONFIG_NOT_FIND(2019,"SUPER_CONFIG_BASE_COIN_NOT_FIND"),
    /**
     * 你不是和合伙人
     */
    YOU_ARE_NOT_PARTNER(2020,"YOU_ARE_NOT_PARTNER"),
    /**
     * 你正在申请退出合伙人请等待审核
     */
    YOU_ARE_EXTING_PARTNER_PLEASE_WATING(2021,"YOU_ARE_EXTING_PARTNER_PLEASE_WATING"),
    /**
     * slu钱包不存在或余额不足
     */
    SLU_WALLET_NOT_FIND_BLANCE_BUZU(2022,"SLU_WALLET_NOT_FIND_BLANCE_BUZU"),
    /**
     *扣减slu失败
     */
    PAY_TO_PARTNER_SLU_FAILT(2023,"PAY_TO_PARTNER_SLU_FAILT"),
    /**
     * 你不能加入自己的社区
     */
    YOU_CANT_JOIN_SELF_COMMUNITY(2024,"YOU_CANT_JOIN_SELF_COMMUNITY"),
    /**
     * 社区名称不符合规则
     */
    COMMUNITY_NAME_IS_INCORRECT(2025,"COMMUNITY_NAME_IS_INCORRECT"),
    /**
     * 你不能退出合伙人
     */
    YOU_CANT_EXIT_SUPERTNER(2026,"YOU_CANT_EXIT_SUPERTNER"),
    /**
     *你已经申请了上币或已经上币
     */
    YOU_HAS_UP_COIN(2027,"YOU_HAS_UP_COIN"),
    /**
     *你必须先退出超级合伙人
     */
    YOU_MUST_EXIT_SUPER_PARTNER(2028,"YOU_MUST_EXIT_SUPER_PARTNER"),
    /**
     *孵化区全局配置不存在
     */
    INCUBATORS_CONFIG_NOT_FIND(2029,"INCUBATORS_CONFIG_NOT_FIND"),
    ;


    private final int    code;
    private final String message;

    LockMsgCode(int code, String message) {
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
