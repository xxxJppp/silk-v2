package com.spark.bitrade.constant;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *  释放任务类型
 *
 * @author young
 * @time 2019.12.16 15:08
 */
@AllArgsConstructor
@Getter
public enum ReleaseTaskType implements BaseEnum, IEnum {
    /**
     * 0=释放锁仓的币
     */
    RELEASE_4_LOCK("锁仓释放"),
    /**
     * 1=释放币币交易买入后冻结的币
     */
    RELEASE_4_FREEZE("冻结释放");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
