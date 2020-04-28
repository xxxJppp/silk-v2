package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.entity.ExchangeFastOrder;
import com.spark.bitrade.trans.ExchangeFastCoinRateInfo;

import java.math.BigDecimal;

/**
 * 闪兑订单(ExchangeFastOrder)表服务接口
 *
 * @author yangch
 * @since 2019-06-24 17:06:54
 */
public interface ExchangeFastOrderService extends IService<ExchangeFastOrder> {

    /**
     * 查询闪兑订单
     *
     * @param orderId 订单ID
     * @return
     */
    ExchangeFastOrder findOne(Long orderId);

    /**
     * 闪兑发起方接口
     *
     * @param memberId       会员ID
     * @param appId          应用ID
     * @param coinSymbol     闪兑币种名称
     * @param baseSymbol     闪兑基币名称
     * @param amount         闪兑数量
     * @param direction      兑换方向
     * @param rateInfo       汇率价信息
     * @param isTargetAmount 是否兑换为指定目标结果的币
     * @return
     */
    ExchangeFastOrder exchangeInitiator(Long memberId, String appId,
                                        String coinSymbol, String baseSymbol, BigDecimal amount,
                                        ExchangeOrderDirection direction,
                                        ExchangeFastCoinRateInfo rateInfo,
                                        boolean isTargetAmount);


    /**
     * 闪兑接收方接口
     *
     * @param orderId 订单ID
     */
    void exchangeReceiver(Long orderId);
}