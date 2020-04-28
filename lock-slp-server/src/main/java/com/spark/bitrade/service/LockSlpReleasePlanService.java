package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.LockSlpReleasePlan;
import com.spark.bitrade.vo.LockSlpMemberRecordDetailVo;
import com.spark.bitrade.vo.LockSlpMemberRecordVo;

import java.util.List;

/**
 * 本金返还计划表(LockSlpReleasePlan)表服务接口
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
public interface LockSlpReleasePlanService extends IService<LockSlpReleasePlan> {

    /**
     * 获取最后释放时间在此时间戳之前的数据
     *
     * @param releaseAt 最后释放时间戳
     * @return list
     */
    List<LockSlpReleasePlan> getLatestReleaseAt(Long releaseAt);

    /**
     * 获取锁仓记录所需的字段，包括：理财套餐、锁仓数量、锁仓时间
     *
     * @param id 本金返回计划ID
     * @return 锁仓记录所需的字段，包括：理财套餐、锁仓数量、锁仓时间
     */
    LockSlpReleasePlan getLockSlpReleasePlanById(Long id);

    /**
     * 获取用户参与记录汇总
     *
     * @param memberId 会员ID
     * @return 用户参与记录
     */
    LockSlpMemberRecordVo findMemberRecordAnlys(Long memberId);


    /**
     * 获取用户参与记录行明细
     *
     * @param memberId 会员ID
     * @return 用户参与记录
     */
    IPage<LockSlpMemberRecordDetailVo> findMemberRecordsLine(Long memberId, int current, int size);
}