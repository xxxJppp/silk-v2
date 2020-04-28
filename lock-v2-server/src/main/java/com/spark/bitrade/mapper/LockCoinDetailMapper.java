package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.constant.LockStatus;
import com.spark.bitrade.constant.LockType;
import com.spark.bitrade.entity.LockCoinDetail;
import org.apache.ibatis.annotations.Param;

/**
 * (LockCoinDetail)表数据库访问层
 *
 * @author zhangYanjun
 * @since 2019-06-19 15:57:04
 */
public interface LockCoinDetailMapper extends BaseMapper<LockCoinDetail> {

    /**
     * 根据id修改返佣状态(未返佣-》已返佣)
     *
     * @param id
     * @return java.lang.Integer
     * @author zhangYanjun
     * @time 2019.06.21 17:40
     */
    Integer updateRewardStatusToCompleteById(@Param("id") Long id);

    /**
     * 根据id修改状态
     *
     * @param id 用户ID
     * @param oldStatus
     * @param newStatus
     * @return
     */
    Integer updateStatusById(@Param("id") Long id, @Param("oldStatus") LockStatus oldStatus ,
                             @Param("newStatus") LockStatus newStatus);

    /**
     * 根据id修改状态和币种
     *
     * @param id 用户ID
     * @param oldStatus
     * @param newStatus
     * @return
     */
    Integer updateStatusAndRemarkById(@Param("id") Long id, @Param("oldStatus") LockStatus oldStatus ,
                             @Param("newStatus") LockStatus newStatus,
                             @Param("remark") String remark);

    /**
     * 当前有效的活动数量
     *
     * @param memberId 用户ID
     * @param lockType 活动类型
     * @return
     */
    Integer countValidLockCoinDetail(@Param("memberId") Long memberId, @Param("lockType") LockType lockType);

    /**
     * 当日参与活动的次数
     *
     * @param memberId 用户ID
     * @param lockType 活动类型
     * @return
     */
    Integer countLockCoinDetailInDay(@Param("memberId") Long memberId, @Param("lockType") LockType lockType);
}