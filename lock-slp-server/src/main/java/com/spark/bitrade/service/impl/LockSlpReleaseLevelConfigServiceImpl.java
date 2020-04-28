package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.LockSlpReleaseLevelConfig;
import com.spark.bitrade.mapper.LockSlpReleaseLevelConfigMapper;
import com.spark.bitrade.service.LockSlpReleaseLevelConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 社区奖励级差配置表(LockSlpReleaseLevelConfig)表服务实现类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@Service("lockSlpReleaseLevelConfigService")
public class LockSlpReleaseLevelConfigServiceImpl extends ServiceImpl<LockSlpReleaseLevelConfigMapper, LockSlpReleaseLevelConfig> implements LockSlpReleaseLevelConfigService {

    @Override
    public LockSlpReleaseLevelConfig getDefaultLevelConfig(String coinUnit) {
        List<LockSlpReleaseLevelConfig> list = findByCoinUnit(coinUnit);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<LockSlpReleaseLevelConfig> findByCoinUnit(String unit) {
        QueryWrapper<LockSlpReleaseLevelConfig> query = new QueryWrapper<>();
        query.eq("coin_unit", unit).orderByAsc("sort"); // 升序
        return list(query);
    }

    @Override
    public LockSlpReleaseLevelConfig findByUnitAndLevelId(String unit,Integer levelId){
        // QueryWrapper<LockSlpReleaseLevelConfig> query = new QueryWrapper<>();
        // query.eq("coin_unit", unit).eq("level_id",levelId);
        // return getOne(query);

        final String pk = unit + levelId;
        return getById(pk);
    }
}