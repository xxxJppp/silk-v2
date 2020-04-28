package com.spark.bitrade.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.MemberExtend;
import com.spark.bitrade.entity.MemberBenefitsExtends;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 * 会员扩展表，与原member表一对一 Mapper 接口
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Mapper
public interface MemberBenefitsExtendsMapper extends BaseMapper<MemberBenefitsExtends> {

	@Select("select id, member_id, level_id from member_benefits_extends where member_id = #{memberId}")
	public MemberExtend getMemberBenefitsExtends(long memberId);
	
	@Select("select id, member_id, level_id from member_benefits_extends where id = #{memberExtendId}")
	public MemberExtend getMemberExtend(long memberExtendId);

	MemberBenefitsExtends getMemberBenefitsExtendsByMemberId(@Param("memberId") Long memberId);
	
	@Update("update member_benefits_extends set level_id = 1 where id = #{eid}")
	void cheakBenefitsExtendsById(@Param("eid") Long id);

	@Select("select id, member_id from member_benefits_extends where level_id != 1 and end_time <= now()")
	List<MemberExtend> getBenefitsExtendsLessThanEndTime();
	
	@Select("select me.id, me.member_id, me.level_id from member_benefits_extends me where member_id = (select m.inviter_id from member m where m.id = #{memberId})")
	MemberBenefitsExtends getSuperiorAccountLevelId(@Param("memberId")Long memberId);
}
