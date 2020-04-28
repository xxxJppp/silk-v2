package com.spark.bitrade.mapper;

import com.spark.bitrade.entity.MemberRecommendCommisionSetting;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;


public interface MemberRecommendCommisionSettingMapper extends BaseMapper<MemberRecommendCommisionSetting> {

	@Select("select level_id, recommend_level,commision_ratio from member_recommend_commision_setting where level_id = #{level}")
	public List<MemberRecommendCommisionSetting> getCommisionByLevel(int level);
}
