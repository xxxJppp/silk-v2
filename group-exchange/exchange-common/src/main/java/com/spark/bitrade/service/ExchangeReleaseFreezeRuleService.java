package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.ExchangeReleaseFreezeRule;

import java.util.Optional;

/**
 * 币币交易释放与冻结-规则配置表(ExchangeReleaseFreezeRule)表服务接口
 *
 * @author yangch
 * @since 2019-12-16 14:45:06
 */
public interface ExchangeReleaseFreezeRuleService extends IService<ExchangeReleaseFreezeRule> {
    /**
     * 查询释放与冻结规则
     *
     * @param symbol 交易对
     * @return
     */
    Optional<ExchangeReleaseFreezeRule> findBySymbol(String symbol);

    /**
     * 查询释放与冻结规则
     *
     * @param symbol 交易对
     * @return
     */
    Optional<ExchangeReleaseFreezeRule> findBySymbol4LocalCache(String symbol);
}