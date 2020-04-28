package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.ExchangeReleaseFreezeRule;
import com.spark.bitrade.mapper.ExchangeReleaseFreezeRuleMapper;
import com.spark.bitrade.service.ExchangeReleaseFreezeRuleService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 币币交易释放与冻结-规则配置表(ExchangeReleaseFreezeRule)表服务实现类
 *
 * @author yangch
 * @since 2019-12-16 14:45:06
 */
@Service("exchangeReleaseFreezeRuleService")
public class ExchangeReleaseFreezeRuleServiceImpl
        extends ServiceImpl<ExchangeReleaseFreezeRuleMapper, ExchangeReleaseFreezeRule> implements ExchangeReleaseFreezeRuleService {

    @Override
    @Cacheable(cacheNames = "exchangeReleaseFreezeRule", key = "'entity:exchangeReleaseFreezeRule:'+#symbol")
    public Optional<ExchangeReleaseFreezeRule> findBySymbol(String symbol) {
        return Optional.ofNullable(this.baseMapper.selectById(symbol));
    }

    @Override
    @Cacheable(cacheNames = "exchangeReleaseFreezeRule", key = "'entity:exchangeReleaseFreezeRule:'+#symbol")
    public Optional<ExchangeReleaseFreezeRule> findBySymbol4LocalCache(String symbol) {
        return Optional.ofNullable(this.baseMapper.selectById(symbol));
    }
}