package com.spark.bitrade.mapper;

import com.spark.bitrade.entity.OtcCoin;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * (OtcCoin)表数据库访问层
 *
 * @author ss
 * @date 2020-03-19 10:23:46
 */
public interface OtcCoinMapper extends BaseMapper<OtcCoin>{

	@Select("select * from otc_coin where status = 0")
   List<OtcCoin> selectOpenOtcCoin();

    /**
     *
     * @param id
     * @return
     */
    List<Map<String, Object>> getAllNormalCoinAndBalance(@Param("memberId") Long id);
}
