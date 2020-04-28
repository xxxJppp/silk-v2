package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.LockSlpReleaseTask;

/**
 * 推荐人奖励基金释放任务表(LockSlpReleaseTask)表服务接口
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
public interface LockSlpReleaseTaskService extends IService<LockSlpReleaseTask> {

    /**
     * 更新任务状态为已处理
     *
     * @param id      任务id
     * @param comment 备注
     * @return bool
     */
    boolean processed(Long id, String comment);
}