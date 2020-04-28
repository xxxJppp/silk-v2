package com.spark.bitrade.mapper;

import com.spark.bitrade.entity.NewYearProjectAdvertisement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 年终活动-广告位项目方配置表 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
public interface NewYearProjectAdvertisementMapper extends BaseMapper<NewYearProjectAdvertisement> {

    @Select("SELECT * FROM new_year_project_advertisement ORDER BY RAND() LIMIT 1")
    NewYearProjectAdvertisement findRandom();
}
