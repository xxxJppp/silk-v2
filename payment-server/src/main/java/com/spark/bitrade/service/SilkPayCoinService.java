package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.SilkPayCoin;
import com.spark.bitrade.vo.MemberWalletVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 币种配置(SilkPayCoin)表服务接口
 *
 * @author wsy
 * @since 2019-07-29 14:22:25
 */
public interface SilkPayCoinService extends IService<SilkPayCoin> {

    List<MemberWalletVo> findAllAbleUnits();

    /**
     * 校验是否达到币种每日交易上限
     *
     * @param unit   币种
     * @param amount 当前订单金额
     * @return true - 达到 false - 未达到
     */
    boolean checkCoinDailyMax(String unit, BigDecimal amount);
}