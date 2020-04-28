package com.spark.bitrade.service.impl;

//import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.LockType;
import com.spark.bitrade.entity.ExchangeFastOrder;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.service.IExchangeFastApiService;
import com.spark.bitrade.service.ILockService;
import com.spark.bitrade.service.LockSlpService;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 *  
 *
 * @author young
 * @time 2019.06.27 14:05
 */
@Slf4j
@Service
public class LockSlpServiceImpl implements LockSlpService {
    @Autowired
    private IExchangeFastApiService exchangeFastApiService;
    @Autowired
    private ILockService lockService;


    /**
     * SLP锁仓操作
     * 注意：通过分布式事务，先进行闪兑操作，再进行锁仓操作
     *
     * @param apiKey
     * @param appId              应用ID
     * @param id                 活动ID
     * @param amount             参与活动数量
     * @param limitCountValid    当前有效的活动数量限制
     * @param limitCountInDay    当日参与活动的次数限制
     * @param holdCoinSymbol     活动参与币种
     * @param lockCoinSymbol     活动币种
     * @param coinSymbolRate     活动参与币种汇率
     * @param lockCoinSymbolRate 活动币种汇率
     * @return
     */
    @Override
//    @LcnTransaction
    public MessageRespResult<LockCoinDetail> lockSlpCoin(String apiKey,
                                                         String appId,
                                                         Long id,
                                                         BigDecimal amount,
                                                         int limitCountValid,
                                                         int limitCountInDay,
                                                         String holdCoinSymbol,
                                                         String lockCoinSymbol,
                                                         BigDecimal coinSymbolRate,
                                                         BigDecimal lockCoinSymbolRate) {
        //闪兑操作，将 活动参与币种 兑换为 活动币种
        MessageRespResult<ExchangeFastOrder> result = exchangeFastApiService.exchangeTargetAmoutAndRate(apiKey, appId,
                holdCoinSymbol, lockCoinSymbol,
                amount, ExchangeOrderDirection.SELL,
                coinSymbolRate, lockCoinSymbolRate);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(result);

        //锁仓操作
        MessageRespResult<LockCoinDetail> resultLockDetail = lockService.lockWithPassword(apiKey, LockType.LOCK_SLP, id,
                amount, result.getData().getAmount(), coinSymbolRate, null, limitCountValid, limitCountInDay);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultLockDetail);
        return resultLockDetail;
    }
}
