package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.SupportCoinMatch;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 扶持上币交易对  Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
public interface SupportCoinMatchMapper extends BaseMapper<SupportCoinMatch> {

    @Select("SELECT c.symbol FROM exchange_coin c where c.coin_symbol=#{coin} ")
    List<String> findByCoinUnit(@Param("coin") String coin);
}
