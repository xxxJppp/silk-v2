package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.LockSlpReleaseTaskRecord;
import com.spark.bitrade.vo.AccelerationRecordsVo;

import java.util.List;

/**
 * 推荐人奖励基金释放记录表(LockSlpReleaseTaskRecord)表服务接口
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
public interface LockSlpReleaseTaskRecordService extends IService<LockSlpReleaseTaskRecord> {

    /**
     * SLP加速释放页面，查询加速记录
     *
     * @param size      显示条数
     * @param current   当前页数
     * @param memberId  会员ID
     * @param startTime 查询起始时间
     * @param endTime   查询结束时间
     * @return SLP加速记录
     */
    IPage<AccelerationRecordsVo> listAccelerationRecords(Integer size, Integer current, Long memberId, Long startTime, Long endTime);

    /**
     * 查询未处理的记录
     * @author zhangYanjun
     * @time 2019.07.18 19:04
     * @param
     * @return java.util.List<com.spark.bitrade.entity.LockSlpReleaseTaskRecord>
     */
    List<LockSlpReleaseTaskRecord> getHandleFaild();
}