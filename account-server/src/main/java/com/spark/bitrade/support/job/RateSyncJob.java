package com.spark.bitrade.support.job;

import com.spark.bitrade.entity.Coin;
import com.spark.bitrade.service.CoinService;
import com.spark.bitrade.service.ICoinExchange;
import com.spark.bitrade.support.RateManager;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * RateSyncJob
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/11/21 10:52
 */
@Slf4j
@Component
public class RateSyncJob {

    private CoinService coinService;
    private ICoinExchange coinExchange;

    private RateManager rateManager;

    @Scheduled(fixedRate = 60 * 1000)
    public void sync() {
        for (Coin coin : coinService.list()) {
            try {
                MessageRespResult<BigDecimal> usdExchangeRate = coinExchange.getUsdExchangeRate(coin.getUnit());
                MessageRespResult<BigDecimal> cnyExchangeRate = coinExchange.getCnyExchangeRate(coin.getUnit());

                BigDecimal usd = BigDecimal.ZERO;
                BigDecimal cny = BigDecimal.ZERO;

                if (usdExchangeRate.isSuccess()) {
                    usd = usdExchangeRate.getData();
                }
                if (cnyExchangeRate.isSuccess()) {
                    cny = cnyExchangeRate.getData();
                }

                rateManager.set(coin, usd, cny);

            } catch (RuntimeException ex) {
                log.error("同步汇率出错 coin = {}, err = {}", coin.getUnit(), ex.getMessage());
            }
        }
    }

    @Autowired
    public void setCoinExchange(ICoinExchange coinExchange) {
        this.coinExchange = coinExchange;
    }

    @Autowired
    public void setCoinService(CoinService coinService) {
        this.coinService = coinService;
    }

    @Autowired
    public void setRateManager(RateManager rateManager) {
        this.rateManager = rateManager;
    }
}
