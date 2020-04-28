package com.spark.bitrade.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态：0=已取消/1=未付款/2=已付款/3=已完成/4=申诉中
 * @author ss
 * @date 2017年12月23日
 */
@AllArgsConstructor
@Getter
public enum OrderStatus {

    /**
     * 0 已取消
     */
    CANCELLED(0,"已取消"),
    /**
     * 1 未付款
     */
    NONPAYMENT(1,"未付款"),
    /**
     * 2 已付款
     */
    PAID(2,"已付款"),
    /**
     * 3 已完成
     */
    COMPLETED(3,"已完成"),
    /**
     * 4 申诉中
     */
    APPEAL(4,"申诉中"),
    /**
     * 5 已关闭
     */
    CLOSE(5,"已关闭");

    private Integer code;
    private String msg;

    /**
     * 通过枚举值码查找枚举值。
     *
     * @param code 查找枚举值的枚举值码。
     * @return 枚举值码对应的枚举值。
     * @throws IllegalArgumentException 如果 code 没有对应的 枚举 。
     */
    public static OrderStatus find(Integer code) {
        for (OrderStatus eu : values()) {
            if (eu.getCode().equals(code)) {
                return eu;
            }
        }
        return null;
    }
}
