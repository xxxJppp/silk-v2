package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.LockCoinActivitieSetting;

import java.math.BigDecimal;
import java.util.List;

/**
 * 锁仓活动方案配置(LockCoinActivitieSetting)表服务接口
 *
 * @author zhangYanjun
 * @since 2019-06-19 14:33:04
 */
public interface LockCoinActivitieSettingService extends IService<LockCoinActivitieSetting> {

    /**
     * 查询活动
     *
     * @param id 活动ID
     * @return
     */
    LockCoinActivitieSetting findOne(Long id);

    /**
     * 根据活动方案ID查询子活动列表
     *
     * @param projetcId 活动方案ID
     * @return
     */
    List<LockCoinActivitieSetting> findList(Long projetcId);

    /**
     * 通过时间查找生效中的活动配置
     *
     * @author tansitao
     * @time 2018/7/2 9:32   
     */
    LockCoinActivitieSetting findOneByTime(Long id);

    /**
     * 根据活动方案ID查询生效中子活动列表
     *
     * @param projetcId 活动方案ID
     * @author yangch
     * @time 2018/7/2 9:32   
     */
    List<LockCoinActivitieSetting> findListByTime(Long projetcId);

    /**
     * 累加购买数量
     *
     * @param activitieId 活动ID
     * @param amount      购买数量
     * @return
     */
    Boolean updateBoughtAmount(long activitieId, BigDecimal amount);

    /**
     * 查询指定活动方案下所有的累计购买数量
     *
     * @param projetcId 活动方案ID
     * @return
     */
    BigDecimal totalBoughtAmount(long projetcId);
}