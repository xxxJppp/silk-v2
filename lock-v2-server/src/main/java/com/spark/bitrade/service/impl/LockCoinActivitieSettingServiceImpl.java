package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.spark.bitrade.mapper.LockCoinActivitieSettingMapper;
import com.spark.bitrade.entity.LockCoinActivitieSetting;
import com.spark.bitrade.service.LockCoinActivitieSettingService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * (LockCoinActivitieSetting)表服务实现类
 *
 * @author zhangYanjun
 * @since 2019-06-19 14:33:32
 */
@Service("lockCoinActivitieSettingService")
public class LockCoinActivitieSettingServiceImpl extends ServiceImpl<LockCoinActivitieSettingMapper, LockCoinActivitieSetting>
        implements LockCoinActivitieSettingService {

    @Override
    @Cacheable(cacheNames = "lockCoinActivitieSetting", key = "'entity:lockCoinActivitieSetting:'+#id")
    public LockCoinActivitieSetting findOne(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    @Cacheable(cacheNames = "lockCoinActivitieSetting", key = "'entity:lockCoinActivitieSetting:lst-'+#projetcId")
    public List<LockCoinActivitieSetting> findList(Long projetcId) {
        return this.baseMapper.findList(projetcId);
    }

    /**
     * 通过时间查找生效中的活动配置
     *  @author tansitao
     *
     * @time 2018/7/2 9:32   
     */
    @Cacheable(cacheNames = "lockCoinActivitieSetting", key = "'entity:lockCoinActivitieSetting:bytime-'+#id")
    @Override
    public LockCoinActivitieSetting findOneByTime(Long id) {
        return this.baseMapper.findByIdAndTime(id);
    }

    @Override
    @Cacheable(cacheNames = "lockCoinActivitieSetting", key = "'entity:lockCoinActivitieSetting:lst-bytime-'+#projetcId")
    public List<LockCoinActivitieSetting> findListByTime(Long projetcId) {
        return this.baseMapper.findListByTime(projetcId);
    }


    @CacheEvict(cacheNames = "lockCoinActivitieSetting", allEntries = true)
    @Override
    public Boolean updateBoughtAmount(long activitieId, BigDecimal amount) {
        return SqlHelper.retBool(this.baseMapper.updateBoughtAmount(activitieId, amount));
    }

    @Override
    public BigDecimal totalBoughtAmount(long projetcId) {
        return this.baseMapper.totalBoughtAmount(projetcId);
    }


}