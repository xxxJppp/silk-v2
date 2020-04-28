package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.mapper.ExchangeMemberDiscountRuleMapper;
import com.spark.bitrade.entity.ExchangeMemberDiscountRule;
import com.spark.bitrade.service.ExchangeMemberDiscountRuleService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员币币交易优惠规则服务实现类
 *
 * @author yangch
 * @since 2019-11-06 10:38:26
 */
@Service("exchangeMemberDiscountRuleService")
public class ExchangeMemberDiscountRuleServiceImpl
        extends ServiceImpl<ExchangeMemberDiscountRuleMapper, ExchangeMemberDiscountRule>
        implements ExchangeMemberDiscountRuleService {

    @Override
    @Cacheable(cacheNames = "exchangeMDR", key = "'entity:exchangeMDR:'+#memberId")
    public List<ExchangeMemberDiscountRule> findByMemberId(long memberId) {
        List<ExchangeMemberDiscountRule> list = this.baseMapper.findByMemberId(memberId);
        if (list == null) {
            list = new ArrayList<>(1);
        }
        return list;
    }
}