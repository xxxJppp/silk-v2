package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.NewYearMineral;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 矿石表 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
public interface NewYearMineralService extends IService<NewYearMineral> {

    /**
     * 获取所有矿石
     */
    List<NewYearMineral> findMineralList();

    NewYearMineral findAndupdateMineral(Integer type);

    Map<String,Object> findSilkPlatInfo();
}
