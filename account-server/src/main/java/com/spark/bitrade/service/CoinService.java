package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.Coin;
import org.springframework.cache.annotation.Cacheable;

/**
 * (Coin)表服务接口
 *
 * @author zhangYanjun
 * @since 2019-06-20 17:48:00
 */
public interface CoinService extends IService<Coin> {

    /**
     * 根据币种ID查询(注：会缓存结果数据)
     * @param coinId 币种ID
     * @return
     */
    Coin findOne(String coinId);

    /**
     * 根据币种单位查询(注：会缓存结果数据)
     * @param unit
     * @return
     */
    Coin findByUnit(String unit);
}