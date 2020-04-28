package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spark.bitrade.common.RedisUtil;
import com.spark.bitrade.constants.MsgCode;
import com.spark.bitrade.entity.CurrencyRuleSetting;
import com.spark.bitrade.enums.OtcExceptionMsg;
import com.spark.bitrade.mapper.CurrencyRuleSettingMapper;
import com.spark.bitrade.service.CurrencyRuleSettingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.util.AssertUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 法币规则配置 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2020-03-18
 */
@Service
public class CurrencyRuleSettingServiceImpl extends ServiceImpl<CurrencyRuleSettingMapper, CurrencyRuleSetting> implements CurrencyRuleSettingService {
    @Resource
    private CurrencyRuleSettingMapper currencyRuleSettingMapper;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public String getCurrencyRuleValueByKey(String ruleKey, MsgCode otcExceptionMsg) {
        String value = redisUtil.getVal("OTC:USDC:" + ruleKey);
        if(value == null){
            CurrencyRuleSetting currencyRuleSetting = currencyRuleSettingMapper.selectOne(new LambdaQueryWrapper<CurrencyRuleSetting>().eq(CurrencyRuleSetting::getRuleKey,ruleKey));
            AssertUtil.notNull(currencyRuleSetting, otcExceptionMsg);
            AssertUtil.notNull(currencyRuleSetting.getRuleValue(), otcExceptionMsg);
            value = currencyRuleSetting.getRuleValue();
            redisUtil.setVal("OTC:USDC:" + ruleKey,value,30*60L);
        }
        return value;
    }
}
