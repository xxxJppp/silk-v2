package com.spark.bitrade.biz.Impl;

import com.spark.bitrade.biz.IRuleDescrService;
import com.spark.bitrade.entity.MemberRuleDescr;
import com.spark.bitrade.service.MemberRuleDescrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.18 18:07
 */
@Service
public class RuleDescrServiceImpl implements IRuleDescrService {

    @Autowired
    private MemberRuleDescrService ruleDescrService;


    @Override
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<MemberRuleDescr> getMemberRuleDescr() {
        return ruleDescrService.getRuleDescrList();
    }

}
