package com.spark.bitrade.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.MemberFeeDayStat;

/**
 * <p>
 * 会员购买情况日统计 Mapper 接口
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Mapper
public interface MemberFeeDayStatMapper extends BaseMapper<MemberFeeDayStat> {

	@Select("select * from member_fee_day_stat where statistic_date = #{date}")
	MemberFeeDayStat getCurrentDayStat(String date);
	
	@Update("update member_fee_day_stat set buy_unit_quantity = buy_unit_quantity + #{stat.buyUnitQuantity}, lock_unit_quantity = lock_unit_quantity + #{stat.lockUnitQuantity}, "
			+ "buy_count= buy_count + #{stat.buyCount}, lock_count = lock_count + #{stat.lockCount}, buy_commision = buy_commision + #{stat.buyCommision}, lock_commision = lock_commision + #{stat.lockCommision}, "
			+ "unlock_unit_quantity = unlock_unit_quantity + #{stat.unlockUnitQuantity}, version = version + 1 , update_time = now() where statistic_date = DATE_FORMAT(#{stat.statisticDate}, '%Y-%m-%d') and version = #{stat.version}")
	int updateByStatDate(@Param("stat") MemberFeeDayStat stat);
}
