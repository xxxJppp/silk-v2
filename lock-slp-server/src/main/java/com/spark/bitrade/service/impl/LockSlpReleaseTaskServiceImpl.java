package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.SlpProcessStatus;
import com.spark.bitrade.entity.LockSlpReleaseTask;
import com.spark.bitrade.mapper.LockSlpReleaseTaskMapper;
import com.spark.bitrade.service.LockSlpReleaseTaskService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Calendar;

/**
 * 推荐人奖励基金释放任务表(LockSlpReleaseTask)表服务实现类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@Service("lockSlpReleaseTaskService")
public class LockSlpReleaseTaskServiceImpl extends ServiceImpl<LockSlpReleaseTaskMapper, LockSlpReleaseTask> implements LockSlpReleaseTaskService {

    @Override
    public boolean processed(Long id, String comment) {
        // 更新当前任务的“记录状态”为“1-已处理”
        UpdateWrapper<LockSlpReleaseTask> update = new UpdateWrapper<>();
        update.set("status", SlpProcessStatus.PROCESSED).set("update_time", Calendar.getInstance().getTime());

        if (StringUtils.hasText(comment)) {
            update.set("comment", comment);
        }
        update.eq("id", id).eq("status", SlpProcessStatus.NOT_PROCESSED);
        return update(update);
    }
}