package com.spark.bitrade.biz;

import com.spark.bitrade.entity.ExchangeCoinExtend;

/**
 * @author: Zhong Jiang
 * @date: 2019-11-22 10:48
 */
public interface IExchangeCoinService {

    ExchangeCoinExtend getExchangeCoinExtendBySymbol(String symbol);
}
