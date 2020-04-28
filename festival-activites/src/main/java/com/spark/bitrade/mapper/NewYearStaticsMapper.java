package com.spark.bitrade.mapper;

import com.spark.bitrade.entity.NewYearStatics;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 领奖记录 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
public interface NewYearStaticsMapper extends BaseMapper<NewYearStatics> {

	@Update("UPDATE new_year_statics SET send_amount = send_amount + #{send} , lock_amount = lock_amount + #{lock} WHERE coin_unit = #{coin} AND collect_date = #{collectDate}")
	void addSendAndLock(@Param("send")BigDecimal send , @Param("lock")BigDecimal lock , @Param("coin")String coin ,@Param("collectDate") String collectDate);

	@Update("UPDATE new_year_statics SET released_amount = released_amount + #{released} WHERE coin_unit = #{coin} AND collect_date = #{collectDate}")
	void addReleased(@Param("released")BigDecimal released ,@Param("coin")String coin ,@Param("collectDate") String collectDate);
}
