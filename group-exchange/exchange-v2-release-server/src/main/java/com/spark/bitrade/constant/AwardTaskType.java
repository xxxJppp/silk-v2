package com.spark.bitrade.constant;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 推荐人奖励任务表任务类型：0=手续费返佣、1=直推用户买币累计奖励
 *
 * @author young
 * @time 2019.12.16 15:08
 */
@AllArgsConstructor
@Getter
public enum AwardTaskType implements BaseEnum, IEnum {
    /**
     * 0=手续费返佣
     */
    AWARD_FOR_FEE("手续费返佣"),
    /**
     * 1=直推用户买币累计奖励
     */
    AWARD_FOR_ACCUMULATION("直推用户买币累计奖励");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
