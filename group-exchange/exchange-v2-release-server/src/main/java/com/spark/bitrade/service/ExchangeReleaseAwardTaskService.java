package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.ExchangeReleaseAwardTask;

import java.util.List;

/**
 * 币币交易-推荐人奖励任务表(ExchangeReleaseAwardTask)表服务接口
 *
 * @author yangch
 * @since 2020-01-17 17:18:53
 */
public interface ExchangeReleaseAwardTaskService extends IService<ExchangeReleaseAwardTask> {

    /**
     * 返回需要处理的任务
     * <p>
     * 查询所有奖励到账时间小于当前时间且未处理的任务
     *
     * @return list
     */
    List<ExchangeReleaseAwardTask> findAwaitReleaseTask();

    /**
     * 释放任务
     *
     * @param taskId 任务ID
     */
    void releaseTask(Long taskId);
}