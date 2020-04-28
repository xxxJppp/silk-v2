package com.spark.bitrade.mapper;

import com.spark.bitrade.entity.RiskSummary;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 风控出入金汇总 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2020-02-25
 */
public interface RiskSummaryMapper extends BaseMapper<RiskSummary> {

}
