package com.spark.bitrade.service.impl;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.date.DateUnit;
import com.spark.bitrade.entity.ExchangeMemberDiscountRule;
import com.spark.bitrade.service.DiscountService;
import com.spark.bitrade.service.ExchangeMemberDiscountRuleService;
import com.spark.bitrade.trans.DiscountRate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  币币交易折扣服务实现类
 *
 * @author young
 * @time 2019.11.06 10:41
 */
@Slf4j
@Service
public class DiscountServiceImpl implements DiscountService {

    @Autowired
    private ExchangeMemberDiscountRuleService discountRuleService;

    /**
     * 默认折扣
     */
    private DiscountRate defaulDiscountRate = DiscountRate.getDefaulDiscountRate();

    /**
     * 创建缓存，默认30分钟过期
     */
    private Cache<String, Map<String, DiscountRate>> cachedDiscountRate =
            CacheUtil.newLFUCache(500, DateUnit.MINUTE.getMillis() * 30);

    @Override
    public DiscountRate getDiscountRate(long memberId, String symbol) {
        if (null == symbol) {
            return defaulDiscountRate;
        }

        Map<String, DiscountRate> map = this.getDiscountRateMap(memberId);
        if (null == map) {
            return defaulDiscountRate;
        }

        DiscountRate discountRate = map.get(symbol.toUpperCase());
        if (null != discountRate) {
            return discountRate;
        }

        //处理“*”号的规则
        return map.getOrDefault("*", defaulDiscountRate);
    }

    /**
     * 获取用户的优化规则集
     *
     * @param memberId
     * @return
     */
    private Map<String, DiscountRate> getDiscountRateMap(long memberId) {
        Map<String, DiscountRate> map = cachedDiscountRate.get(String.valueOf(memberId));
        if (map == null) {
            // 初始化数据
            map = new HashMap<>(8);
            List<ExchangeMemberDiscountRule> list = discountRuleService.findByMemberId(memberId);
            if (list == null || list.size() == 0) {
                map.put("*", defaulDiscountRate);
            } else {
                for (ExchangeMemberDiscountRule rule : list) {
                    map.put(rule.getSymbol().toUpperCase(), new DiscountRate(rule));
                }
            }
        }

        return map;
    }
}
