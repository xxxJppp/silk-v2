package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.NewYearConfig;
import com.spark.bitrade.mapper.NewYearConfigMapper;
import com.spark.bitrade.service.NewYearConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 年终集矿石活动配置表 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
@Service
public class NewYearConfigServiceImpl extends ServiceImpl<NewYearConfigMapper, NewYearConfig> implements NewYearConfigService {

    @Override
    public List<NewYearConfig> findNewYearConfig() {
        QueryWrapper<NewYearConfig> queryWrapper = new QueryWrapper<>();
        return this.baseMapper.selectList(queryWrapper);
    }
}
