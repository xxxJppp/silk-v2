package com.spark.bitrade.mapper;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GlobalConfMapper {

	@Select("select dict_val from silk_data_dist where dict_id = \'SYSTEM_UNIT_CONFIG\' and dict_key = \'SYSTEM_UNIT\'")
	public String getPlatformToken();
	
	@Select("select dict_val from silk_data_dist where dict_id = \'SYSTEM_UNIT_CONFIG\' and dict_key = \'MEMBER_RECOMMEND_COMMISION_UNIT\'")
	public String getMemberRecommendCommisionUnit() ;
	
	@Select("select dict_val from silk_data_dist where dict_id = \'SYSTEM_UNIT_CONFIG\' and dict_key = \'TOKEN_EXCHANGE_FEE_COMMISION_UNIT\'")
	public String getTokenExchangeFeeCommisionUnit();
	
	@Select("SELECT dict_val FROM silk_data_dist WHERE dict_id = \'MEMBER_COMMISION_DISTRIBUTE\' AND dict_key = \'IS_OPEN\'")
	public String getMemberCommisionDistributeStatus();
	
	@Select("SELECT dict_val FROM silk_data_dist WHERE dict_id = \'MEMBER_COMMISION_DISTRIBUTE\' AND dict_key = \'TURN_RATE\'")
	public BigDecimal getCommisionTuneRate();
}
