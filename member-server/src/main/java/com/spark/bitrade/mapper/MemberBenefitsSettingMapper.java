package com.spark.bitrade.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.MemberBenefitsSetting;

/**
 * <p>
 * 会员权益表 Mapper 接口
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Mapper
public interface MemberBenefitsSettingMapper extends BaseMapper<MemberBenefitsSetting> {

//    List<MemberBenefitsSettingVo> secletSettingVoList();
	
	@Select("select * from member_benefits_setting where level_id = #{levelId}")
	public MemberBenefitsSetting getBenefitsSettingByMemberLevel(int levelId);
}
