package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Zhang Jinwei
 * @date 2018年02月26日
 */
@AllArgsConstructor
@Getter
public enum LockType implements BaseEnum {
    /**
     * 0=商家保证金
     */

    DEPOSIT("商家保证金"),
    /**
     * 1=手动锁仓
     */
    HANDLE_LOCK("手动锁仓"),
    /**
     * 2=锁仓活动
     */
    LOCK_ACTIVITY("锁仓活动"),
    /**
     * 3=理财锁仓
     */
    FINANCIAL_LOCK("理财锁仓"),
    /**
     * 4=SLB节点产品
     */
    QUANTIFY("SLB节点产品"),

    /**
     * 5=STO锁仓
     */
    STO("STO锁仓"),
    /**
     * 6=STO增值计划
     */
    STO_CNYT("STO增值计划"),
    /**
     * 7=IEO锁仓
     */
    IEO("IEO锁仓"),
    /**
     * 8=金钥匙活动
     */
    GOLD_KEY("金钥匙活动"),
    /**
     * 9=扩展1
     */
    ext1("扩展1"),
    /**
     * 10=SLP锁仓活动
     */
    LOCK_SLP("SLP锁仓活动"),
    /**
     * 11=超级合伙人
     */
    SUPER_PARTNER("超级合伙人"),
    /**
     * 12=扩展2
     */
    ext2("扩展2"),
    /**
     * 13=孵化区锁仓
     */
    INCUBOTORS_LOCK("孵化区锁仓"),

    /**
     * 14=类似于UTT的锁仓
     */
    NEW_LIKE_UTT("类似于UTT的锁仓"),

    BY_MEMBER_LOCK("购买会员锁仓"),

    ext5("扩展5")
    ;

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
