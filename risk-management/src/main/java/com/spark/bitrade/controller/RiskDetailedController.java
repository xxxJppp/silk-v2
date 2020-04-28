package com.spark.bitrade.controller;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.RiskDetailed;
import com.spark.bitrade.entity.RiskSummary;
import com.spark.bitrade.service.RiskDetailedService;
import com.spark.bitrade.service.RiskSummaryService;
import com.spark.bitrade.util.MessageRespResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * <p>
 * 风控出入金明细 前端控制器
 * </p>
 *
 * @author qiliao
 * @since 2020-02-25
 */
@RestController
@RequestMapping("/api/v2/riskDetailed")
@Api(value = "风控用户出入金明细")
public class RiskDetailedController {
	
	@Resource
	private RiskDetailedService riskDetailedService;
	
	@ApiOperation(tags = "公共服务" ,value = "用户出入金明细列表")
	@RequestMapping(value ="/list",method = {RequestMethod.POST,RequestMethod.GET})
	public IPage<RiskDetailed> list(@RequestParam(name = "memberId" , required = true) @ApiParam(value = "用户id")Long memberId , 
													  @RequestParam(name = "inOut" , required = false , defaultValue = "0") @ApiParam(value = "0出金1入金")String inOut ,
													  @RequestParam(name = "typeDesc" , required = false , defaultValue = "0") @ApiParam(value = "出入金类型")String typeDesc,
													  @RequestParam(name = "unit" , required = false , defaultValue = "0") @ApiParam(value = "币种")String unit,
													  @RequestParam(name = "timeStart" , required = false , defaultValue = "0") @ApiParam(value = "时间起始yyyy-MM-dd HH:mm:ss")String timeStart ,
													  @RequestParam(name = "timeEnd" , required = false , defaultValue = "0") @ApiParam(value = "时间结束yyyy-MM-dd HH:mm:ss")String timeEnd,
													  int pageNum ,
													  int size) {
		return this.riskDetailedService.list(memberId, inOut,typeDesc , unit , timeStart , timeEnd, pageNum, size);
	}
	
	@ApiOperation(tags = "公共服务" ,value = "出入金风险调整")
	@RequestMapping(value ="/addDetailed",method = {RequestMethod.POST,RequestMethod.GET})
	public MessageRespResult<Boolean> addDetailed(@RequestParam(name = "memberId" , required = true) @ApiParam(value = "用户id")Long memberId , 
												 @RequestParam(name = "inOut" , required = true) @ApiParam(value = "0出金1入金")String inOut ,
												 @RequestParam(name = "money" , required = true ) @ApiParam(value = "出入金额")double money ,
												 @RequestParam(name = "desc" , required = true) @ApiParam(value = "备注")String desc ,
												 @RequestParam(name = "customerId" , required = true) @ApiParam(value = "操作用户id")Long customerId) {
		return this.riskDetailedService.addDetailed(memberId, inOut, money, desc, customerId);
	}
}

