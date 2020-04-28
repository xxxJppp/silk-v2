package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.SilkDataDist;

import java.util.List;

/**
 * 系统配置(SilkDataDist)表服务接口
 *
 * @author yangch
 * @since 2019-06-22 15:11:16
 */
public interface SilkDataDistService extends IService<SilkDataDist> {
    /**
     * 查询配置
     *
     * @param id
     * @param key
     * @return
     */
    SilkDataDist findByIdAndKey(String id, String key);

    Boolean toBoolean(SilkDataDist silkData);

    /**
     * 查询指定配置ID下所有的配置
     *
     * @param id
     * @return
     */
    List<SilkDataDist> findListById(String id);
}