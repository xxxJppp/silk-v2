package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.ExchangeMemberDiscountRule;

import java.util.List;

/**
 * 会员币币交易优惠规则服务接口
 *
 * @author yangch
 * @since 2019-11-06 10:38:26
 */
public interface ExchangeMemberDiscountRuleService extends IService<ExchangeMemberDiscountRule> {

    /**
     * 查询会员的优惠规则
     *
     * @param memberId
     * @return
     */
    List<ExchangeMemberDiscountRule> findByMemberId(long memberId);
}