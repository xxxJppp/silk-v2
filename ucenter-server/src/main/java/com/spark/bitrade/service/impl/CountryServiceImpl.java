package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.mapper.CountryMapper;
import com.spark.bitrade.entity.Country;
import com.spark.bitrade.service.CountryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * (Country)表服务实现类
 *
 * @author wsy
 * @since 2019-06-14 14:39:44
 */
@Service("countryService")
public class CountryServiceImpl extends ServiceImpl<CountryMapper, Country> implements CountryService {
    @Override
    @Cacheable(cacheNames = "country", key = "'entity:country:'+#id")
    public Country findone(String id) {
        return this.getById(id);
    }
}