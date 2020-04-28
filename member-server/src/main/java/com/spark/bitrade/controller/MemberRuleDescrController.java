package com.spark.bitrade.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spark.bitrade.biz.IRuleDescrService;
import com.spark.bitrade.entity.MemberRuleDescr;
import com.spark.bitrade.util.MessageRespResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 会员规则 前端控制器
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@RestController
@RequestMapping("api/v2/member/memberRuleDescr")
@Api(tags = "会员规则前端接口")
public class MemberRuleDescrController extends ApiController {

    @Autowired
    private IRuleDescrService ruleDescrService;


    @ApiOperation(value = "获取会员规列表则接口", notes = "查询会员规则列表接口")
    @PostMapping(value = "/no-auth/list")
    public MessageRespResult<List<MemberRuleDescr>> findMemberRuleDescrs() {
        List<MemberRuleDescr> list = ruleDescrService.getMemberRuleDescr();
        return success(list);
    }
}
