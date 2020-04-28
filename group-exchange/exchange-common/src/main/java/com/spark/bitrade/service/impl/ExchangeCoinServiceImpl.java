package com.spark.bitrade.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.mapper.ExchangeCoinMapper;
import com.spark.bitrade.entity.ExchangeCoin;
import com.spark.bitrade.service.ExchangeCoinService;
import com.spark.bitrade.util.SpringContextUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 交易币种配置表服务实现类
 *
 * @author yangch
 * @since 2019-09-03 13:44:40
 */
@Service
public class ExchangeCoinServiceImpl extends ServiceImpl<ExchangeCoinMapper, ExchangeCoin> implements ExchangeCoinService {
    /**
     * 创建缓存，默认1分钟过期
     */
    private TimedCache<String, ExchangeCoin> timedCache = CacheUtil.newTimedCache(1 * 60 * 1000);

    @Override
    public ExchangeCoin findBySymbol4LocalCache(String symbol) {
        ExchangeCoin exchangeCoin = timedCache.get(symbol);
        if (exchangeCoin == null) {
            exchangeCoin = this.getService().findBySymbol(symbol);
            timedCache.put(symbol, exchangeCoin);
        }
        return exchangeCoin;
    }

    @Override
    @Cacheable(cacheNames = "exchangeCoinSymbol", key = "'entity:exchangeCoin:'+#symbol")
    public ExchangeCoin findBySymbol(String symbol) {
        return this.baseMapper.selectById(symbol);
    }


    ExchangeCoinServiceImpl getService() {
        return SpringContextUtil.getBean(ExchangeCoinServiceImpl.class);
    }
}
