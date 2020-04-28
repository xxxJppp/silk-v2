package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.LockSlpJackpot;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * 大乐透总奖池表(LockSlpJackpot)表数据库访问层
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
public interface LockSlpJackpotMapper extends BaseMapper<LockSlpJackpot> {

    @Update("update lock_slp_jackpot set jackpot_amount = jackpot_amount + #{amount}, update_time = NOW() where id = #{id} and coin_unit = #{unit}")
    int updateJackpotAmount(@Param("id") Long id, @Param("unit") String coinUnit, @Param("amount") BigDecimal amount);
}