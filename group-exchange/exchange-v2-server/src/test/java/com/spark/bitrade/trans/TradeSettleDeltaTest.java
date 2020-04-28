package com.spark.bitrade.trans;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.TradeBehaviorType;
import com.spark.bitrade.entity.ExchangeCoin;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeTrade;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 *  
 *
 * @author young
 * @time 2019.09.04 11:21
 */
public class TradeSettleDeltaTest {

    /**
     * 正常买卖单测试
     */
    @Test
    public void settle1() {
        //测试要点：正常买卖单测试
        ExchangeOrder order = getExchangeOrder();
        ExchangeTrade trade = getExchangeTrade();
        ExchangeCoin exchangeCoin = getExchangeCoin();

        //模拟用户折扣
        //DiscountRate discountRate = new DiscountRate(scale(new BigDecimal(1)), scale(new BigDecimal(0.5)));
        DiscountRate discountRate = new DiscountRate(new BigDecimal(1), new BigDecimal(0.5), new BigDecimal(0.8), new BigDecimal(0.4));

        //买单：交易结算
        order.setDirection(ExchangeOrderDirection.BUY);
        trade.setBuyOrderId("S001");
        trade.setSellOrderId("S002");
        // 买方
        assert_buy(ExchangeOrderDirection.BUY, order, trade, exchangeCoin, discountRate);
        // 买方
        assert_buy(ExchangeOrderDirection.SELL, order, trade, exchangeCoin, discountRate);

        //卖单：交易结算
        order.setDirection(ExchangeOrderDirection.SELL);
        trade.setSellOrderId("S001");
        trade.setBuyOrderId("S002");
        // 买方
        assert_sell(ExchangeOrderDirection.BUY, order, trade, exchangeCoin, discountRate);
        // 买方
        assert_sell(ExchangeOrderDirection.SELL, order, trade, exchangeCoin, discountRate);
    }

