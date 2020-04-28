package com.spark.bitrade.service;

import com.spark.bitrade.entity.ExchangeUsdcOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.vo.ExchangeUsdcInfo;

import java.math.BigDecimal;

/**
 * USDC兑换记录(ExchangeUsdcOrder)表服务接口
 *
 * @author ss
 * @date 2020-04-08 16:01:32
 */
public interface ExchangeUsdcOrderService extends IService<ExchangeUsdcOrder>{

    /**
     * 根据memberId查询USDC兑换前的必要参数
     * @param memberId
     * @return
     */
    ExchangeUsdcInfo getPre(Long memberId);

    /**
     *  USDC兑换
     * @param member
     * @param usdcAmount 兑换USDC数量
     * @param exchangeUnitAmount 兑换币数量
     * @param jyPassword 交易密码
     * @param price USDC对兑换币的价格：如果price=2,则 一个兑换币等于两个USDC
     * @return
     */
    Boolean exchange(Member member, BigDecimal usdcAmount, BigDecimal exchangeUnitAmount, String jyPassword,BigDecimal price);
}