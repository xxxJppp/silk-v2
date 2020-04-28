package com.spark.bitrade.api.dto;

/**
 * @author Administrator
 * @time 2019.10.24 15:43
 */
public enum MinerBalanceTransactionType {

    /**
     * 类型：1  转入，2 抢单本金转出，3 抢单佣金转入，4 抢单佣金转出，5 派单本金转出，6 派单佣金转入，7 派单佣金转出，8 转出，9 固定佣金转出，10 固定佣金转入，11 抢单锁仓，12 派单锁仓
     */


    TRANSFER_IN(1, "转入"),
    GRAB_PRINCIPAL_TRANSFER_OUT(2, "抢单本金转出"),
    GRAB_COMMISSION_TRANSFER_IN(3, "抢单佣金转入"),
    GRAB_COMMISSION_TRANSFER_OUT(4, "抢单佣金转出"),
    DISPATCH_PRINCIPAL_TRANSFER_OUT(5, "派单本金转出"),
    DISPATCH_COMMISSION_TRANSFER_IN(6, "派单佣金转入"),
    DISPATCH_COMMISSION_TRANSFER_OUT(7, "派单佣金转出"),
    TRANSFER_OUT(8, "转出"),
    FIEXD_COMMISSION_TRANSFER_OUT(9, "固定佣金转出"),
    FIEXD_COMMISSION_TRANSFER_IN(10, "固定佣金转入"),
    GRABBED_LOCKS(11, "抢单锁仓"),
    DISPATCHED_LOCKS(12, "派单锁仓"),
    ;


    int value;
    String name;


    MinerBalanceTransactionType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }


}
