package com.spark.bitrade.service;

import com.spark.bitrade.trans.DiscountRate;

/**
 *  币币交易折扣率服务接口
 *
 * @author young
 * @time 2019.11.06 10:41
 */
public interface DiscountService {
    /**
     * 获取折扣率
     *
     * @param memberId 会员ID
     * @param symbol   交易对
     * @return
     */
    DiscountRate getDiscountRate(long memberId, String symbol);
}
