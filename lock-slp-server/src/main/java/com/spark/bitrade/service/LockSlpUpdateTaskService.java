package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.LockSlpUpdateTask;
import com.spark.bitrade.vo.LockRecordsVo;

/**
 * 更新推荐人实时数据任务表(LockSlpUpdateTask)表服务接口
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
public interface LockSlpUpdateTaskService extends IService<LockSlpUpdateTask> {
    /**
     * SLP加速释放页面，查询锁仓记录
     *
     * @param size      显示条数
     * @param current   当前页数
     * @param memberId  会员ID
     * @param startTime 查询起始时间
     * @param endTime   查询结束时间
     * @return SLP加速记录
     */
    IPage<LockRecordsVo> listLockRecords(Integer size, Integer current, Long memberId, Long startTime, Long endTime);

}