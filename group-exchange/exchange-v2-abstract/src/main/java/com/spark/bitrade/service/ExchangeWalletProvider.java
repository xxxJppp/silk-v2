package com.spark.bitrade.service;

import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeWalletWalRecord;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.trans.OrderSettleDelta;
import com.spark.bitrade.trans.TradeSettleDelta;
import com.spark.bitrade.trans.Tuple2;

/**
 * 钱包服务提供接口
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/3 16:52
 */
public interface ExchangeWalletProvider {

    /**
     * 冻结余额
     *
     * @param order 订单
     * @return bool
     */
    boolean freezeBalance(ExchangeOrder order);

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
     * @return tuple2
     */
    Tuple2<ExchangeWalletWalRecord, ExchangeWalletWalRecord> tradeSettle(TradeSettleDelta delta);

}
