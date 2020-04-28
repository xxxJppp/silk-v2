package com.spark.bitrade.controller;


import java.util.List;

import com.spark.bitrade.entity.Member;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.spark.bitrade.biz.IRequireConditionService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.RequireConditionVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.validation.Valid;

/**
 * <p>
 * 会员申请条件 前端控制器
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@RestController
@RequestMapping("api/v2/member/memberRequireCondition")
@Api(tags = "会员申请条件前端接口")
public class MemberRequireConditionController extends ApiController{

    @Autowired
    private IRequireConditionService requireConditionService;


    @ApiOperation(value = "获取会员申请条件列表接口", notes = "查询会员申请条件列表接口")
    @PostMapping("/no-auth/list")
    public MessageRespResult<List<RequireConditionVo>> findRequireConditions(){
        List<RequireConditionVo> voList = requireConditionService.getRequireConditionVoList();
        return success(voList);
    }

    @GetMapping("/updateCache")
    public MessageRespResult<Boolean> updateCache() {
        Boolean aBoolean = requireConditionService.updateRequireConditionCache();
        return success(aBoolean);
    }
}
