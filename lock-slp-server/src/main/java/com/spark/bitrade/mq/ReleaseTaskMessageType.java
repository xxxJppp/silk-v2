package com.spark.bitrade.mq;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 加速释放任务消息类型
 *
 * @author yangch
 * @time 2019-06-09 21:38:15
 *  
 */
@AllArgsConstructor
@Getter
public enum ReleaseTaskMessageType implements BaseEnum {

//    /**
//     * 生成直接推荐人加速释放任务
//     */
//    BUILD_INVITER_RELEASE_TASK("生成直接推荐人加速释放任务"),
    /**
     * 推荐人社区奖励加速释放任务
     */
    HANDLE_COMMUNITY_RELEASE_TASK("推荐人社区奖励加速释放任务"),
    /**
     * 推荐人分享收益加速释放任务
     */
    HANDLE_SHARE_RELEASE_TASK("推荐人分享收益加速释放任务"),

    /**
     * 太阳等级专享加速释放任务
     */
    HANDLE_SUN_RELEASE_TASK("推荐人分享收益加速释放任务");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal(){
        return this.ordinal();
    }
}
