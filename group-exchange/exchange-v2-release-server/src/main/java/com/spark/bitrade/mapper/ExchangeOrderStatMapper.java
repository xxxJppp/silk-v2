package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.dto.ExchangeOrderSellStat;
import com.spark.bitrade.entity.ExchangeOrder;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 *  
 *
 * @author young
 * @time 2019.12.16 20:03
 */
public interface ExchangeOrderStatMapper extends BaseMapper<ExchangeOrder> {

    /**
     * 统计指定价格下有效的交易数量
     *
     * @param symbol 交易对
     * @param price  价格
     * @return
     */
    ExchangeOrderSellStat sellStatByPrice(@Param("symbol") String symbol, @Param("price") BigDecimal price);
}
