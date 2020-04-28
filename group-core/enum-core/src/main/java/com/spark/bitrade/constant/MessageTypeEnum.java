package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 消息类型枚举类
 *
 */
@AllArgsConstructor
@Getter
public enum MessageTypeEnum implements BaseEnum {

    /**
     * 0:提醒对方刷新订单页面
     */
    NOTICE("确认付款"),
    /**
     * 1:聊天
     */
    NORMAL_CHAT("正常聊天"),
    /**
     * 2:系统消息
     */
    SYSTEM_MES("系统消息"),
    /**
     * 3:OTC事件流转消息
     */
    OTC_EVENT("OTC事件消息"),
    /**
     * 4:线上订单流转消息
     */
    ONLINE_EVENT("线上订单事件消息"),
    /**
     * 5:商家认证/取消流转消息
     */
    BUSINESS_EVENT("商家认证/取消事件"),
    /**
     * 6:申诉发起/取消事件
     */
    APPEAL_EVENT("申诉发起/取消事件"),

    UNKNOWN("未知");

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }
}
