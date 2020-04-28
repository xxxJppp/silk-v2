package com.spark.bitrade.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
  * 币种价格的工具类
  * @author tansitao
  * @time 2018/6/26 14:29 
  */
public class PriceUtil {
    private Logger logger = LoggerFactory.getLogger(PriceUtil.class);

    /**
     * 币种转化汇率
     * @author tansitao
     * @time 2018/10/31 16:36 
     * @return 汇率
     */
    public static BigDecimal toRate( int CoinScale ,BigDecimal sourcePrice, BigDecimal targetPrice){
        return sourcePrice.divide(targetPrice, CoinScale + 1, BigDecimal.ROUND_UP);
    }

    /**
     * 币种汇率转化
     * @author tansitao
     * @time 2018/10/31 16:36 
     * @param sourceAmount 转化前的数目
     */
    public static BigDecimal toRate(BigDecimal sourceAmount,int coinScale, BigDecimal sourcePrice, BigDecimal targetPrice) {
        return toRate(coinScale, sourcePrice, targetPrice).multiply(sourceAmount).setScale(coinScale, BigDecimal.ROUND_UP);
    }
}
