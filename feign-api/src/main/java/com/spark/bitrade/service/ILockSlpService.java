package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.JobReceipt;
import com.spark.bitrade.vo.PromotionMemberExtensionVo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * ILockSlpService
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/9 10:31
 */
@FeignClient(FeignServiceConstant.SLP_SERVER)
public interface ILockSlpService {

    /**
     * 获取规定时间返回类应该释放的返还计划
     *
     * @param releaseAt 最后释放时间戳
     * @return resp
     */
    @GetMapping("/lock-slp/api/v2/lockSlpReleasePlan/range")
    MessageRespResult<List<LockSlpReleasePlan>> getLockSlpReleasePlan(@RequestParam("releaseAt") Long releaseAt);

    /**
     * 释放任务
     *
     * @param planId   返还计划ID
     * @param datetime 日期
     * @return resp
     */
    @PostMapping("/lock-slp/api/v2/lockSlpReleasePlan/release")
    MessageRespResult<JobReceipt> releaseLockSlpReleasePlan(@RequestParam("planId") Long planId,
                                                            @RequestParam("datetime") String datetime);

    /**
     * 释放完成检查
     * <p>
     * 处理已释放完成但还未更新状态的返回计划
     *
     * @return resp
     */
    @PostMapping("/lock-slp/api/v2/lockSlpReleasePlan/check")
    MessageRespResult<JobReceipt> releaseCompletedCheck();

    /**
     * 获取会员社区节点信息
     *
     * @param memberId 会员ID
     * @return 会员社区节点信息
     */
    @PostMapping("/lock-slp/lockSlpRelease/findCurrentLevelName")
    MessageRespResult<PromotionMemberExtensionVo> findCurrentLevelName(@RequestParam("memberId") Long memberId);

    /**
     * 获取当前会员或者伞下会员，锁仓总人数
     *
     * @param memberId 会员ID集合
     * @return 有效总人数
     * @author zhongxj
     */
    @PostMapping("/lock-slp/lockSlpRelease/countEffectiveTotal")
    MessageRespResult<Integer> countEffectiveTotal(@RequestParam("memberId") Long memberId);
}
