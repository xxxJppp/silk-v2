package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.SupportInternational;
import com.spark.bitrade.mapper.SupportInternationalMapper;
import com.spark.bitrade.service.SupportInternationalService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 国际化资源 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Service
public class SupportInternationalServiceImpl extends ServiceImpl<SupportInternationalMapper, SupportInternational> implements SupportInternationalService {

    @Resource
    public SupportInternationalMapper internationalMapper;

    @Override
    public List<SupportInternational> findByinternationalKey(String inteKey) {
        QueryWrapper<SupportInternational> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight(SupportInternational.INTERNATIONAL_KEY, inteKey);
        return internationalMapper.selectList(queryWrapper);
    }

    @Override
    public SupportInternational findOneByinternationalKey(String inteKey) {
        QueryWrapper<SupportInternational> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SupportInternational::getInternationalKey,inteKey);
        return internationalMapper.selectOne(queryWrapper);
    }

}
