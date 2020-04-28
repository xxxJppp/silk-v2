package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.mapper.CoinMapper;
import com.spark.bitrade.entity.Coin;
import com.spark.bitrade.service.CoinService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * (Coin)表服务实现类
 *
 * @author zhangYanjun
 * @since 2019-06-20 17:48:00
 */
@Service("coinService")
public class CoinServiceImpl extends ServiceImpl<CoinMapper, Coin> implements CoinService {

    @Override
    @Cacheable(cacheNames = "coin", key = "'entity:coin:'+#coinId")
    public Coin findOne(String coinId) {
        return this.baseMapper.selectById(coinId);
    }

    @Override
    @Cacheable(cacheNames = "coin", key = "'entity:coinUnit:'+#unit")
    public Coin findByUnit(String unit) {
        return this.baseMapper.selectOne(new QueryWrapper<Coin>().eq("unit", unit));
    }
}