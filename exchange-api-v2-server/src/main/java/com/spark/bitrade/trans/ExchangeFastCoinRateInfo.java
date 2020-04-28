package com.spark.bitrade.trans;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.util.BigDecimalUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  闪兑汇率信息
 *
 * @author young
 * @time 2019.06.25 15:55
 */
@Data
@ApiModel(description = "闪兑汇率信息")
public class ExchangeFastCoinRateInfo {
    /**
     * 是否为固定汇率，0=否/1=是
     */
    @ApiModelProperty(value = "是否为固定汇率，0=否/1=是", example = "")
    private boolean isFixedRate = true;

    /**
     * 兑换方向，0=买/1=卖
     */
    @ApiModelProperty(value = "兑换方向，0=买/1=卖", example = "")
    private ExchangeOrderDirection direction;

    /**
     * 兑换币种价格
     */
    @ApiModelProperty(value = "兑换币种价格", example = "")
    private BigDecimal coinRate = BigDecimal.ZERO;

    /**
     * 兑换基本价格
     */
    @ApiModelProperty(value = "兑换基本价格", example = "")
    private BigDecimal baseRate = BigDecimal.ZERO;

    /**
     * 实时汇率（不考虑买卖时的调整的参数）
     */
    @ApiModelProperty(value = "实时汇率", example = "")
    private BigDecimal realtimeRate = BigDecimal.ZERO;

    /**
     * 交易汇率（需考虑买卖时的调整的参数）
     */
    @ApiModelProperty(value = "交易汇率", example = "")
    private BigDecimal tradeRate = BigDecimal.ZERO;

    /**
     * 买入时价格上调默认比例,取值[0-1]；闪兑用户买入时，基于实时价的上调价格的浮动比例。
     */
    @ApiModelProperty(value = "买入时价格上调默认比例,取值[0-1]", example = "0.05")
    private BigDecimal buyAdjustRate = BigDecimal.ZERO;

    /**
     * 卖出时价格下调默认比例，取值[0-1]；闪兑用户买出时，基于实时价的下调价格的浮动比例。
     */
    @ApiModelProperty(value = "卖出时价格下调默认比例，取值[0-1]", example = "0.05")
    private BigDecimal sellAdjustRate = BigDecimal.ZERO;


    public BigDecimal getRealtimeRate() {
        return this.calculateRealtimeAmount(BigDecimal.ONE);
    }

    public BigDecimal getTradeRate() {
        return this.calculateTradeAmount(BigDecimal.ONE);
    }

