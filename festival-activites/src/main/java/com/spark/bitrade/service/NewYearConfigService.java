package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.NewYearConfig;

import java.util.List;

/**
 * <p>
 * 年终集矿石活动配置表 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
public interface NewYearConfigService extends IService<NewYearConfig> {


    /**
     * 查询 有效活动
     * @return
     */
    public List<NewYearConfig> findNewYearConfig();

}
