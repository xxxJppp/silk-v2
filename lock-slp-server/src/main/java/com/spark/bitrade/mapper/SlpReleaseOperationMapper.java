package com.spark.bitrade.mapper;

import com.spark.bitrade.entity.LockSlpReleasePlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * SlpReleaseOperationMapper
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/15 14:22
 */
@Mapper
@Repository
public interface SlpReleaseOperationMapper {

    /**
     * 悲观锁
     * <p>
     * 已取消悲观锁
     *
     * @param id 计划ID
     * @return plan
     */
    LockSlpReleasePlan findByIdWithLock(Long id);

    /**
     * 查找锁仓返还计划ID
     *
     * @param memberId 会员id
     * @param coinUnit 币种
     * @return long
     */
    Long findPlanId(@Param("memberId") Long memberId, @Param("unit") String coinUnit);

    /**
     * 更新锁仓剩余余额
     *
     * @param id     id
     * @param amount 数量
     * @return int
     */
    @Update("update lock_slp_release_plan  set remain_amount = remain_amount - #{amount} where id = #{id} and remain_amount >= #{amount}")
    int updateRemainAmount(@Param("id") Long id, @Param("amount") BigDecimal amount);
}
