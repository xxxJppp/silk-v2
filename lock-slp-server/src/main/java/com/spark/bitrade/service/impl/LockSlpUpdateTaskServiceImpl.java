package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.mapper.LockSlpUpdateTaskMapper;
import com.spark.bitrade.entity.LockSlpUpdateTask;
import com.spark.bitrade.service.LockSlpUpdateTaskService;
import com.spark.bitrade.vo.LockRecordsVo;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

/**
 * 更新推荐人实时数据任务表(LockSlpUpdateTask)表服务实现类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@Service("lockSlpUpdateTaskService")
public class LockSlpUpdateTaskServiceImpl extends ServiceImpl<LockSlpUpdateTaskMapper, LockSlpUpdateTask> implements LockSlpUpdateTaskService {

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
    @Override
    public IPage<LockRecordsVo> listLockRecords(Integer size, Integer current, Long memberId, Long startTime, Long endTime) {
        Page<LockRecordsVo> page = new Page<>(current, size);
        Long queryEndTime = null;
        if (endTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(endTime * 1000);
            calendar.add(Calendar.MONTH, 1);
            queryEndTime = calendar.getTimeInMillis() / 1000;
        }
        List<LockRecordsVo> list = getBaseMapper().listLockRecords(page, memberId, startTime, queryEndTime);
        page.setRecords(list);
        return page;
    }

}