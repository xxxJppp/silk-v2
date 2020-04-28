package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.LockSlpReleasePlanRecord;
import com.spark.bitrade.vo.LockSlpPlanRecordsVo;

import java.util.List;

/**
 * 本金返还记录表(LockSlpReleasePlanRecord)表服务接口
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
public interface LockSlpReleasePlanRecordService extends IService<LockSlpReleasePlanRecord> {

    /**
     * 获取指定计划指定期数的释放记录
     *
     * @param planId 计划ID
     * @param period 期数
     * @return record
     * @author archx
     */
    LockSlpReleasePlanRecord getRecord(Long planId, Integer period);

    /**
     * 获取未处理成功的数据
     *
     * @param
     * @return
     * @author zhangYanjun
     * @time 2019.07.18 14:55
     */
    List<LockSlpReleasePlanRecord> getHandleFaild();

    /**
     * 获取静态释放记录
     *
     * @param memberId 会员ID
     * @param current  当前页
     * @param size     条数
     * @return com.spark.bitrade.util.MessageRespResult<com.baomidou.mybatisplus.core.metadata.IPage < com.spark.bitrade.vo.LockSlpPlanRecordsVo>>
     * @author zhangYanjun
     * @time 2019.08.08 18:03
     */
    IPage<LockSlpPlanRecordsVo> findMyReleaseRecord(Integer current, Integer size, Long memberId);
}