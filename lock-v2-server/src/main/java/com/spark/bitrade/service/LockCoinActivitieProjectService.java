package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.LockCoinActivitieProject;

/**
 * (LockCoinActivitieProject)表服务接口
 *
 * @author zhangYanjun
 * @since 2019-06-19 15:23:27
 */
public interface LockCoinActivitieProjectService extends IService<LockCoinActivitieProject> {
    LockCoinActivitieProject findOne(Long id);
}