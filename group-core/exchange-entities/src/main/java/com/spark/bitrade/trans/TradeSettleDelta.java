package com.spark.bitrade.trans;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.TradeBehaviorType;
import com.spark.bitrade.entity.ExchangeCoin;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeTrade;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  交易结算
 *
 * @author young
 * @time 2019.09.03 15:12
 */
@Data
public class TradeSettleDelta {
    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 关联订单号
     */
    private String refOrderId;

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 成交数量
     */
    private BigDecimal amount;

    /**
     * 成交额
     */
    private BigDecimal turnover;

    /**
     * 成交价
     */
    private BigDecimal price;

    /**
     * 基币USD汇率
     */
    private BigDecimal baseUsdRate;

    /**
     * 收入币
     */
    private String incomeSymbol;
    /**
     * 收入币数量
     */
    private BigDecimal incomeCoinAmount;

    /**
     * 支出币
     */
    private String outcomeSymbol;

    /**
     * 支出币数量
     */
    private BigDecimal outcomeCoinAmount;

    /**
     * 收入手续费（tips：未优惠的手续费）
     */
    private BigDecimal fee;

    /**
     * 收入真实手续费
     */
    private BigDecimal realFee;
    /**
     * 收入优惠的手续费（买入订单收取coin,卖出订单收取baseCoin）
     */
    private BigDecimal feeDiscount;

    /**
     * 交易类型
     */
    private TradeBehaviorType type;

    @Deprecated
    public static TradeSettleDelta settle(final ExchangeOrder order, final ExchangeTrade trade,
                                          final ExchangeCoin exchangeCoin, final DiscountRate discountRate) {
        return null;
    }

    /**
     * 结算
     *
     * @param settleDirection 结算方向
     * @param order           订单信息
     * @param trade           撮合信息
     * @param exchangeCoin    交易对配置信息
     * @param discountRate    用户折扣率
     * @return
     */
    public static TradeSettleDelta settle(final ExchangeOrderDirection settleDirection,
                                          final ExchangeOrder order, final ExchangeTrade trade,
                                          final ExchangeCoin exchangeCoin, final DiscountRate discountRate) {
        TradeSettleDelta delta = new TradeSettleDelta();
        delta.setMemberId(order.getMemberId());
        delta.setOrderId(order.getOrderId());
        delta.setAmount(trade.getAmount());
        delta.setPrice(trade.getPrice());
        delta.setBaseUsdRate(trade.getBaseUsdRate());
        delta.setSymbol(trade.getSymbol());
        // 交易记录交易方向 与 结算方向 相同时 ，则为吃单，否则为挂单
        if (order.getDirection().equals(ExchangeOrderDirection.BUY)) {
            delta.setType(trade.getDirection() == settleDirection ? TradeBehaviorType.BUY_TAKER : TradeBehaviorType.BUY_MAKER);
        } else {
            delta.setType(trade.getDirection() == settleDirection ? TradeBehaviorType.SELL_TAKER : TradeBehaviorType.SELL_MAKER);
        }

        // 设置成交的关联订单号
        if (order.getOrderId().equalsIgnoreCase(trade.getSellOrderId())) {
            delta.setRefOrderId(trade.getBuyOrderId());
        } else {
            delta.setRefOrderId(trade.getSellOrderId());
        }

        if (order.getDirection() == ExchangeOrderDirection.BUY) {
            // 成交额
            delta.setTurnover(trade.getBuyTurnover());
            // 手续费，买入时扣交易币
            delta.setFee(trade.getAmount().multiply(exchangeCoin.getFee()).setScale(exchangeCoin.getCoinScale(), BigDecimal.ROUND_UP));
            // 买币优惠的手续费
            delta.setFeeDiscount(calculateFeeDiscount(delta.getFee(),
                    getBuyDiscountRate(delta, exchangeCoin), getUserBuyDiscountRate(delta, discountRate)));

            // 买入时获得交易币
            delta.setIncomeSymbol(order.getCoinSymbol());
            // 增加可用的币，买入的时候获得交易币(减去手续费)
            delta.setIncomeCoinAmount(trade.getAmount().subtract(delta.getFee().subtract(delta.getFeeDiscount())));

            // 买入时用 基币 支付
            delta.setOutcomeSymbol(order.getBaseSymbol());
            // 扣除支付的币，买入的时候算成交额（基本）
            delta.setOutcomeCoinAmount(delta.getTurnover());
        } else {
            // 成交额
            delta.setTurnover(trade.getSellTurnover());
            // 手续费，买入时扣基币
            delta.setFee(delta.getTurnover().multiply(exchangeCoin.getFee()).setScale(exchangeCoin.getBaseCoinScale(), BigDecimal.ROUND_UP));
            // 卖币优惠的手续费
            delta.setFeeDiscount(calculateFeeDiscount(delta.getFee(),
                    getSellDiscountRate(delta, exchangeCoin), getUserSellDiscountRate(delta, discountRate)));

            // 卖出时获得基币
            delta.setIncomeSymbol(order.getBaseSymbol());
            // 增加可用的币,卖出的时候获得基币(减去手续费)
            delta.setIncomeCoinAmount(delta.getTurnover().subtract(delta.getFee().subtract(delta.getFeeDiscount())));

            // 买入时用 交易币 支付
            delta.setOutcomeSymbol(order.getCoinSymbol());
            // 扣除支付的币，卖出的算成交量（交易币）
            delta.setOutcomeCoinAmount(trade.getAmount());
        }
        // 设置真实手续费
        delta.setRealFee(delta.getFee().subtract(delta.getFeeDiscount()));

        return delta;
    }

