package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.MemberSecuritySet;
import com.spark.bitrade.mapper.MemberSecuritySetMapper;
import com.spark.bitrade.service.MemberSecuritySetService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * (MemberSecuritySet)表服务实现类
 *
 * @author wsy
 * @since 2019-06-14 14:20:15
 */
@Service("memberSecuritySetService")
public class MemberSecuritySetServiceImpl extends ServiceImpl<MemberSecuritySetMapper, MemberSecuritySet> implements MemberSecuritySetService {

    @Override
    @Cacheable(cacheNames = "memberSecuritySet", key = "'entity:memberSecuritySet:'+#memberId")
    public MemberSecuritySet findByMemberId(Long memberId) {
        QueryWrapper<MemberSecuritySet> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId);
        return getOne(wrapper);
    }
}