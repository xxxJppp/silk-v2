package com.spark.bitrade.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spark.bitrade.service.ICoinExchange;
import com.spark.bitrade.service.TokenExchangeRateService;
import com.spark.bitrade.util.MessageRespResult;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TokenExchangeRateServiceImpl implements TokenExchangeRateService{
	
	@Autowired
	private ICoinExchange coinExchangeService;

	public BigDecimal getTokenExchangeRate(String source,String dest) {
		if(source.equals(dest)) return new BigDecimal(1);
		MessageRespResult<BigDecimal> p1 = null;
		MessageRespResult<BigDecimal> p2 = null;
		try {
			p1 = this.coinExchangeService.getUsdExchangeRate(source);
			p2 = this.coinExchangeService.getUsdExchangeRate(dest);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("query token exchange rate error",e.getMessage());
			return new BigDecimal(0);
		}
		BigDecimal source2USDRate = p1.getData();
		BigDecimal dest2USDRate = p2.getData();
		return source2USDRate.divide(dest2USDRate, 8 , RoundingMode.DOWN);
	}
	
	
	public BigDecimal getToken2CNYRate(String source) {
		if(source.equals("CNY")) return new BigDecimal(1);
		MessageRespResult<BigDecimal> p1 = null;
		try {
			p1 = this.coinExchangeService.getCnyExchangeRate(source);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("query token exchange rate error",e.getMessage());
			return new BigDecimal(0);
		}
		BigDecimal p = p1.getData();
		return p;
	}
	
	public BigDecimal commision2USDT(String commisionUnit) {
		MessageRespResult<BigDecimal> p1 = null;
		try {
			p1 = this.coinExchangeService.getUsdExchangeRate(commisionUnit);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("query commision token to USDT rate error",e.getMessage());
			return new BigDecimal(0);
		}
		BigDecimal p = p1.getData();
		return p;
	}
}
