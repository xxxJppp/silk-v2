package com.spark.bitrade.service;

import java.math.BigDecimal;

public interface GlobalConfService {

//	public PlatformToken getPlatformToken();
	
	public String getMemberRecommendCommisionUnit() ;
	
	public String getTokenExchangeFeeCommisionUnit();
	
	public String getMemberCommisionDistributeStatus();
	
	public BigDecimal getCommisionTuneRate();
}
