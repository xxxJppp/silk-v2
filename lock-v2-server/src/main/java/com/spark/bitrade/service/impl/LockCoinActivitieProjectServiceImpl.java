package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.mapper.LockCoinActivitieProjectMapper;
import com.spark.bitrade.entity.LockCoinActivitieProject;
import com.spark.bitrade.service.LockCoinActivitieProjectService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * (LockCoinActivitieProject)表服务实现类
 *
 * @author zhangYanjun
 * @since 2019-06-19 15:23:27
 */
@Service("lockCoinActivitieProjectService")
public class LockCoinActivitieProjectServiceImpl extends ServiceImpl<LockCoinActivitieProjectMapper, LockCoinActivitieProject> implements LockCoinActivitieProjectService {

    @Cacheable(cacheNames = "lockCoinActivitieProject", key = "'entity:lockCoinActivitieProject:'+#id")
    @Override
    public LockCoinActivitieProject findOne(Long id){
        return this.baseMapper.selectById(id);
    }
}