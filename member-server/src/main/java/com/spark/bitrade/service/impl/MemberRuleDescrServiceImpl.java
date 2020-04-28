package com.spark.bitrade.service.impl;


import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.MemberRuleDescr;
import com.spark.bitrade.mapper.MemberRuleDescrMapper;
import com.spark.bitrade.service.MemberRuleDescrService;

/**
 * <p>
 * 会员规则 服务实现类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Service
public class MemberRuleDescrServiceImpl extends ServiceImpl<MemberRuleDescrMapper, MemberRuleDescr> implements MemberRuleDescrService {

    @Override
    public List<MemberRuleDescr> getRuleDescrList() {
        QueryWrapper<MemberRuleDescr> queryWrapper = new QueryWrapper();
        return this.baseMapper.selectList(queryWrapper);
    }
}
