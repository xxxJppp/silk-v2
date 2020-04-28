package com.spark.bitrade.entity.constants;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 记录交易类型
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/5 11:31
 */
@AllArgsConstructor
@Getter
public enum WalTradeType implements BaseEnum {

    // 0
    NONE("未知"),

    // 1 划转： + 转入，- 转出
    TRANSFER("划拨转入"),

    // 2 下单：只有冻结
    PLACE_ORDER("下单"),

    // 3 撮合交易：减冻结，加余额
    TURNOVER("撮合交易"),

    // 4 撤单：退回冻结
    CANCEL_ORDER("撤单"),

    // 5 成交：退回冻结
    DEAL("成交退回冻结"),

    // 6 撤单：部分成交
    PART_CANCEL_ORDER("部分成交撤单"),

    // 7 推荐返佣奖励
    PROMOTION_REWARD("返佣"),

    // 8 币币划转
    TRANSFER_EXCHANGE("币币划转"),

    // 9 OTC划转
    TRANSFER_OTC("OTC划转"),

    // 10 活期宝账户划转
    TRANSFER_HQB("活期宝账户划转"),
    // 11 闪兑
    EXCHANGE_FAST("闪兑"),
    // 12 冻结
    FREEZE("冻结"),
    // 13 释放
    RELEASE("释放"),

    // 14 币币交易奖励
    EXCHANGE_REWARD("币币交易奖励"),
    // 15 币币交易消耗
    EXCHANGE_GAS_FREEZE("币币交易燃料冻结"),
    // 16 币币交易消耗
    EXCHANGE_GAS("币币交易消耗"),
    // 17 币币交易归还冻结的燃料费
    EXCHANGE_GAS_GIVE_BACK("币币交易冻结归还"),


    ;

    @Setter
    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }

    public static WalTradeType of(int ord) {
        for (WalTradeType value : values()) {
            if (value.ordinal() == ord) {
                return value;
            }
        }
        return NONE;
    }
}
