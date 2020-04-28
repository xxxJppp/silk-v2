package com.spark.bitrade.biz;

import com.spark.bitrade.constant.ModuleType;
import com.spark.bitrade.entity.SupportPayRecords;

import java.math.BigDecimal;

/**
 *  
 *    
 *  @author liaoqinghui  
 *  @time 2019.11.05 16:49  
 */
public interface IPayRecordService {

    /**
     * 构造支付记录
     * @param memberId
     * @param upCoinId
     * @param moduleType
     * @param payCoin
     * @param remark
     * @param usdtAmount
     * @return
     */
    SupportPayRecords generatePayRecord(Long memberId,
                                        Long upCoinId,
                                        ModuleType moduleType,
                                        String payCoin,
                                        String remark, BigDecimal usdtAmount);
}
