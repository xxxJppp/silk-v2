package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.entity.NewYearMineral;
import com.spark.bitrade.mapper.NewYearMineralMapper;
import com.spark.bitrade.service.NewYearMineralService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 矿石表 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
@Service
public class NewYearMineralServiceImpl extends ServiceImpl<NewYearMineralMapper, NewYearMineral> implements NewYearMineralService {

    @Override
    public List<NewYearMineral> findMineralList() {
        return this.baseMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public NewYearMineral findAndupdateMineral(Integer type) {
        this.baseMapper.updateByType(type);
        QueryWrapper<NewYearMineral> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(NewYearMineral::getMineralType, type);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public Map<String, Object> findSilkPlatInfo() {
        return baseMapper.findSilkPlatInfo();
    }
}