    /**
     * 计算优惠的手续费
     *
     * @param fee             手续费
     * @param feeDiscountRate 交易对配置的折扣率
     * @param discountRate    用户折扣率
     * @return
     */
    private static BigDecimal calculateFeeDiscount(BigDecimal fee, BigDecimal feeDiscountRate, BigDecimal discountRate) {
        BigDecimal feeDiscount;

        if (feeDiscountRate.compareTo(BigDecimal.ONE) >= 0) {
            //手续费全部优惠
            feeDiscount = fee;
        } else {
            //卖币优惠手续费数量
            feeDiscount = fee.multiply(feeDiscountRate).setScale(fee.scale(), BigDecimal.ROUND_DOWN);
            //优惠后的当前手续费
            BigDecimal remainingFee = fee.subtract(feeDiscount);
            //计算 当前会员可优惠手续费数量
            BigDecimal memberFeeDiscount = remainingFee.multiply(discountRate).setScale(fee.scale(), BigDecimal.ROUND_DOWN);

            feeDiscount = feeDiscount.add(memberFeeDiscount).setScale(fee.scale(), BigDecimal.ROUND_DOWN);
        }

        return feeDiscount;
    }


    /**
     * 汇率应该在0到1之间
     *
     * @param rate
     * @return
     */
    private static BigDecimal getRate(BigDecimal rate) {
        if (rate.compareTo(BigDecimal.ONE) > 0) {
            return BigDecimal.ONE;
        } else if (rate.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        } else {
            return rate;
        }
    }

    /**
     * 买入折扣率
     *
     * @param delta
     * @param exchangeCoin
     * @return
     */
    private static BigDecimal getBuyDiscountRate(TradeSettleDelta delta, ExchangeCoin exchangeCoin) {
        // 根据交易行为获取吃单、挂单的折扣率
        if (delta.getType().equals(TradeBehaviorType.BUY_TAKER)) {
            return getRate(exchangeCoin.getFeeBuyDiscount());
        }

        return getRate(exchangeCoin.getFeeEntrustBuyDiscount());
    }

    /**
     * 卖出折扣率
     *
     * @param delta
     * @param exchangeCoin
     * @return
     */
    private static BigDecimal getSellDiscountRate(TradeSettleDelta delta, ExchangeCoin exchangeCoin) {
        // 根据交易行为获取吃单、挂单的折扣率
        if (delta.getType().equals(TradeBehaviorType.SELL_TAKER)) {
            return getRate(exchangeCoin.getFeeSellDiscount());
        }

        return getRate(exchangeCoin.getFeeEntrustSellDiscount());
    }


    /**
     * 买入折扣率
     *
     * @param delta
     * @param discountRate
     * @return
     */
    private static BigDecimal getUserBuyDiscountRate(TradeSettleDelta delta, DiscountRate discountRate) {
        // 根据交易行为获取吃单、挂单的折扣率
        if (delta.getType().equals(TradeBehaviorType.BUY_TAKER)) {
            return discountRate.getBuyDiscount();
        }

        return discountRate.getEntrustBuyDiscount();
    }

    /**
     * 卖出折扣率
     *
     * @param delta
     * @param discountRate
     * @return
     */
    private static BigDecimal getUserSellDiscountRate(TradeSettleDelta delta, DiscountRate discountRate) {
        // 根据交易行为获取吃单、挂单的折扣率
        if (delta.getType().equals(TradeBehaviorType.SELL_TAKER)) {
            return discountRate.getSellDiscount();
        }

        return discountRate.getEntrustSellDiscount();
    }
}
