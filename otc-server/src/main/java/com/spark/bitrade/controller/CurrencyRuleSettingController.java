package com.spark.bitrade.controller;


import com.spark.bitrade.enums.OtcExceptionMsg;
import com.spark.bitrade.service.CurrencyRateService;
import com.spark.bitrade.service.CurrencyRuleSettingService;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 法币规则配置 前端控制器
 * </p>
 *
 * @author qiliao
 * @since 2020-03-18
 */
@RestController
@RequestMapping("/api/v2/currencyRuleSetting")
public class CurrencyRuleSettingController extends ApiController{
    @Resource
    private CurrencyRuleSettingService currencyRuleSettingService;

    /**
     * 根据配置key获取配置value
     * @param ruleKey 配置key
     * @return
     */
    @PostMapping("getCurrencyRuleValueByKey")
    public MessageRespResult<String> getCurrencyRuleValueByKey(String ruleKey){
        return success(currencyRuleSettingService.getCurrencyRuleValueByKey(ruleKey, OtcExceptionMsg.NOT_CURRENTY_RULE));
    }

}

