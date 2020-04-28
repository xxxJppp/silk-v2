package com.spark.bitrade.service;

import com.spark.bitrade.entity.RiskSummary;
import com.spark.bitrade.util.MessageRespResult;

import io.swagger.annotations.ApiParam;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 风控出入金汇总 服务类
 * </p>
 *
 * @author qiliao
 * @since 2020-02-25
 */
public interface RiskSummaryService extends IService<RiskSummary> {

	public IPage<RiskSummary> list(Long memberId , 
										             String phone ,
										             String outTimeStart , 
										             String outTimeEnd ,
										             Double coefficientStart , 
										             Double coefficientEnd ,
										             int pageNum ,
										             int pageSize);
	
}
