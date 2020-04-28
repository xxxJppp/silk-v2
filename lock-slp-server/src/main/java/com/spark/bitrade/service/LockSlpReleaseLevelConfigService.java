package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.LockSlpReleaseLevelConfig;

import java.util.List;

/**
 * 社区奖励级差配置表(LockSlpReleaseLevelConfig)表服务接口
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
public interface LockSlpReleaseLevelConfigService extends IService<LockSlpReleaseLevelConfig> {

    /**
     * 获取默认等级配置
     *
     * @param coinUnit 币种
     * @return S0级配置
     * @author archx
     */
    LockSlpReleaseLevelConfig getDefaultLevelConfig(String coinUnit);

    /**
     * 获取所有等级配置
     *
     * @param unit 币种
     * @return list
     * @author archx
     */
    List<LockSlpReleaseLevelConfig> findByCoinUnit(String unit);

    /**
     * 根据币种和节点id获取配置
     * @author zhangYanjun
     * @time 2019.07.04 20:50
     * @param unit
     * @param levelId
     * @return com.spark.bitrade.entity.LockSlpReleaseLevelConfig
     */
    LockSlpReleaseLevelConfig findByUnitAndLevelId(String unit,Integer levelId);
}