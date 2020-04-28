package com.spark.bitrade.mapper;

import com.spark.bitrade.entity.NewYearMemberHas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 用户矿石持有表 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
public interface NewYearMemberHasMapper extends BaseMapper<NewYearMemberHas> {

	@Select("select mineral_name as mn ,count(*) as ct from new_year_member_has where member_id = #{memberId} and status =1 group by mineral_name")
	List<Map<String, Object>> myOreCount(@Param("memberId")Long memberId);
}
