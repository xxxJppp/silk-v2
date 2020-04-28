package com.spark.bitrade.service;

import com.spark.bitrade.entity.OtcCoin;
import com.spark.bitrade.util.MessageRespResult;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * (OtcCoin)表服务接口
 *
 * @author ss
 * @date 2020-03-19 10:23:47
 */
public interface OtcCoinService extends IService<OtcCoin>{

	MessageRespResult<List<Map<String, Object>>> selectCoinForCurrency();

	MessageRespResult<Map<String, List<OtcCoin>>> selectCurrencyForCoin(String[] baseId);

	List<Map<String, Object>> getAllNormalCoinAndBalance(Long id);
}
