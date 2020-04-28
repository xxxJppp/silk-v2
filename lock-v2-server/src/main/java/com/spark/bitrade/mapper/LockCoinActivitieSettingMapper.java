package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.LockCoinActivitieSetting;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * (LockCoinActivitieSetting)表数据库访问层
 *
 * @author zhangYanjun
 * @since 2019-06-19 14:33:04
 */
public interface LockCoinActivitieSettingMapper extends BaseMapper<LockCoinActivitieSetting> {

    /**
     * 查询锁仓活动配置
     *
     * @param id 活动ID
     * @return
     */
    LockCoinActivitieSetting findByIdAndTime(@Param("id") long id);

    /**
     * 根据活动方案ID查询子活动列表
     *
     * @param projetcId 活动方案ID
     * @return
     */
    List<LockCoinActivitieSetting> findList(@Param("projetcId")Long projetcId);

    /**
     * 根据活动方案ID查询生效中子活动列表
     * @param projetcId 活动方案ID
     *
     * @author yangch
     * @time 2018/7/2 9:32   
     */
    List<LockCoinActivitieSetting> findListByTime(@Param("projetcId")Long projetcId);

    /**
     * 累加购买数量
     *
     * @param activitieId 活动ID
     * @param amount      购买数量
     * @return
     */
    Integer updateBoughtAmount(@Param("activitieId") long activitieId, @Param("amount") BigDecimal amount);

    /**
     * 查询指定活动方案下所有的累计购买数量
     * @param projetcId 活动方案ID
     * @return
     */
    BigDecimal totalBoughtAmount(@Param("projetcId") long projetcId);
}