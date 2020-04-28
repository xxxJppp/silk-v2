package com.spark.bitrade.service;

import com.spark.bitrade.constants.MsgCode;
import com.spark.bitrade.entity.CurrencyRuleSetting;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 法币规则配置 服务类
 * </p>
 *
 * @author qiliao
 * @since 2020-03-18
 */
public interface CurrencyRuleSettingService extends IService<CurrencyRuleSetting> {
    /**
     * 根据配置key获取配置value
     * @param ruleKey 配置key
     * @param otcExceptionMsg 配置为空时的错误码
     * @return
     */
    String getCurrencyRuleValueByKey(String ruleKey, MsgCode otcExceptionMsg);
}