    /**
     * 根据提供的数量、实时汇率计算成交的数量
     *
     * @param amount 数量，为1时得到的是汇率
     * @return
     */
    public BigDecimal calculateRealtimeAmount(BigDecimal amount) {
        if (this.getDirection() == ExchangeOrderDirection.BUY) {
            //买入场景： 兑换基币币种 -> 接收币种
            //      基币(BT)=1 -> 兑换币(BTC)=4， 汇率计算 = currentPrice= BT/BTC=1/4=0.25
            if (BigDecimalUtil.gt0(this.getCoinRate())) {
                return BigDecimalUtil.div2down(
                        BigDecimalUtil.mul2down(amount, this.getBaseRate(), 16),
                        this.getCoinRate(), 16);
            }
        } else {
            //卖出场景：兑换币 ->基币
            //      兑换币(BTC)=4 -> 基币(BT)=1， 汇率计算 = currentPrice= BTC/BT=4/1=4
            if (BigDecimalUtil.gt0(this.getBaseRate())) {
                return BigDecimalUtil.div2down(
                        BigDecimalUtil.mul2down(amount, this.getCoinRate(), 16),
                        this.getBaseRate(), 16);
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * 根据提供的数量、实时汇率计算支付的数量
     *
     * @param targetAmount 兑换的目标数量
     * @return
     */
    public BigDecimal calculateRealtimePayAmount(BigDecimal targetAmount) {
        if (this.getDirection() == ExchangeOrderDirection.BUY) {
            //买入场景： 兑换基币币种 -> 接收币种
            //      基币(BT)=1 -> 兑换币(BTC)=4， 汇率计算 = currentPrice= BT/BTC=1/4=0.25
            if (BigDecimalUtil.gt0(this.getCoinRate())
                    && BigDecimalUtil.gt0(this.getBaseRate())) {
                // amount * BaseRate /CoinRate = ?
                // amount = ? / BaseRate /CoinRate

                return  BigDecimalUtil.div2up(targetAmount,
                        BigDecimalUtil.div2up( this.getBaseRate(),this.getCoinRate(), 16) , 16);
            }
        } else {
            //卖出场景：兑换币 ->基币
            //      兑换币(BTC)=4 -> 基币(BT)=1， 汇率计算 = currentPrice= BTC/BT=4/1=4
            if (BigDecimalUtil.gt0(this.getCoinRate())
                    && BigDecimalUtil.gt0(this.getBaseRate())) {
                return  BigDecimalUtil.div2up(targetAmount,
                        BigDecimalUtil.div2up( this.getCoinRate(),this.getBaseRate(), 16) , 16);
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * 根据提供的数量、交易汇率计算成交的数量
     *
     * @param amount 数量，为1时得到的是汇率
     * @return
     */
    public BigDecimal calculateTradeAmount(BigDecimal amount) {
        if (this.getDirection() == ExchangeOrderDirection.BUY) {
            //买入场景： 兑换基币币种 -> 接收币种
            //      基币(BT)=1 -> 兑换币(BTC)=4， 汇率计算 = currentPrice= BT/BTC=1/4=0.25
            if (BigDecimalUtil.gt0(this.getCoinRate())) {
                return BigDecimalUtil.div2down(
                        BigDecimalUtil.mul2down(amount,
                                this.getBaseRate().multiply(BigDecimal.ONE.subtract(this.getBuyAdjustRate()))),
                        this.getCoinRate(), 16);
            }
        } else {
            //卖出场景：兑换币 ->基币
            //      兑换币(BTC)=4 -> 基币(BT)=1， 汇率计算 = currentPrice= BTC/BT=4/1=4
            if (BigDecimalUtil.gt0(this.getBaseRate())) {
                return BigDecimalUtil.div2down(
                        BigDecimalUtil.mul2down(amount,
                                this.getCoinRate().multiply(BigDecimal.ONE.subtract(this.getSellAdjustRate()))),
                        this.getBaseRate(), 16);
            }
        }

        return BigDecimal.ZERO;
    }

    /**
     * 根据提供的数量、交易汇率计算支付的数量
     *
     * @param targetAmount 兑换的目标数量
     * @return
     */
    public BigDecimal calculateTradePayAmount(BigDecimal targetAmount) {
        if (this.getDirection() == ExchangeOrderDirection.BUY) {
            //买入场景： 兑换基币币种 -> 接收币种
            //      基币(BT)=1 -> 兑换币(BTC)=4， 汇率计算 = currentPrice= BT/BTC=1/4=0.25
            if (BigDecimalUtil.gt0(this.getCoinRate())
                    && BigDecimalUtil.gt0(this.getBaseRate())) {
                // amount * (BaseRate /CoinRate) * (1-浮动) = ?
                // amount = ? / (BaseRate /CoinRate) /(1-浮动)

               return BigDecimalUtil.div2up(
                        BigDecimalUtil.div2up(targetAmount,
                                BigDecimalUtil.div2up( this.getBaseRate(),this.getCoinRate(), 16) , 16),
                        BigDecimal.ONE.subtract(this.getSellAdjustRate()));
            }
        } else {
            //卖出场景：兑换币 ->基币
            //      兑换币(BTC)=4 -> 基币(BT)=1， 汇率计算 = currentPrice= BTC/BT=4/1=4
            if (BigDecimalUtil.gt0(this.getCoinRate())
                    && BigDecimalUtil.gt0(this.getBaseRate())) {
                return BigDecimalUtil.div2up(
                        BigDecimalUtil.div2up(targetAmount,
                                BigDecimalUtil.div2up( this.getCoinRate(),this.getBaseRate(), 16) , 16),
                        BigDecimal.ONE.subtract(this.getSellAdjustRate()));
            }
        }

        return BigDecimal.ZERO;
    }
}
