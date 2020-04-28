package com.spark.bitrade.trans;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.entity.ExchangeOrder;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  订单结算
 *
 * @author young
 * @time 2019.09.03 09:32
 */
@Data
public class OrderSettleDelta {

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 币种，如USDT、BTC
     */
    String coinSymbol;

    /**
     * 订单冻结币数
     */
    BigDecimal frozenBalance;
    /**
     * 成交币数
     */
    BigDecimal dealBalance;

    /**
     * 退还数量
     */
    BigDecimal returnAmount;

    /**
     * 订单结算
     *
     * @param order 订单
     * @return
     */
    public static OrderSettleDelta settle(final ExchangeOrder order) {
        OrderSettleDelta delta = new OrderSettleDelta();
        delta.setOrderId(order.getOrderId());
        delta.setMemberId(order.getMemberId());
        delta.setCoinSymbol(order.getDirection() == ExchangeOrderDirection.BUY ? order.getBaseSymbol() : order.getCoinSymbol());
        delta.setFrozenBalance(order.getFreezeAmount());

        //下单时候冻结的币，实际成交应扣的币，退还数量
        if (order.getDirection() == ExchangeOrderDirection.BUY) {
            //买的时候为 基币，如USDT
            if (order.getType() == ExchangeOrderType.LIMIT_PRICE) {
                if (order.getFreezeAmount() == null || order.getFreezeAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    //按下单时的规则计算冻结余额
                    //ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(order.getSymbol());
                    ///frozenBalance = order.getAmount().multiply(order.getPrice()).setScale(exchangeCoin.getBaseCoinScale(), BigDecimal.ROUND_UP);
                    delta.setFrozenBalance(order.getAmount().multiply(order.getPrice()).setScale(8, BigDecimal.ROUND_UP));
                } else {
                    delta.setFrozenBalance(order.getFreezeAmount());
                }
            } else {
                if (order.getFreezeAmount() == null || order.getFreezeAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    //市价交易的冻结余额为委托数量
                    delta.setFrozenBalance(order.getAmount());
                } else {
                    delta.setFrozenBalance(order.getFreezeAmount());
                }
            }

            delta.setDealBalance(order.getTurnover());
        } else {
            //卖的时候为 当前交易币
            if (order.getFreezeAmount() == null || order.getFreezeAmount().compareTo(BigDecimal.ZERO) <= 0) {
                delta.setFrozenBalance(order.getAmount());
            } else {
                delta.setFrozenBalance(order.getFreezeAmount());
            }

            //卖出时候，成交量 即为对应的成交额
            delta.setDealBalance(order.getTradedAmount());
        }

        //退还金额
        delta.setReturnAmount(delta.getFrozenBalance().subtract(delta.getDealBalance()));

        return delta;
    }
}
