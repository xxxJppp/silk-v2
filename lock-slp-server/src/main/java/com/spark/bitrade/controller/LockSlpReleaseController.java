package com.spark.bitrade.controller;


import com.spark.bitrade.service.LockSlpMemberSummaryService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.PromotionMemberExtensionVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 布朗计划，内部服务调用控制层
 *
 * @author zhongxj
 * @since 2019-07-15 15:48:58
 */
@Slf4j
@RestController
@RequestMapping("lockSlpRelease")
@Api(description = "布朗计划，内部服务调用控制层")
public class LockSlpReleaseController extends ApiController {
    /**
     * 会员社区奖励实时统计,服务对象
     */
    @Resource
    private LockSlpMemberSummaryService lockSlpMemberSummaryService;

    /**
     * 获取会员社区节点信息
     *
     * @param memberId 会员ID
     * @return 会员社区节点信息
     * @author zhongxj
     */
    @ApiOperation(value = "获取会员社区节点信息", notes = "获取会员社区节点信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "会员ID", name = "memberId", dataTypeClass = Long.class, required = true)
    })
    @PostMapping("/findCurrentLevelName")
    public MessageRespResult<PromotionMemberExtensionVo> findCurrentLevelName(@RequestParam("memberId") Long memberId) {
        log.info("获取会员社区节点信息,接收参数:memberId={}", memberId);
        return success(lockSlpMemberSummaryService.findCurrentLevelName(memberId));
    }

    /**
     * 获取当前会员或者伞下会员，锁仓总人数
     *
     * @param memberId 会员ID集合
     * @return 有效总人数
     * @author zhongxj
     */
    @ApiOperation(value = "获取当前会员或者伞下会员，锁仓总人数", notes = "获取当前会员或者伞下会员，锁仓总人数")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "会员ID", name = "memberId", dataTypeClass = Long.class, required = true)
    })
    @PostMapping("/countEffectiveTotal")
    public MessageRespResult<Integer> countEffectiveTotal(@RequestParam("memberId") Long memberId) {
        log.info("获取当前会员或者伞下会员，锁仓总人数:memberId={}", memberId);
        return success(lockSlpMemberSummaryService.countEffectiveTotal(memberId));
    }
}