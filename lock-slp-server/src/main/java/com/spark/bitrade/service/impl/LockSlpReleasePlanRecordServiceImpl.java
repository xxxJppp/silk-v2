package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.SlpStatus;
import com.spark.bitrade.entity.LockSlpReleasePlanRecord;
import com.spark.bitrade.mapper.LockSlpReleasePlanRecordMapper;
import com.spark.bitrade.service.LockSlpReleasePlanRecordService;
import com.spark.bitrade.vo.LockSlpPlanRecordsVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 本金返还记录表(LockSlpReleasePlanRecord)表服务实现类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@Service("lockSlpReleasePlanRecordService")
public class LockSlpReleasePlanRecordServiceImpl extends ServiceImpl<LockSlpReleasePlanRecordMapper, LockSlpReleasePlanRecord> implements LockSlpReleasePlanRecordService {

    @Override
    public LockSlpReleasePlanRecord getRecord(Long planId, Integer period) {
        // 指定计划和期数
        QueryWrapper<LockSlpReleasePlanRecord> query = new QueryWrapper<>();
        query.eq("ref_plan_id", planId).eq("period", period);

        return getOne(query);
    }

    @Override
    public List<LockSlpReleasePlanRecord> getHandleFaild() {
        QueryWrapper<LockSlpReleasePlanRecord> query = new QueryWrapper<>();
        query.eq("status", SlpStatus.NOT_PROCESSED);
        return list(query);
    }

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
    @Override
    public IPage<LockSlpPlanRecordsVo> findMyReleaseRecord(Integer current, Integer size, Long memberId) {
        IPage<LockSlpPlanRecordsVo> page = new Page<>(current, size);
        List<LockSlpPlanRecordsVo> list = getBaseMapper().findMyReleaseRecord(page, memberId);
        page.setRecords(list);
        return page;
    }
}