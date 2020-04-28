package com.spark.bitrade.service;

import java.math.BigDecimal;

/**
 * ExchangeRateService
 *
 * @author Pikachu
 * @since 2019/11/6 17:00
 */
public interface ExchangeRateService {

    /**
     * 获取交易
     *
     * @param coinUnit 币种
     * @return rate
     */
    BigDecimal gateUsdRate(String coinUnit);
}
