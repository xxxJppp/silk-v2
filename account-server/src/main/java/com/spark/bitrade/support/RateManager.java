package com.spark.bitrade.support;

import com.spark.bitrade.entity.Coin;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * RateManager
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/11/21 11:01
 */
public interface RateManager {

    void set(Coin coin, BigDecimal usd, BigDecimal cny);

    CoinRate get(Coin coin);

    CoinRate get(String coinUnit);

    /**
     * 所有币种汇率
     *
     * @return map
     */
    Map<String, CoinRate> rate();

    @AllArgsConstructor
    class CoinRate {
        private BigDecimal usd;
        private BigDecimal cny;

        public double usdOrElse(double other) {
            return usd == null || usd.compareTo(BigDecimal.ZERO) == 0 ? other : usd.doubleValue();
        }

        public double cnyOrElse(double other) {
            return cny == null || cny.compareTo(BigDecimal.ZERO) == 0 ? other : cny.doubleValue();
        }

        public BigDecimal getUsd() {
            return usd == null ? BigDecimal.ZERO : usd;
        }

        public BigDecimal getCny() {
            return cny == null ? BigDecimal.ZERO : cny;
        }

        public void setUsd(BigDecimal usd) {
            if (usd == null || usd.compareTo(BigDecimal.ZERO) == 0) {
                return;
            }
            this.usd = usd;
        }

        public void setCny(BigDecimal cny) {
            if (cny == null || cny.compareTo(BigDecimal.ZERO) == 0) {
                return;
            }
            this.cny = cny;
        }
    }
}
