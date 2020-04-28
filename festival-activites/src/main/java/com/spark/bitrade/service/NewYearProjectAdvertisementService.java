package com.spark.bitrade.service;

import com.spark.bitrade.entity.NewYearProjectAdvertisement;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 年终活动-广告位项目方配置表 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
public interface NewYearProjectAdvertisementService extends IService<NewYearProjectAdvertisement> {

    /**
     * 随机获取一条广告语
     *
     * @return
     */
    NewYearProjectAdvertisement findRandomOneRecord();

}
