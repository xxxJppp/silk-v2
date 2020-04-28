package com.spark.bitrade.mq;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 构建任务消息类型
 *
 * @author yangch
 * @time 2019-06-09 21:38:15
 *  
 */
@AllArgsConstructor
@Getter
public enum BuildTaskMessageType implements BaseEnum {
    /**
     * 处理锁仓数据任务
     */
    LOCK_SLP_TASK("处理锁仓数据任务"),
    /**
     * 预处理锁仓数据任务
     */
    LOCK_SLP_PRE_APPEND_TASK("预处理加仓数据任务"),
    /**
     * 处理锁仓数据任务
     */
    LOCK_SLP_POST_APPEND_TASK("处理加仓数据任务"),
    /**
     * 生成参与者更新任务
     */
    BUILD_ACTOR_UPDATE_TASK("生成参与者更新任务"),
    /**
     * 生成直接推荐人的更新任务
     */
    BUILD_INVITER_UPDATE_TASK("生成直接推荐人的更新任务"),

    /**
     * 生成直接推荐人加速释放任务
     */
    BUILD_INVITER_RELEASE_TASK("生成直接推荐人加速释放任务");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
