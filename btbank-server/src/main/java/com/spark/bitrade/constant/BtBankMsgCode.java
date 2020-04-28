package com.spark.bitrade.constant;

import com.spark.bitrade.constants.MsgCode;

/**
 * btbank错误码定义
 *
 * @author archx
 * @since 2019/5/8 18:05
 */
public enum BtBankMsgCode implements MsgCode {

    /**
     * 定单已经被抢或被派出
     */
    ORDERS_HAVE_LOOTED_OR_DISPATCHED(71000, "ORDERS_HAVE_LOOTED_OR_DISPATCHED"),

    /**
     * 收单异常
     */
    ORDER_RECEIVED_ABNORMAL(71001, "ORDER_RECEIVED_ABNORMAL"),

    /**
     * 转入开关关闭,活动暂时关闭，敬请期待
     */
    TURN_IN_SWITCH_OFF(71002, "TURN_IN_SWITCH_OFF"),

    /**
     * 单次转入不能低于最小限额
     */
    BELOW_THE_MINIMUM(71003, "BELOW_THE_MINIMUM"),

    /**
     * 抢单失败
     */
    FAILED_TO_SNATCH_THE_ORDER(71004, "FAILED_TO_SNATCH_THE_ORDER"),

    /**
     * 载入系统配置失败
     */
    FAILED_TO_LOAD_SYSTEM_CONFIGURATION(71005, "FAILED_TO_LOAD_SYSTEM_CONFIGURATION"),

    /**
     * 暂无可解锁订单
     */
    UNLOCKING_ORDERS_ARE_NOT_AVAILABLE(71006, "UNLOCKING_ORDERS_ARE_NOT_AVAILABLE"),

    /**
     * 添加抢单订单记录失败
     */
    FAILED_TO_ADD_GRAB_ORDER_RECORD(71007, "FAILED_TO_ADD_GRAB_ORDER_RECORD"),

    /**
     * 添加抢单本金转出记录失败
     */
    ADD_ORDER_PRINCIPAL_TRANSFER_RECORD_FAILED(71008, "ADD_ORDER_PRINCIPAL_TRANSFER_RECORD_FAILED"),

    /**
     * 添加抢单本金佣金记录失败
     */
    FAILED_TO_RECORD_THE_PRINCIPAL_COMMISSION(71009, "FAILED_TO_RECORD_THE_PRINCIPAL_COMMISSION"),

    /**
     * 余额变动失败
     */
    BALANCE_CHANGE_FAILED(71010, "BALANCE_CHANGE_FAILED"),

    /**
     * 派单开关已关闭
     */
    DELIVERY_SWITCH_IS_OFF(71011, "DELIVERY_SWITCH_IS_OFF"),

    /**
     * 暂无符合派单的订单
     */
    NO_ORDER_IN_LINE_WITH_THE_ORDER(71012, "NO_ORDER_IN_LINE_WITH_THE_ORDER"),

    /**
     * 修改订单失败
     */
    FAILED_TO_MODIFY_THE_ORDER(71013, "FAILED_TO_MODIFY_THE_ORDER"),

    /**
     * 添加订单记录失败
     */
    FAILED_TO_ADD_ORDER_RECORD(71014, "FAILED_TO_ADD_ORDER_RECORD"),

    /**
     * 添加余额记录失败
     */
    FAILED_TO_ADD_BALANCE_RECORD(71015, "FAILED_TO_ADD_BALANCE_RECORD"),

    /**
     * 余额记录可用不足
     */
    INSUFFICIENT_BALANCE_RECORD_AVAILABLE(71016, "INSUFFICIENT_BALANCE_RECORD_AVAILABLE"),

    /**
     * 暂时无法抢单，请稍后再试
     */
    UNABLE_TO_SNATCH_THE_ORDER(71017, "UNABLE_TO_SNATCH_THE_ORDER"),
    /**
     * 订单不存在
     */
    ORDER_NOT_EXIST(71018, "ORDER_NOT_EXIST"),

    /**
     * WebSOCKET 推送订单状态 改变
     */
    WEBSOCKET_ORDER_STATUS_CHANGED(71019, "ORDER_STATUS_CHANGED");


    private final int code;
    private final String message;

    BtBankMsgCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 构建MsgCode类
     *
     * @param code    编码
     * @param message 消息
     * @return
     */
    public static MsgCode of(final int code, final String message) {
        return new MsgCode() {
            @Override
            public int getCode() {
                return code;
            }

            @Override
            public String getMessage() {
                return message;
            }
        };
    }
}
