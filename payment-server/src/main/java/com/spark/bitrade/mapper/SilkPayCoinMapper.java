package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.SilkPayCoin;
import com.spark.bitrade.vo.MemberWalletVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 币种配置(SilkPayCoin)表数据库访问层
 *
 * @author wsy
 * @since 2019-07-29 14:22:25
 */
public interface SilkPayCoinMapper extends BaseMapper<SilkPayCoin> {

    @Select(" select unit,scale,trade_max,trade_min,rate_reduction_factor from silk_pay_coin where state=1 order by sort desc ")
    List<MemberWalletVo> findAllAbleUnits();

    @Select("SELECT IF(coin_daily_max >= (SELECT IFNULL(SUM(money), 0) + #{amount} AS money FROM silk_pay_order WHERE coin_id=#{unit} AND state IN (0,1,2,3) AND create_time>=date_format(NOW(),'%Y-%m-%d 00:00:00')), 0, 1) FROM silk_pay_coin WHERE unit = #{unit}")
    int checkCoinDailyMax(@Param("unit") String unit, @Param("amount") BigDecimal amount);
}
