package com.spark.bitrade.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MemberInviteMapper {

	
	
	@Select("select inviter_id from member where id = #{memberId}")
	public Long getInviterById(Long memberId) ;
}
