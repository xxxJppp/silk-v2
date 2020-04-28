package com.spark.bitrade.mapper;

import com.spark.bitrade.entity.CurrencyManage;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 法币管理 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2020-03-18
 */
public interface CurrencyManageMapper extends BaseMapper<CurrencyManage> {

	@Select("select * from currency_manage where id = #{id}")
	CurrencyManage selectManageById(@Param("id") Long id);
}
