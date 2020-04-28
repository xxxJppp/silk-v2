package com.spark.bitrade.mapper;

import com.spark.bitrade.entity.Member;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2020-03-20
 */
public interface MemberMapper extends BaseMapper<Member> {

	@Select("select currency_id from member where id = #{memberId}")
	@ResultType(Long.class)
	Long selectMemberCurrency(@Param("memberId")Long memberId);
	
	@Update("update member set currency_id = #{baseId} where id = #{memberId}")
	int updateMmeberCurrency(@Param("memberId")Long memberId , @Param("baseId")Long baseId);
}
