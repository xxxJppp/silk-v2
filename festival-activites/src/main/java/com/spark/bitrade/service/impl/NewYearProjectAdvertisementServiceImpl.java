package com.spark.bitrade.service.impl;

import com.spark.bitrade.entity.NewYearProjectAdvertisement;
import com.spark.bitrade.mapper.NewYearProjectAdvertisementMapper;
import com.spark.bitrade.service.NewYearProjectAdvertisementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 年终活动-广告位项目方配置表 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
@Service
public class NewYearProjectAdvertisementServiceImpl extends ServiceImpl<NewYearProjectAdvertisementMapper, NewYearProjectAdvertisement> implements NewYearProjectAdvertisementService {

    @Override
    public NewYearProjectAdvertisement findRandomOneRecord() {
        return this.baseMapper.findRandom();
    }
}
