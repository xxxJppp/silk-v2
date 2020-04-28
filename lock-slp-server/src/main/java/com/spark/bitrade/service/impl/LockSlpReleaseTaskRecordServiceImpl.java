package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.SlpStatus;
import com.spark.bitrade.mapper.LockSlpReleaseTaskRecordMapper;
import com.spark.bitrade.entity.LockSlpReleaseTaskRecord;
import com.spark.bitrade.service.LockSlpReleaseTaskRecordService;
import com.spark.bitrade.vo.AccelerationRecordsVo;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 推荐人奖励基金释放记录表(LockSlpReleaseTaskRecord)表服务实现类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@Service("lockSlpReleaseTaskRecordService")
public class LockSlpReleaseTaskRecordServiceImpl extends ServiceImpl<LockSlpReleaseTaskRecordMapper, LockSlpReleaseTaskRecord> implements LockSlpReleaseTaskRecordService {

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
    @Override
    public IPage<AccelerationRecordsVo> listAccelerationRecords(Integer size, Integer current, Long memberId, Long startTime, Long endTime) {
        Page<LockSlpReleaseTaskRecord> lockSlpReleaseTaskRecordPage = new Page<>(current, size);
        QueryWrapper<LockSlpReleaseTaskRecord> lockSlpReleaseTaskRecordQueryWrapper = new QueryWrapper<LockSlpReleaseTaskRecord>()
                .and(wrapper -> wrapper.eq("member_id", memberId).gt("release_in_amount",0)
                        .eq("status", 1)
                )
                .orderByDesc("release_time");
        if (startTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startTime * 1000);
            lockSlpReleaseTaskRecordQueryWrapper.ge("release_time", calendar.getTime());
        }
        if (endTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(endTime * 1000);
            calendar.add(Calendar.MONTH, 1);
            lockSlpReleaseTaskRecordQueryWrapper.lt("release_time", calendar.getTime());
        }
        IPage<LockSlpReleaseTaskRecord> lockSlpReleaseTaskRecordIPage = page(lockSlpReleaseTaskRecordPage, lockSlpReleaseTaskRecordQueryWrapper);
        Page<AccelerationRecordsVo> accelerationRecordsVoPage = new Page<>(current, size);
        List<AccelerationRecordsVo> accelerationRecordsVoList = new ArrayList<>();
        if (lockSlpReleaseTaskRecordIPage.getRecords() != null && lockSlpReleaseTaskRecordIPage.getRecords().size() > 0) {
            lockSlpReleaseTaskRecordIPage.getRecords().forEach(record -> {
                AccelerationRecordsVo accelerationRecordsVo = new AccelerationRecordsVo();
                accelerationRecordsVo.setMemberId(record.getRefInviteesId());
                accelerationRecordsVo.setReleaseInAmount(record.getReleaseInAmount());
                accelerationRecordsVo.setType(record.getType());
                accelerationRecordsVo.setReleaseTime(record.getReleaseTime());
                accelerationRecordsVoList.add(accelerationRecordsVo);
            });
        }
        accelerationRecordsVoPage.setTotal(lockSlpReleaseTaskRecordIPage.getTotal());
        accelerationRecordsVoPage.setPages(lockSlpReleaseTaskRecordIPage.getPages());
        accelerationRecordsVoPage.setRecords(accelerationRecordsVoList);
        return accelerationRecordsVoPage;
    }

    @Override
    public List<LockSlpReleaseTaskRecord> getHandleFaild(){
        QueryWrapper<LockSlpReleaseTaskRecord> query = new QueryWrapper<>();
        query.eq("status", SlpStatus.NOT_PROCESSED);
        return list(query);
    }

}