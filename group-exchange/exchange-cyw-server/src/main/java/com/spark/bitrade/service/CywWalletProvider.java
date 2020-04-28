package com.spark.bitrade.service;

import com.spark.bitrade.entity.ExchangeCywOrder;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.trans.OrderSettleDelta;
import com.spark.bitrade.trans.TradeSettleDelta;

/**
 * 钱包服务提供接口
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/3 16:52
 */
public interface CywWalletProvider {

    /**
     * 冻结余额
     *
     * @param order 订单
     * @return bool
     */
    boolean freezeBalance(ExchangeCywOrder order);

    /**
     * 退还冻结余额
     *
     * @param delta 详情
     * @param type  类型
     * @return bool
     */
    boolean giveBackFrozen(OrderSettleDelta delta, WalTradeType type);

    /**
     * 交易结算
     *
     * @param delta 详情
     * @return bool
     */
    boolean tradeSettle(TradeSettleDelta delta);

}
