package com.spark.bitrade.mq;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 更新任务消息类型
 *
 * @author yangch
 * @time 2019-06-09 21:38:15
 *  
 */
@AllArgsConstructor
@Getter
public enum UpdateTaskMessageType implements BaseEnum {
//    /**
//     * 生成参与者更新任务
//     */
//    BUILD_ACTOR_UPDATE_TASK("生成参与者更新任务"),
//    /**
//     * 生成直接推荐人的更新任务
//     */
//    BUILD_INVITER_UPDATE_TASK("生成直接推荐人的更新任务"),
    /**
     * 处理更新任务
     */
    HANDLE_UPDATE_TASK("处理更新任务");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }
}
