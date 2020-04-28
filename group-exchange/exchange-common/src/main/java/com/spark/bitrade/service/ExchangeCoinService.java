package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.ExchangeCoin;

import java.util.List;

/**
 * 交易币种配置表服务接口
 *
 * @author yangch
 * @since 2019-09-03 13:44:40
 */
public interface ExchangeCoinService extends IService<ExchangeCoin> {
    ExchangeCoin findBySymbol4LocalCache(String symbol);

    ExchangeCoin findBySymbol(String symbol);

}
