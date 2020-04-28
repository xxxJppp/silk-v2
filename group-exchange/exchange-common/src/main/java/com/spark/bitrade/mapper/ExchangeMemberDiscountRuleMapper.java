package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.ExchangeMemberDiscountRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员币币交易优惠规则表数据库访问层
 *
 * @author yangch
 * @since 2019-11-06 10:37:29
 */
public interface ExchangeMemberDiscountRuleMapper extends BaseMapper<ExchangeMemberDiscountRule> {

    /**
     * 查询会员的优惠规则
     *
     * @param memberId
     * @return
     */
    List<ExchangeMemberDiscountRule> findByMemberId(@Param("memberId") long memberId);
}