    /**
     * exchangeCoin无手续费配置(按exchangeCoin.getFee()扣手续费)
     */
    @Test
    public void settle2() {
        //测试要点：exchangeCoin无手续费配置
        ExchangeOrder order = getExchangeOrder();
        ExchangeTrade trade = getExchangeTrade();
        ExchangeCoin exchangeCoin = getExchangeCoin();

        // 吃单手续费
        exchangeCoin.setFeeBuyDiscount(scale(new BigDecimal(0)));
        exchangeCoin.setFeeSellDiscount(scale(new BigDecimal(0)));
        // 挂单手续费
        exchangeCoin.setFeeEntrustBuyDiscount(scale(new BigDecimal(0)));
        exchangeCoin.setFeeEntrustSellDiscount(scale(new BigDecimal(0)));

        //模拟用户折扣
        DiscountRate discountRate = DiscountRate.getDefaulDiscountRate();


        //买单：无手续费测试
        order.setDirection(ExchangeOrderDirection.BUY);
        trade.setDirection(ExchangeOrderDirection.BUY);
        // 买方，吃单 不优惠手续费
        TradeSettleDelta delta3_b = TradeSettleDelta.settle(ExchangeOrderDirection.BUY, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(198)), delta3_b.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(0)), delta3_b.getFeeDiscount());
        assertEquals(scale(new BigDecimal(2)), delta3_b.getRealFee());

        // 卖方，挂单 不优惠手续费
        TradeSettleDelta delta3_s = TradeSettleDelta.settle(ExchangeOrderDirection.SELL, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(198)), delta3_s.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(0)), delta3_s.getFeeDiscount());
        assertEquals(scale(new BigDecimal(2)), delta3_s.getRealFee());

        //卖单：无手续费测试
        order.setDirection(ExchangeOrderDirection.SELL);
        trade.setDirection(ExchangeOrderDirection.SELL);
        // 买方，挂单 不优惠手续费
        TradeSettleDelta delta4_b = TradeSettleDelta.settle(ExchangeOrderDirection.BUY, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(99)), delta4_b.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(0)), delta4_b.getFeeDiscount());
        assertEquals(scale(new BigDecimal(1)), delta4_b.getRealFee());

        // 卖方，吃单 不优惠手续费
        TradeSettleDelta delta4_s = TradeSettleDelta.settle(ExchangeOrderDirection.SELL, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(99)), delta4_s.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(0)), delta4_s.getFeeDiscount());
        assertEquals(scale(new BigDecimal(1)), delta4_s.getRealFee());
    }

    /**
     * exchangeCoin手续费全免
     */
    @Test
    public void settle3() {
        //测试要点：exchangeCoin手续费全免
        ExchangeOrder order = getExchangeOrder();
        ExchangeTrade trade = getExchangeTrade();
        ExchangeCoin exchangeCoin = getExchangeCoin();
        //手续费全免
        exchangeCoin.setFeeBuyDiscount(scale(new BigDecimal(1)));
        exchangeCoin.setFeeSellDiscount(scale(new BigDecimal(1)));
        exchangeCoin.setFeeEntrustBuyDiscount(scale(new BigDecimal(1)));
        exchangeCoin.setFeeEntrustSellDiscount(scale(new BigDecimal(1)));

        //模拟用户折扣
        DiscountRate discountRate = new DiscountRate(scale(new BigDecimal(1)), scale(new BigDecimal(0.5)));

        //买单：无手续费测试
        order.setDirection(ExchangeOrderDirection.BUY);
        trade.setDirection(ExchangeOrderDirection.BUY);
        // 买方，吃单免手续费
        TradeSettleDelta delta3_b = TradeSettleDelta.settle(ExchangeOrderDirection.BUY, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(200)), delta3_b.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(2)), delta3_b.getFeeDiscount());
        assertEquals(scale(new BigDecimal(0)), delta3_b.getRealFee());

        // 卖方，挂单免手续费
        TradeSettleDelta delta3_s = TradeSettleDelta.settle(ExchangeOrderDirection.SELL, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(200)), delta3_s.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(2)), delta3_s.getFeeDiscount());
        assertEquals(scale(new BigDecimal(0)), delta3_s.getRealFee());

        //卖单：无手续费测试
        order.setDirection(ExchangeOrderDirection.SELL);
        trade.setDirection(ExchangeOrderDirection.SELL);
        // 买方，挂单免手续费
        TradeSettleDelta delta4_b = TradeSettleDelta.settle(ExchangeOrderDirection.BUY, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(100)), delta4_b.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(1)), delta4_b.getFeeDiscount());
        assertEquals(scale(new BigDecimal(0)), delta4_b.getRealFee());

        // 卖方，吃单免手续费
        TradeSettleDelta delta4_s = TradeSettleDelta.settle(ExchangeOrderDirection.SELL, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(100)), delta4_s.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(1)), delta4_s.getFeeDiscount());
        assertEquals(scale(new BigDecimal(0)), delta4_s.getRealFee());
    }

    /**
     * 用户手续费全免
     */
    @Test
    public void settle4() {
        //测试要点：exchangeCoin手续费全免
        ExchangeOrder order = getExchangeOrder();
        ExchangeTrade trade = getExchangeTrade();
        ExchangeCoin exchangeCoin = getExchangeCoin();
        //手续费全免
        exchangeCoin.setFeeBuyDiscount(scale(new BigDecimal(0)));
        exchangeCoin.setFeeSellDiscount(scale(new BigDecimal(0)));
        exchangeCoin.setFeeEntrustBuyDiscount(scale(new BigDecimal(0)));
        exchangeCoin.setFeeEntrustSellDiscount(scale(new BigDecimal(0)));

        //模拟用户折扣
        DiscountRate discountRate = new DiscountRate(scale(new BigDecimal(1)), scale(new BigDecimal(1)),
                scale(new BigDecimal(1)), scale(new BigDecimal(1)));

        //买单：无手续费测试
        order.setDirection(ExchangeOrderDirection.BUY);
        trade.setDirection(ExchangeOrderDirection.BUY);
        // 买方，吃单免手续费
        TradeSettleDelta delta3_b = TradeSettleDelta.settle(ExchangeOrderDirection.BUY, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(200)), delta3_b.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(2)), delta3_b.getFeeDiscount());
        assertEquals(scale(new BigDecimal(0)), delta3_b.getRealFee());

        // 卖方，挂单免手续费
        TradeSettleDelta delta3_s = TradeSettleDelta.settle(ExchangeOrderDirection.SELL, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(200)), delta3_s.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(2)), delta3_s.getFeeDiscount());
        assertEquals(scale(new BigDecimal(0)), delta3_s.getRealFee());

        //卖单：无手续费测试
        order.setDirection(ExchangeOrderDirection.SELL);
        trade.setDirection(ExchangeOrderDirection.SELL);
        // 买方，挂单免手续费
        TradeSettleDelta delta4_b = TradeSettleDelta.settle(ExchangeOrderDirection.BUY, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(100)), delta4_b.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(1)), delta4_b.getFeeDiscount());
        assertEquals(scale(new BigDecimal(0)), delta4_b.getRealFee());

        // 卖方，吃单免手续费
        TradeSettleDelta delta4_s = TradeSettleDelta.settle(ExchangeOrderDirection.SELL, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(100)), delta4_s.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(1)), delta4_s.getFeeDiscount());
        assertEquals(scale(new BigDecimal(0)), delta4_s.getRealFee());
    }

    /**
     * 用户手续费5折
     */
    @Test
    public void settle5() {
        //测试要点：exchangeCoin手续费全免
        ExchangeOrder order = getExchangeOrder();
        ExchangeTrade trade = getExchangeTrade();
        ExchangeCoin exchangeCoin = getExchangeCoin();
        //手续费全免
        exchangeCoin.setFeeBuyDiscount(scale(new BigDecimal(0)));
        exchangeCoin.setFeeSellDiscount(scale(new BigDecimal(0)));
        exchangeCoin.setFeeEntrustBuyDiscount(scale(new BigDecimal(0)));
        exchangeCoin.setFeeEntrustSellDiscount(scale(new BigDecimal(0)));

        //模拟用户折扣
        DiscountRate discountRate = new DiscountRate(scale(new BigDecimal(0.5)), scale(new BigDecimal(0.5)),
                scale(new BigDecimal(0.5)), scale(new BigDecimal(0.5)));

        //买单：5折手续费测试
        order.setDirection(ExchangeOrderDirection.BUY);
        trade.setDirection(ExchangeOrderDirection.BUY);
        // 买方，吃单5折优惠
        TradeSettleDelta delta3_b = TradeSettleDelta.settle(ExchangeOrderDirection.BUY, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(199)), delta3_b.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(1)), delta3_b.getFeeDiscount());
        assertEquals(scale(new BigDecimal(1)), delta3_b.getRealFee());

        // 卖方，挂单5折优惠
        TradeSettleDelta delta3_s = TradeSettleDelta.settle(ExchangeOrderDirection.SELL, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(199)), delta3_s.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(1)), delta3_s.getFeeDiscount());
        assertEquals(scale(new BigDecimal(1)), delta3_s.getRealFee());


        //卖单：5折手续费测试
        order.setDirection(ExchangeOrderDirection.SELL);
        trade.setDirection(ExchangeOrderDirection.SELL);
        // 买方，挂单5折优惠
        TradeSettleDelta delta4_b = TradeSettleDelta.settle(ExchangeOrderDirection.BUY, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(99.5)), delta4_b.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(0.5)), delta4_b.getFeeDiscount());
        assertEquals(scale(new BigDecimal(0.5)), delta4_b.getRealFee());

        // 卖方，吃单5折优惠
        TradeSettleDelta delta4_s = TradeSettleDelta.settle(ExchangeOrderDirection.SELL, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(99.5)), delta4_s.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(0.5)), delta4_s.getFeeDiscount());
        assertEquals(scale(new BigDecimal(0.5)), delta4_s.getRealFee());
    }

    /**
     * 系统手续费5折优惠
     */
    @Test
    public void settle6() {
        //测试要点：exchangeCoin手续费全免
        ExchangeOrder order = getExchangeOrder();
        ExchangeTrade trade = getExchangeTrade();
        ExchangeCoin exchangeCoin = getExchangeCoin();
        //手续费全免
        exchangeCoin.setFeeBuyDiscount(scale(new BigDecimal(0.5)));
        exchangeCoin.setFeeSellDiscount(scale(new BigDecimal(0.5)));
        exchangeCoin.setFeeEntrustBuyDiscount(scale(new BigDecimal(0.5)));
        exchangeCoin.setFeeEntrustSellDiscount(scale(new BigDecimal(0.5)));

        //模拟用户折扣
        DiscountRate discountRate = DiscountRate.getDefaulDiscountRate();

        //买单：5折手续费测试
        order.setDirection(ExchangeOrderDirection.BUY);
        trade.setDirection(ExchangeOrderDirection.BUY);
        // 买方，吃单5折优惠
        TradeSettleDelta delta3_b = TradeSettleDelta.settle(ExchangeOrderDirection.BUY, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(199)), delta3_b.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(1)), delta3_b.getFeeDiscount());
        assertEquals(scale(new BigDecimal(1)), delta3_b.getRealFee());

        // 卖方，挂单5折优惠
        TradeSettleDelta delta3_s = TradeSettleDelta.settle(ExchangeOrderDirection.SELL, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(199)), delta3_s.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(1)), delta3_s.getFeeDiscount());
        assertEquals(scale(new BigDecimal(1)), delta3_s.getRealFee());

        //卖单：5折手续费测试
        order.setDirection(ExchangeOrderDirection.SELL);
        trade.setDirection(ExchangeOrderDirection.SELL);
        // 买方，挂单5折优惠
        TradeSettleDelta delta4_b = TradeSettleDelta.settle(ExchangeOrderDirection.BUY, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(99.5)), delta4_b.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(0.5)), delta4_b.getFeeDiscount());
        assertEquals(scale(new BigDecimal(0.5)), delta4_b.getRealFee());

        // 卖方，吃单5折优惠
        TradeSettleDelta delta4_s = TradeSettleDelta.settle(ExchangeOrderDirection.SELL, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(99.5)), delta4_s.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(0.5)), delta4_s.getFeeDiscount());
        assertEquals(scale(new BigDecimal(0.5)), delta4_s.getRealFee());
    }

    /**
     * 系统手续费 挂单5折优惠，吃单不优惠
     */
    @Test
    public void settle7() {
        //测试要点：exchangeCoin手续费全免
        ExchangeOrder order = getExchangeOrder();
        ExchangeTrade trade = getExchangeTrade();
        ExchangeCoin exchangeCoin = getExchangeCoin();
        //手续费全免
        exchangeCoin.setFeeBuyDiscount(scale(new BigDecimal(0)));
        exchangeCoin.setFeeSellDiscount(scale(new BigDecimal(0)));
        exchangeCoin.setFeeEntrustBuyDiscount(scale(new BigDecimal(0.5)));
        exchangeCoin.setFeeEntrustSellDiscount(scale(new BigDecimal(0.5)));

        //模拟用户折扣
        DiscountRate discountRate = DiscountRate.getDefaulDiscountRate();

        //买单：5折手续费测试
        order.setDirection(ExchangeOrderDirection.BUY);
        trade.setDirection(ExchangeOrderDirection.BUY);

        // 买方，吃单不优惠
        TradeSettleDelta delta3_b = TradeSettleDelta.settle(ExchangeOrderDirection.BUY, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(198)), delta3_b.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(0)), delta3_b.getFeeDiscount());
        assertEquals(scale(new BigDecimal(2)), delta3_b.getRealFee());

        // 卖方，挂单5折优惠
        TradeSettleDelta delta3_s = TradeSettleDelta.settle(ExchangeOrderDirection.SELL, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(199)), delta3_s.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(1)), delta3_s.getFeeDiscount());
        assertEquals(scale(new BigDecimal(1)), delta3_s.getRealFee());


        //卖单：5折手续费测试
        order.setDirection(ExchangeOrderDirection.SELL);
        trade.setDirection(ExchangeOrderDirection.SELL);

        // 买方，挂单5折优惠
        TradeSettleDelta delta4_b = TradeSettleDelta.settle(ExchangeOrderDirection.BUY, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(99.5)), delta4_b.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(0.5)), delta4_b.getFeeDiscount());
        assertEquals(scale(new BigDecimal(0.5)), delta4_b.getRealFee());

        // 卖方，吃单不优惠
        TradeSettleDelta delta4_s = TradeSettleDelta.settle(ExchangeOrderDirection.SELL, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(99)), delta4_s.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(0)), delta4_s.getFeeDiscount());
        assertEquals(scale(new BigDecimal(1)), delta4_s.getRealFee());
    }

    /**
     * 用户手续费 挂单5折优惠，吃单不优惠
     */
    @Test
    public void settle8() {
        //测试要点：exchangeCoin手续费全免
        ExchangeOrder order = getExchangeOrder();
        ExchangeTrade trade = getExchangeTrade();
        ExchangeCoin exchangeCoin = getExchangeCoin();
        //手续费全免
        exchangeCoin.setFeeBuyDiscount(scale(new BigDecimal(0)));
        exchangeCoin.setFeeSellDiscount(scale(new BigDecimal(0)));
        exchangeCoin.setFeeEntrustBuyDiscount(scale(new BigDecimal(0)));
        exchangeCoin.setFeeEntrustSellDiscount(scale(new BigDecimal(0)));

        //模拟用户折扣
        DiscountRate discountRate = new DiscountRate(scale(new BigDecimal(0)), scale(new BigDecimal(0)),
                scale(new BigDecimal(0.5)), scale(new BigDecimal(0.5)));

        //买单：5折手续费测试
        order.setDirection(ExchangeOrderDirection.BUY);
        trade.setDirection(ExchangeOrderDirection.BUY);

        // 买方，吃单不优惠
        TradeSettleDelta delta3_b = TradeSettleDelta.settle(ExchangeOrderDirection.BUY, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(198)), delta3_b.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(0)), delta3_b.getFeeDiscount());
        assertEquals(scale(new BigDecimal(2)), delta3_b.getRealFee());

        // 卖方，挂单5折优惠
        TradeSettleDelta delta3_s = TradeSettleDelta.settle(ExchangeOrderDirection.SELL, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(199)), delta3_s.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(1)), delta3_s.getFeeDiscount());
        assertEquals(scale(new BigDecimal(1)), delta3_s.getRealFee());


        //卖单：5折手续费测试
        order.setDirection(ExchangeOrderDirection.SELL);
        trade.setDirection(ExchangeOrderDirection.SELL);

        // 买方，挂单5折优惠
        TradeSettleDelta delta4_b = TradeSettleDelta.settle(ExchangeOrderDirection.BUY, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(99.5)), delta4_b.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(0.5)), delta4_b.getFeeDiscount());
        assertEquals(scale(new BigDecimal(0.5)), delta4_b.getRealFee());

        // 卖方，吃单不优惠
        TradeSettleDelta delta4_s = TradeSettleDelta.settle(ExchangeOrderDirection.SELL, order, trade, exchangeCoin, discountRate);
        assertEquals(scale(new BigDecimal(99)), delta4_s.getIncomeCoinAmount());
        assertEquals(scale(new BigDecimal(0)), delta4_s.getFeeDiscount());
        assertEquals(scale(new BigDecimal(1)), delta4_s.getRealFee());
    }


    //模拟币种手续费配置
    private ExchangeCoin getExchangeCoin() {
        ExchangeCoin exchangeCoin = new ExchangeCoin();
        exchangeCoin.setFee(scale(new BigDecimal(0.01)));
        // 吃单手续费，5折优化
        exchangeCoin.setFeeBuyDiscount(scale(new BigDecimal(0.5)));
        exchangeCoin.setFeeSellDiscount(scale(new BigDecimal(0.5)));
        // 挂单手续费，4折优化
        exchangeCoin.setFeeEntrustBuyDiscount(scale(new BigDecimal(0.4)));
        exchangeCoin.setFeeEntrustSellDiscount(scale(new BigDecimal(0.4)));
        exchangeCoin.setBaseCoinScale(4);
        exchangeCoin.setCoinScale(4);
        return exchangeCoin;
    }

    //模拟交易明细
    private ExchangeTrade getExchangeTrade() {
        ExchangeTrade trade = new ExchangeTrade();
        trade.setSymbol("SLU/USDT");
        trade.setAmount(scale(new BigDecimal(200)));
        trade.setBuyTurnover(scale(new BigDecimal(100)));
        trade.setSellTurnover(scale(new BigDecimal(100)));
        trade.setPrice(scale(new BigDecimal(3)));
        return trade;
    }

    //模拟订单
    private ExchangeOrder getExchangeOrder() {
        ExchangeOrder order = new ExchangeOrder();
        order.setOrderId("S001");
        order.setMemberId(71639L);
        order.setCoinSymbol("SLU");
        order.setBaseSymbol("USDT");
        return order;
    }

    private void assert_buy(ExchangeOrderDirection settleDirection, ExchangeOrder order, ExchangeTrade trade, ExchangeCoin exchangeCoin, DiscountRate discountRate) {
        trade.setDirection(ExchangeOrderDirection.BUY);

        // 买方
        TradeSettleDelta delta = TradeSettleDelta.settle(settleDirection, order, trade, exchangeCoin, discountRate);

        //断言
        assertEquals(delta.getMemberId().longValue(), 71639L);
        assertEquals("S001", delta.getOrderId());
        assertEquals("S002", delta.getRefOrderId());
        assertEquals(scale(new BigDecimal(3)), delta.getPrice());
        assertEquals(scale(new BigDecimal(100)), delta.getTurnover());
        assertEquals("SLU", delta.getIncomeSymbol());
        assertEquals("USDT", delta.getOutcomeSymbol());

        //手续费计算： trade.getAmount().multiply(exchangeCoin.getFee())
        assertEquals(scale(new BigDecimal(2)), delta.getFee());

        // 支出数量
        assertEquals(scale(new BigDecimal(100)), delta.getOutcomeCoinAmount());

        if (delta.getType().equals(TradeBehaviorType.BUY_TAKER) ||
                delta.getType().equals(TradeBehaviorType.SELL_TAKER)) {
            // 吃单，优惠：t=0.5/m=0.4，用户优惠：t=1/m=0.8

            //优化手续费： 2*0.5 + (2-2*0.5)*1
            assertEquals(scale(new BigDecimal(2)), delta.getFeeDiscount());
            assertEquals(scale(new BigDecimal(0)), delta.getRealFee());

            //买单：获得交易币（减去手续费）
            assertEquals(scale(new BigDecimal(200)), delta.getIncomeCoinAmount());
        } else if (delta.getType().equals(TradeBehaviorType.BUY_MAKER)
                || delta.getType().equals(TradeBehaviorType.SELL_MAKER)) {
            // 挂单，优惠：t=0.5/m=0.4，用户优惠：t=1/m=0.8

            //优化手续费： 2*0.4 + (2-2*0.4)*0.8
            assertEquals(scale(new BigDecimal(1.76)), delta.getFeeDiscount());
            assertEquals(scale(new BigDecimal(0.24)), delta.getRealFee());

            //买单：获得交易币（减去手续费）
            assertEquals(scale(new BigDecimal(199.76)), delta.getIncomeCoinAmount());
        } else {
            Assert.fail("错误的类型");
        }
    }


    private void assert_sell(ExchangeOrderDirection settleDirection, ExchangeOrder order, ExchangeTrade trade, ExchangeCoin exchangeCoin, DiscountRate discountRate) {
        trade.setDirection(ExchangeOrderDirection.SELL);

        TradeSettleDelta delta = TradeSettleDelta.settle(settleDirection, order, trade, exchangeCoin, discountRate);

        //断言
        assertEquals(delta.getMemberId().longValue(), 71639L);
        assertEquals("S001", delta.getOrderId());
        assertEquals("S002", delta.getRefOrderId());
        assertEquals(scale(new BigDecimal(3)), delta.getPrice());
        assertEquals(scale(new BigDecimal(100)), delta.getTurnover());
        assertEquals("USDT", delta.getIncomeSymbol());
        assertEquals("SLU", delta.getOutcomeSymbol());

        //手续费计算： trade.getAmount().multiply(exchangeCoin.getFee())
        assertEquals(scale(new BigDecimal(1)), delta.getFee());

        // 支出数量
        assertEquals(scale(new BigDecimal(200)), delta.getOutcomeCoinAmount());


        if (delta.getType().equals(TradeBehaviorType.BUY_TAKER) ||
                delta.getType().equals(TradeBehaviorType.SELL_TAKER)) {
            // 吃单，优惠：t=0.5/m=0.4，用户优惠：t=0.5/m=0.4

            //优化手续费：  1*0.5 + (1-1*0.5)*0.5
            assertEquals(scale(new BigDecimal(0.75)), delta.getFeeDiscount());
            assertEquals(scale(new BigDecimal(0.25)), delta.getRealFee());

            //买单：获得交易币（减去手续费）
            assertEquals(scale(new BigDecimal(99.75)), delta.getIncomeCoinAmount());
        } else if (delta.getType().equals(TradeBehaviorType.BUY_MAKER)
                || delta.getType().equals(TradeBehaviorType.SELL_MAKER)) {
            // 挂单，优惠：t=0.5/m=0.4，用户优惠：t=0.5/m=0.4

            //优化手续费： 1*0.4 + (1-1*0.4)*0.4
            assertEquals(scale(new BigDecimal(0.64)), delta.getFeeDiscount());
            assertEquals(scale(new BigDecimal(0.36)), delta.getRealFee());

            //买单：获得交易币（减去手续费）
            assertEquals(scale(new BigDecimal(99.64)), delta.getIncomeCoinAmount());
        } else {
            Assert.fail("错误的类型");
        }
    }

    private BigDecimal scale(BigDecimal val) {
        return val.setScale(4, BigDecimal.ROUND_HALF_UP);
    }

}