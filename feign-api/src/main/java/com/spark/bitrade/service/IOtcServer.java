package com.spark.bitrade.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.util.MessageRespResult;


import java.util.List;
import java.util.Map;

@FeignClient(FeignServiceConstant.OTC_SERVER_NEW)
public interface IOtcServer {

	@PostMapping(value = "/otcServerApi/api/v2/otcCoin/getCurrencyRate")
	 MessageRespResult getCurrencyRate(@RequestParam("fSymbol") String fSymbol,
	                                             @RequestParam("tSymbol") String tSymbol);

	 @PostMapping(value = "/otcServerApi/api/v2/currencyManage/getMemberCurrencyUnitById")
	String getMemberCurrencyUnitById(@RequestParam("memberId")Long memberId);

	/**
	 * 自动下架余额不足的广告
	 * @return
	 */
	 @PostMapping(value = "/otcServerApi/api/v2/advertise/autoPutOffShelvesAdvertise")
	 MessageRespResult<Map<String, List<Long>>> autoPutOffShelvesAdvertise();
}
