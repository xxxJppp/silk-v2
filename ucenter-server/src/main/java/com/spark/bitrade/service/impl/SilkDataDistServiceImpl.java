package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.SilkDataDist;
import com.spark.bitrade.mapper.SilkDataDistMapper;
import com.spark.bitrade.service.SilkDataDistService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统配置(SilkDataDist)表服务实现类
 *
 * @author yangch
 * @since 2019-06-22 15:11:16
 */
@Service("silkDataDistService")
public class SilkDataDistServiceImpl extends ServiceImpl<SilkDataDistMapper, SilkDataDist> implements SilkDataDistService {

    @Override
    @Cacheable(cacheNames = "silkDataDist", key = "'entity:silkDataDist:dictId-'+#id+'&dictKey-'+#key")
    public SilkDataDist findByIdAndKey(String id, String key) {
        return this.baseMapper.findByIdAndKey(id, key);
    }

    @Override
    @Cacheable(cacheNames = "silkDataDist", key = "'entity:silkDataDist:lst-dictId-'+#id")
    public List<SilkDataDist> findListById(String id) {
        return this.baseMapper.findListById(id);
    }

    @Override
    public Boolean toBoolean(SilkDataDist silkData) {
        if (silkData == null) {
            return false;
        }
        if ("true".equalsIgnoreCase(silkData.getDictVal())
                || "1".equals(silkData.getDictVal())) {
            return true;
        }

        return false;
    }
}