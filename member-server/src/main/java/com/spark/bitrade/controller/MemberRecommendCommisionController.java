package com.spark.bitrade.controller;


import javax.validation.Valid;

import com.spark.bitrade.vo.MemberRecommendCommisionVo;
import com.spark.bitrade.vo.RecommendCommisionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.biz.IRecommendCommisionService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberRecommendCommision;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * <p>
 * 会员邀请佣金 前端控制器
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@RestController
@RequestMapping("api/v2/member/memberRecommendCommision")
@Api(tags = "会员邀请佣金前端接口")
@Slf4j
public class MemberRecommendCommisionController extends ApiController{

    @Autowired
    private IRecommendCommisionService recommendCommisionService;

    @ApiOperation(value = "我的奖励-获取会员推荐佣金列表接口", notes = "我的奖励-获取会员推荐佣金列表接口")
    @PostMapping("/Recommend/listBuy")
    public MessageRespResult<IPage<RecommendCommisionVo>> findMemberRecommendCommisionByBuy(@MemberAccount Member member, @ApiParam("查询公共参数") @Valid PageParam param) {
        log.info("seatchForm ------>> {}", param);
        IPage<RecommendCommisionVo> page = recommendCommisionService.findMemberRecommendCommisionsByBuy(member.getId(), param);
        return success(page);
    }

    @ApiOperation(value = "我的奖励-币币交易佣金列表接口", notes = "我的奖励-币币交易佣金列表接口")
    @PostMapping("/Recommend/listExchange")
    public MessageRespResult<IPage<MemberRecommendCommision>> findMemberRecommendCommisionByExchange(@MemberAccount Member member, @ApiParam("查询公共参数") @Valid PageParam param) {
        log.info("seatchForm ------>> {}", param);
        IPage<MemberRecommendCommision> page = recommendCommisionService.findMemberRecommendCommisionsByExchange(member.getId(), param);
        return success(page);
    }


    @ApiOperation(value = "我的奖励-返佣记录列表接口", notes = "我的奖励-返佣记录列表接口")
    @PostMapping("/Recommend/listSend")
    public MessageRespResult<IPage<MemberRecommendCommision>> findMemberRecommendCommision(@MemberAccount Member member, @ApiParam("查询公共参数") @Valid PageParam param) {
        log.info("seatchForm ------>> {}", param);
        IPage<MemberRecommendCommision> page = recommendCommisionService.findMemberRecommendCommisionsBySend(member.getId(), param);
        return success(page);
    }


    @ApiOperation(value = "我的奖励-统计币币交易返还", notes = "我的奖励-统计推荐会员返佣")
    @PostMapping("/count")
    public MessageRespResult<List<MemberRecommendCommisionVo>> countMemberRecommendCommision(@MemberAccount Member member) {
        List<MemberRecommendCommisionVo> commisionVo = recommendCommisionService.countRecommendCommision(member.getId());
        return success(commisionVo);
    }

}
