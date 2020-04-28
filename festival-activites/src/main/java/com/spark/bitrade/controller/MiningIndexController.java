package com.spark.bitrade.controller;

import com.spark.bitrade.biz.MemberDailyTaskBizService;
import com.spark.bitrade.biz.MiningActivityBizService;
import com.spark.bitrade.biz.NewYearConfigBizService;
import com.spark.bitrade.common.RedisUtil;
import com.spark.bitrade.common.customer.EventConsumer;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.MiningIndexVo;
import com.spark.bitrade.vo.MiningMineralVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: Zhong Jiang
 * @date: 2019-12-30 17:29
 */

@RestController
@RequestMapping("api/v2/mining")
@Api(tags = "挖矿活动-前端接口")
public class MiningIndexController extends ApiController {

    @Autowired
    private NewYearConfigBizService configBizService;

    @Resource
    private MemberDailyTaskBizService memberDailyTaskBizService;
    private  static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    @Autowired
    private MiningActivityBizService activityBizService;

    @ApiOperation(value = "获取挖矿主页数据,活动规则接口", notes = "获取挖矿主页数据,活动规则接口",tags = "挖矿活动-前端接口")
    @PostMapping("/index")
    public MessageRespResult<MiningIndexVo> findMiningIndex(@MemberAccount Member member) {
    	//新增登录挖矿机会
    	this.memberDailyTaskBizService.addMemberTask(EventConsumer.TASK_LOGIN_STATUS, member.getId(), sdf.format(new Date()), true, true , true);
        MiningIndexVo indexVo = configBizService.findActivity(member.getId());
        return success(indexVo);
    }

    @ApiOperation(value = "立即挖矿", notes = "立即挖矿",tags = "挖矿活动-前端接口")
    @PostMapping("/doMining")
    public MessageRespResult<MiningMineralVo> doMiningActivity(@MemberAccount Member member) {
//        MiningMineralVo miningMineralVo = activityBizService.doMiningActivity(memberid);
        MiningMineralVo miningMineralVo = activityBizService.doMiningActivity(member.getId());
        System.out.println(miningMineralVo);
        return success(miningMineralVo);
    }
}
