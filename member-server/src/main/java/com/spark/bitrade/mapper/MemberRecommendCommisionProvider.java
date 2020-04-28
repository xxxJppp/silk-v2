package com.spark.bitrade.mapper;

import org.apache.ibatis.annotations.Param;

import com.alibaba.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MemberRecommendCommisionProvider {

	public static String query(@Param("memberId") long memberId, @Param("distributingCommisionIdList" ) String distributingCommisionIdList) {
		String sql = null;
		if(!Strings.isNullOrEmpty(distributingCommisionIdList)) {
		  sql = "select accumulative_quantity from member_recommend_commision where deliver_to_member_id = "+memberId+" and distribute_status = 10 and id not in ("+ distributingCommisionIdList+") order by id desc limit 1 offset 0";
		
		} else {
			  sql = "select accumulative_quantity from member_recommend_commision where deliver_to_member_id = "+memberId+" and distribute_status = 10  order by id desc limit 1 offset 0";

		}
		log.info("accumulative query sql:" + sql);
		return sql;
	}
}
