package com.spark.bitrade.service;


import com.spark.bitrade.entity.CurrencyRateData;
import feign.Param;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Map;

/**
 * 货币汇率换算
 *
 * @author lc
 * @date 2020/3/24 09:53
 */
public interface CurrencyRateService {


    /**
     *  获取法币与交易币种的汇率
     * @param fSymbol 法币币种缩写
     * @param tSymbol 交易币种缩写
     * @return
     */
    BigDecimal getCurrencyRate(@Param("fSymbol") String fSymbol, @Param("tSymbol") String tSymbol);

    /**
     *  获取法币与交易币种的汇率
     * @param fSymbol 法币币种ID
     * @param tSymbol 交易币种缩写
     * @return 成功时返回币种对应的法币价格
     */
    BigDecimal getCurrencyPrice(Long fSymbol, String tSymbol);


    /**
     * 获取平台支持法币与交易币种的汇率
     * @return
     */
    Map<String, LinkedList<CurrencyRateData>> getCurrencyRateList();


    /**
     * 获取平台支持法币与美元的汇率列表
     * @return
     */
    Map queryPriceList();

    BigDecimal toUsdcRate(String symbol);

}
