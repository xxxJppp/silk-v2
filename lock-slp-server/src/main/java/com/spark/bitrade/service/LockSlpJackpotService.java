package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.LockSlpJackpot;

import java.math.BigDecimal;

/**
 * 大乐透总奖池表(LockSlpJackpot)表服务接口
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
public interface LockSlpJackpotService extends IService<LockSlpJackpot> {

    /**
     * 加入奖池
     *
     * @param coinUnit 币种
     * @param jackpotAmount 金额
     */
    boolean add(String coinUnit, BigDecimal jackpotAmount);

}