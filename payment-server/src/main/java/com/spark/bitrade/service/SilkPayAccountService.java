package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.GpsLocation;
import com.spark.bitrade.entity.SilkPayAccount;
import com.spark.bitrade.entity.SilkPayOrder;
import com.spark.bitrade.entity.dto.SilkPayAccountDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 支付账号(SilkPayAccount)表服务接口
 *
 * @author wsy
 * @since 2019-07-18 10:38:05
 */
public interface SilkPayAccountService extends IService<SilkPayAccount> {

    /**
     * 进行订单匹配
     * @author shenzucai
     * @time 2019.07.27 9:40
     * @param gpsLocation
     * @param amount
     * @param payType
     * @return true
     */
    List<SilkPayAccountDto> findMostSuitableAccount(GpsLocation gpsLocation, BigDecimal amount, Integer payType);

    /**
     * 减少账户限额
     * @author shenzucai
     * @time 2019.08.05 17:20
     * @param accountId
     * @param amount
     * @return true
     */
    Boolean reduceAccountLimit(Long accountId, BigDecimal amount);

    /**
     * 还原用户限额
     * @author shenzucai
     * @time 2019.08.05 17:20
     * @param accountId
     * @param silkPayOrder
     * @return true
     */
    Boolean cancelAccountLimit(Long accountId,SilkPayOrder silkPayOrder);
}