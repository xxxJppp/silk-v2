package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.LuckyManageCoin;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 幸运宝-对应币种表 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-12-16
 */
public interface LuckyManageCoinMapper extends BaseMapper<LuckyManageCoin> {


    @Select("select * from lucky_manage_coin where num_id=#{actId} and coin_unit=#{coinUnit}")
    Optional<LuckyManageCoin> findByActIdAndCoin(@Param("actId") Long actId, @Param("coinUnit") String coinUnit);

    @Select("select * from lucky_manage_coin where num_id=#{actId}")
    List<LuckyManageCoin> findByActId(@Param("actId") Long actId);

    @Update("update lucky_manage_coin set increase=#{increase} , end_price=#{endPrice} where id=#{id}")
    int updateIncrease(@Param("id") Long id, @Param("increase") BigDecimal increase, @Param("endPrice") BigDecimal endPrice);

    @Update("update lucky_manage_coin set  start_price=#{startPrice} where id=#{id}")
    int updateStartPrice(@Param("id") Long id,  @Param("startPrice") BigDecimal startPrice);
}
