package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/***
  * 采集事件类型（注：粗粒度的事件）
  * @author yangch
  * @time 2018.11.01 14:40
  */
@AllArgsConstructor
@Getter
public enum CollectActionEventType implements BaseEnum {
    /**
     * 无 0
     */
    NONE(0, "NONE", "无"),
    /**
     * 创建C2C订单事件 1
     */
    OTC_ADD_ORDER(1, "OTC", "创建C2C订单事件"),
    /**
     * C2C订单申诉事件 2
     */
    OTC_APPEAL_ORDER(2, "OTC", "C2C订单申诉事件"),
    /**
     * 1.3 实现
     */
    OTC_CANCEL_ORDER(3, "OTC", "C2C撤销订单事件"),
    OTC_PAY_CASH(4, "OTC", "C2C订单付款事件"),
    OTC_PAY_COIN(5, "OTC", "C2C订单放币事件"),

    //币币交易相关事件：下单、撤单
    EXCHANGE_ADD_ORDER(6, "EXCHANGE", "币币交易下单事件"),
    EXCHANGE_CANCEL_ORDER(7, "EXCHANGE", "币币交易撤销订单事件"),

    //充提币事件
    COIN_IN(8, "COIN", "充值事件"),
    COIN_OUT(9, "COIN", "提币事件"),

    //登陆、登出事件
    LOGIN(10, "UC", "登陆事件"),
    LOGOUT(11, "UC", "登出事件"),
    /**
     * 商家认证审核通过 12
     */
    BUSINESS_APPROVE(12, "BUSINESS", "商家认证审核通过"),
    /**
     * C2C订单即将过期 13
     */
    EXPIRE_REMIND_ORDER(13, "OTC", "C2C订单即将过期"),
    /**
     * C2C订单申诉处理完成事件 14
     */
    OTC_APPEAL_ORDER_COMPLETE(14, "OTC", "C2C订单申诉处理完成事件");
    /**
     * 编码
     */
    @Setter
    private int code;

    /**
     * 分类
     */
    @Setter
    private String cnType;

    /**
     * 描述
     */
    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return code;
//        return ordinal();
    }
}
