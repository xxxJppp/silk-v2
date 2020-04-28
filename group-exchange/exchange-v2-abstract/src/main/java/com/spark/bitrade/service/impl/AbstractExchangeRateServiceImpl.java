package com.spark.bitrade.service.impl;

import com.spark.bitrade.service.ExchangeRateService;
import com.spark.bitrade.service.ICoinExchange;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ExchangeRateServiceImpl
 *
 * @author Pikachu
 * @since 2019/11/6 17:14
 */
@Slf4j
public abstract class AbstractExchangeRateServiceImpl implements ExchangeRateService {

    protected Map<String, BigDecimal> rateCacheMap = new ConcurrentHashMap<>();

    protected ICoinExchange coinExchange;

    @Autowired
    public void setCoinExchange(ICoinExchange coinExchange) {
        this.coinExchange = coinExchange;
    }

    @Override
    public BigDecimal gateUsdRate(String coinUnit) {
        MessageRespResult<BigDecimal> resp = coinExchange.getUsdExchangeRate(coinUnit);

        if (resp.isSuccess()) {
            BigDecimal data = resp.getData();
            if (data != null) {
                rateCacheMap.put(coinUnit, data);
                return data;
            }
        }
        log.error("无法获取币种汇率 [ coin = {}, code = {}, msg = {} ]", coinUnit, resp.getCode(), resp.getMessage());
        return rateCacheMap.computeIfAbsent(coinUnit, key -> BigDecimal.ZERO);
    }
}
