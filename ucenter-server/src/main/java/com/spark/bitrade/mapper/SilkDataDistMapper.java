package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.SilkDataDist;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统配置(SilkDataDist)表数据库访问层
 *
 * @author yangch
 * @since 2019-06-22 15:11:16
 */
public interface SilkDataDistMapper extends BaseMapper<SilkDataDist> {

    /**
     * 查询配置
     * @param id
     * @param key
     * @return
     */
    SilkDataDist findByIdAndKey(@Param("dictId")String id, @Param("dictKey")String key);

    /**
     * 根据ID查询配置
     * @param id
     * @return
     */
    List<SilkDataDist> findListById(@Param("dictId")String id);
}