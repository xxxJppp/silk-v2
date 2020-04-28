package com.spark.bitrade.service;

import java.math.BigDecimal;

public interface TokenExchangeRateService {

	public BigDecimal getTokenExchangeRate(String source,String dest);
	
	public BigDecimal getToken2CNYRate(String source) ;
	
	public BigDecimal commision2USDT(String commisionUnit);
}
