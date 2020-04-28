package com.spark.bitrade.service.impl;

import com.spark.bitrade.dto.ExchangeOrderSellStat;
import com.spark.bitrade.mapper.ExchangeOrderStatMapper;
import com.spark.bitrade.service.SellService;
import com.spark.bitrade.service.optfor.RedisStringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

/**
 *  
 *
 * @author young
 * @time 2019.12.16 20:33
 */
@Service
public class SellServiceImpl implements SellService {
    @Autowired
    private ExchangeOrderStatMapper statMapper;
    @Autowired
    private RedisStringService redisStringService;

    @Override
    //@Cacheable(cacheNames = "exchangeSellStat", key = "'entity:exchangeSellStat:'+#symbol+'-'+#price")
    public Optional<ExchangeOrderSellStat> sellStatByPrice(String symbol, BigDecimal price) {
        return Optional.ofNullable(statMapper.sellStatByPrice(symbol, price));
    }

    @Override
    @CacheEvict(cacheNames = "exchangeSellStat", key = "'entity:exchangeSellStat:'+#symbol+'-'+#price")
    public void cleanCached(String symbol, BigDecimal price) {
    }

    /**
     * 更新最新的卖1价格
     *
     * @param symbol 交易对
     * @param price  价格
     */
    @Override
    public void updateSell1NewestPrice(String symbol, BigDecimal price) {
        redisStringService.set(this.key4Sell1NewestPrice(symbol), price);
    }

    /**
     * 最新的卖1价格
     *
     * @param symbol 交易对
     * @return
     */
    @Override
    public Optional<BigDecimal> getSell1NewestPrice(String symbol) {
        //从redis 获取最新的成交价格
        return Optional.ofNullable((BigDecimal) redisStringService.get(this.key4Sell1NewestPrice(symbol)));
    }

    private String key4Sell1NewestPrice(String symbol) {
        return new StringBuilder("entity:sell1NewestPrice:").append(symbol).toString();
    }
}
