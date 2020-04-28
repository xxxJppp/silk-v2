package com.spark.bitrade.biz;

import com.spark.bitrade.util.MessageRespResult;

import java.math.BigDecimal;

/**
 * @author: Zhong Jiang
 * @date: 2019-11-28 16:18
 */
public interface IPayService {

    Long purchaseVipAmount(long orderNumber, long memberId, String unit,
                                                  Integer payType, BigDecimal payAmount, Integer days, Long lockId, Long operType);

}
