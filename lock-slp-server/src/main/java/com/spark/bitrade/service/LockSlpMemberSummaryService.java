package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.LockSlpMemberSummary;
import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.entity.LockSlpUpdateTask;
import com.spark.bitrade.vo.LockSlpCurrentNodeVo;
import com.spark.bitrade.vo.LockSummationVo;
import com.spark.bitrade.vo.PromotionMemberExtensionVo;

/**
 * 会员社区奖励实时统计表(LockSlpMemberSummary)表服务接口
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
public interface LockSlpMemberSummaryService extends IService<LockSlpMemberSummary> {

    /**
     * 初始化并返回
     * <p>
     * 所有活动参与者都会调用该方法并初始
     *
     * @param task 更新任务
     * @param plan 释放计划
     * @return summary
     * @author archx
     */
    LockSlpMemberSummary initAndGet(LockSlpUpdateTask task, LockSlpReleasePlan plan);

    /**
     * 获取并初始化
     * <p>
     * 社区节点加速释放任务调用，处理上级没有参与活动的特殊情况
     *
     * @param memberId  会员ID
     * @param inviterId 邀请人ID
     * @param coinUnit  币种
     * @return summary
     * @author archx
     */
    LockSlpMemberSummary getAndInit(Long memberId, Long inviterId, String coinUnit);

    /**
     * 更新实时统计
     *
     * @param task 更新任务
     * @param plan 释放计划
     * @author archx
     */
    void updateLockSlpMemberSummary(LockSlpUpdateTask task, LockSlpReleasePlan plan);

    /**
     * 升级等级
     *
     * @param summary 实时统计
     * @author archx
     */
    void upgrade(LockSlpMemberSummary summary);

    /**
     * 统计当前下级数量
     *
     * @param summary 统计
     * @return int
     * @author archx
     */
    long countCurrentSubLevel(LockSlpMemberSummary summary);

    /**
     * 根据会员id和币种查询
     * <p>
     * 使用该接口时，请明确确定返回结果不为NULL
     *
     * @param memberId 会员ID
     * @param unit     币种
     * @return com.spark.bitrade.entity.LockSlpMemberSummary
     * @author zhangYanjun
     * @since 2019.07.04 15:24
     */
    LockSlpMemberSummary findByMemberIdAndUnit(Long memberId, String unit);

    /**
     * SLP加速释放页面，查询锁仓汇总明细
     *
     * @param size      显示条数
     * @param current   当前页数
     * @param inviterId 推荐人ID
     * @param startTime 查询起始时间
     * @param endTime   查询结束时间
     * @return SLP加速记录
     */
    IPage<LockSummationVo> listLockSummation(Integer size, Integer current, Long inviterId, Long startTime, Long endTime);

    /**
     * 获取用户当前节点以及分类释放汇总
     *
     * @param memberId  会员ID
     * @param startTime 查询起始时间
     * @param endTime   查询结束时间
     * @return 用户当前节点以及分类释放汇总
     */
    LockSlpCurrentNodeVo findCurrentNode(Long memberId, Long startTime, Long endTime);

    /**
     * 获取会员社区节点信息
     *
     * @param memberId 会员ID
     * @return 会员社区节点信息
     */
    PromotionMemberExtensionVo findCurrentLevelName(Long memberId);

    /**
     * 获取当前会员或者伞下会员，是否存在锁仓记录
     *
     * @param memberId 会员ID集合
     * @return 有效总人数
     */
    int countEffectiveTotal(Long memberId);

}