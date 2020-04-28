package com.spark.bitrade.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum BizRemarksEnum {


    RECOMMEND_RETURN(41, "推荐会员币币交易手续费返佣"),

    /**
     * 推荐会员币币交易手续费返佣
     */
    RETURN_COMMISSION(1,"币币交易手续费返还"),

    /**
     * 直推VIP1会员费返佣
     */
    VIP1_BUY_RETURN(2,"直推VIP1会员费返佣"),

    /**
     * 直推VIP1会员费锁仓分红
     */
    VIP1_LOCK_RETURN(3,"直推VIP1会员费锁仓分红"),

    /**
     * 直推VIP2会员费返佣
     */
    VIP2_BUY_RETURN(4,"直推VIP2会员费返佣"),

    /**
     * 直推VIP2会员费锁仓分红
     */
    VIP2_LOCK_RETURN(5,"直推VIP2会员费锁仓分红"),

    /**
     * 直推VIP3会员费返佣
     */
    VIP3_BUY_RETURN(6,"直推VIP3会员费返佣"),

    /**
     * 直推VIP3会员费锁仓分红
     */
    VIP3_LOCK_RETURN(7,"直推VIP3会员费锁仓分红"),

    /**
     * 直推经纪人会员费返佣
     */
    AGENT_BUY_RETURN(8,"直推经纪人会员费返佣"),

    /**
     * 直推经纪人会员费锁仓分红
     */
    AGENT_LOCK_RETURN(9,"直推经纪人会员费锁仓分红"),

    RETURN_COMMISSION_EN(10,"Commission rebate for spot trading referral"),

    /**
     * 直推VIP1会员费返佣
     */
    VIP1_BUY_RETURN_EN(11,"Commission rebate for VIP1 direct referral"),

    /**
     * 直推VIP1会员费锁仓分红
     */
    VIP1_LOCK_RETURN_EN(12,"Locking dividend for VIP1 direct referral"),

    /**
     * 直推VIP2会员费返佣
     */
    VIP2_BUY_RETURN_EN(13,"Commission rebate for VIP2 direct referral"),

    /**
     * 直推VIP2会员费锁仓分红
     */
    VIP2_LOCK_RETURN_EN(14,"Locking dividend for VIP2 direct referral"),

    /**
     * 直推VIP3会员费返佣
     */
    VIP3_BUY_RETURN_EN(15,"Commission rebate for VIP3 direct referral"),

    /**
     * 直推VIP3会员费锁仓分红
     */
    VIP3_LOCK_RETURN_EN(16,"Locking dividend for VIP3 direct referral"),

    /**
     * 直推经纪人会员费返佣
     */
    AGENT_BUY_RETURN_EN(17,"Commission rebate for broker direct referral"),

    /**
     * 直推经纪人会员费锁仓分红
     */
    AGENT_LOCK_RETURN_EN(18,"Locking dividend for broker direct referral"),


    RETURN_COMMISSION_KO(19,"회원권 거래수수료 수수료 반납 추천"),

    /**
     * 直推VIP1会员费返佣
     */
    VIP1_BUY_RETURN_KO(20,"VIP1회원비 반환을 직추"),

    /**
     * 直推VIP1会员费锁仓分红
     */
    VIP1_LOCK_RETURN_KO(21,"VIP1회원비 창고잠금배당금 직추"),

    /**
     * 直推VIP2会员费返佣
     */
    VIP2_BUY_RETURN_KO(22,"VIP2회원비 반환을 직추"),

    /**
     * 直推VIP2会员费锁仓分红
     */
    VIP2_LOCK_RETURN_KO(23,"VIP2회원비 창고잠금배당금 직추"),

    /**
     * 直推VIP3会员费返佣
     */
    VIP3_BUY_RETURN_KO(24,"VIP3회원비 반환을 직추"),

    /**
     * 直推VIP3会员费锁仓分红
     */
    VIP3_LOCK_RETURN_KO(25,"VIP3회원비 창고잠금배당금 직추"),

    /**
     * 直推经纪人会员费返佣
     */
    AGENT_BUY_RETURN_KO(26,"직접 중개인을 추천하면 회비 반환을 받을 수 있다"),

    /**
     * 直推经纪人会员费锁仓分红
     */
    AGENT_LOCK_RETURN_KO(27,"직접 중개인을 추천하면 회원비 잠금 배당"),


    RETURN_COMMISSION_HK(28,"推薦會員幣幣交易手續費返傭"),

    /**
     * 直推VIP1会员费返佣
     */
    VIP1_BUY_RETURN_HK(29,"直推VIP1會員費返傭"),

    /**
     * 直推VIP1会员费锁仓分红
     */
    VIP1_LOCK_RETURN_HK(30,"直推VIP1會員費鎖倉分紅"),

    /**
     * 直推VIP2会员费返佣
     */
    VIP2_BUY_RETURN_HK(31,"直推VIP2會員費返傭"),

    /**
     * 直推VIP2会员费锁仓分红
     */
    VIP2_LOCK_RETURN_HK(32,"直推VIP2會員費鎖倉分紅"),

    /**
     * 直推VIP3会员费返佣
     */
    VIP3_BUY_RETURN_HK(33,"直推VIP3會員費返傭"),

    /**
     * 直推VIP3会员费锁仓分红
     */
    VIP3_LOCK_RETURN_HK(34,"直推VIP3會員費鎖倉分紅"),

    /**
     * 直推经纪人会员费返佣
     */
    AGENT_BUY_RETURN_HK(35,"直推經紀人會員費返傭"),

    /**
     * 直推经纪人会员费锁仓分红
     */
    AGENT_LOCK_RETURN_HK(36,"直推經紀人會員費鎖倉分紅"),

    COMMISSION_REBATE(37,"会员返佣"),

    COMMISSION_REBATE_EN(38,"Commission rebate for members"),

    COMMISSION_REBATE_KO(39,"회원의 하인"),

    COMMISSION_REBATE_HK(40,"會員返傭"),


    ;












    private final int code;

    private final String name;

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
