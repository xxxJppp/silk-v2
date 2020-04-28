package com.spark.bitrade.support;

import com.spark.bitrade.entity.Coin;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CoinRateManager
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/11/21 10:59
 */
@Component
public class CoinRateManager implements RateManager {

    private Map<String, CoinRate> rateCacheMap = new ConcurrentHashMap<>();

    @Override
    public void set(Coin coin, BigDecimal usd, BigDecimal cny) {
        CoinRate coinRate = rateCacheMap.get(coin.getUnit());
        if (coinRate == null) {
            rateCacheMap.put(coin.getUnit(), new CoinRate(usd, cny));
        } else {
            coinRate.setUsd(usd);
            coinRate.setCny(cny);
        }
    }

    @Override
    public CoinRate get(Coin coin) {
        return rateCacheMap.getOrDefault(coin.getUnit(), new CoinRate(null, null));
    }

    @Override
    public CoinRate get(String coinUnit) {
        return rateCacheMap.getOrDefault(coinUnit, new CoinRate(null, null));
    }

    @Override
    public Map<String, CoinRate> rate() {
        return Collections.unmodifiableMap(rateCacheMap);
    }
}
