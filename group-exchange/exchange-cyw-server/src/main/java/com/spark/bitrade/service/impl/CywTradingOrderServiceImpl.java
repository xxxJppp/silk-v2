package com.spark.bitrade.service.impl;

import com.spark.bitrade.entity.ExchangeCywOrder;
import com.spark.bitrade.entity.constants.CywRedisKeys;
import com.spark.bitrade.service.CywTradingOrderService;
import com.spark.bitrade.service.optfor.RedisHashService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *  
 *
 * @author young
 * @time 2019.09.19 14:58
 */
@Slf4j
@Service
public class CywTradingOrderServiceImpl implements CywTradingOrderService {
    @Autowired
    private RedisHashService redisHashService;

    /**
     * 订单存放到Redis中
     *
     * @param exchangeCywOrder
     */
    @Override
    public void addTradingOrderToReids(ExchangeCywOrder exchangeCywOrder) {
        //订单信息存入正在进行的缓存库中
        redisHashService.hPut(
                CywRedisKeys.getCywOrderTradingKey(exchangeCywOrder.getMemberId(), exchangeCywOrder.getSymbol()),
                exchangeCywOrder.getOrderId(), exchangeCywOrder);
    }

    /**
     * 从Redis中获取交易的订单
     *
     * @param memberId
     * @param orderId
     * @return
     */
    @Override
    public ExchangeCywOrder queryTradingOrderFromRedis(Long memberId, String orderId) {
        return (ExchangeCywOrder) redisHashService.hGet(CywRedisKeys.getCywOrderTradingKey(memberId, CywRedisKeys.parseSymbolFromOrderId(orderId)), orderId);
    }

    /**
     * 从Redis中删除交易中的订单
     *
     * @param memberId
     * @param orderId
     * @return 返回删除的数量
     */
    @Override
    public long deleteTradingOrderFromReids(Long memberId, String orderId) {
        return redisHashService.hDelete(CywRedisKeys.getCywOrderTradingKey(memberId, CywRedisKeys.parseSymbolFromOrderId(orderId)), orderId);
    }
}
