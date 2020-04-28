package com.spark.bitrade.trans;

import com.spark.bitrade.entity.ExchangeMemberDiscountRule;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  币币交易折率
 *
 * @author young
 * @time 2019.09.04 10:36
 */
@Data
public class DiscountRate {
    /**
     * 吃单：买币折扣率，用小数表示百分比（表示减免的手续费）。eg：20%=0.2；1为手续费全免，0为不优惠手续费
     */
    private BigDecimal buyDiscount;
    /**
     * 吃单：卖币折扣率
     */
    private BigDecimal sellDiscount;

    /**
     * 挂单：买币折扣率
     */
    private BigDecimal entrustBuyDiscount;

    /**
     * 挂单：卖币折扣率
     */
    private BigDecimal entrustSellDiscount;


    /**
     * 没有手续费
     */
    private static DiscountRate noProcedureFee = new DiscountRate(BigDecimal.ONE, BigDecimal.ONE);

    /**
     * 默认有手续费
     */
    private static DiscountRate defaulDiscountRate = new DiscountRate(BigDecimal.ZERO, BigDecimal.ZERO);

    /**
     * @param buyDiscount         吃单：买币折扣率
     * @param sellDiscount        吃单：卖币折扣率
     * @param entrustBuyDiscount  挂单：买币折扣率
     * @param entrustSellDiscount 挂单：卖币折扣率
     */
    public DiscountRate(BigDecimal buyDiscount, BigDecimal sellDiscount,
                        BigDecimal entrustBuyDiscount, BigDecimal entrustSellDiscount) {
        this.buyDiscount = getRate(buyDiscount);
        this.sellDiscount = getRate(sellDiscount);
        this.entrustBuyDiscount = getRate(entrustBuyDiscount);
        this.entrustSellDiscount = getRate(entrustSellDiscount);
    }

    /**
     * @param buyDiscount  吃单、挂单：买币折扣率
     * @param sellDiscount 吃单、挂单：卖币折扣率
     */
    public DiscountRate(BigDecimal buyDiscount, BigDecimal sellDiscount) {
        this(buyDiscount, sellDiscount, buyDiscount, sellDiscount);
    }

    public DiscountRate(ExchangeMemberDiscountRule rule) {
        this(rule.getFeeBuyDiscount(), rule.getFeeSellDiscount(),
                rule.getFeeEntrustBuyDiscount(), rule.getFeeEntrustSellDiscount());
    }

    /**
     * 无手续费的折扣率
     *
     * @return
     */
    public static DiscountRate getNoProcedureFee() {
        return noProcedureFee;
    }

    /**
     * 无优惠的折扣率
     *
     * @return
     */
    public static DiscountRate getDefaulDiscountRate() {
        return defaulDiscountRate;
    }


    private BigDecimal getRate(BigDecimal rate) {
        if (rate.compareTo(BigDecimal.ONE) > 0) {
            return BigDecimal.ONE;
        } else if (rate.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        } else {
            return rate;
        }
    }
}
