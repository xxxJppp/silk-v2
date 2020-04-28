package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.Country;

/**
 * (Country)表服务接口
 *
 * @author wsy
 * @since 2019-06-14 14:39:44
 */
public interface CountryService extends IService<Country> {
    Country findone(String id);
}