package com.spark.bitrade.controller;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.RiskSummary;
import com.spark.bitrade.service.RiskSummaryService;
import com.spark.bitrade.util.MessageRespResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * <p>
 * 风控出入金汇总 前端控制器
 * </p>
 *
 * @author qiliao
 * @since 2020-02-25
 */
@RestController
@RequestMapping("/api/v2/riskSummary")
@Api(value = "风控用户出入金汇总")
public class RiskSummaryController {

	@Resource
	private RiskSummaryService riskSummaryService;
	
	@ApiOperation(tags = "公共服务" ,value = "用户出入金汇总列表")
	@RequestMapping(value ="/list",method = {RequestMethod.POST,RequestMethod.GET})
	public IPage<RiskSummary> list(@RequestParam(name = "memberId" , required = false , defaultValue = "-1") @ApiParam(value = "用户id")Long memberId , 
										             @RequestParam(name = "phone" , required = false , defaultValue = "")@ApiParam(value = "电话号码")String phone ,
										             @RequestParam(name = "outTimeStart" , required = false , defaultValue = "")@ApiParam(value = "出场时间开始 yyyy-MM-dd HH:mm:ss")String outTimeStart , 
										             @RequestParam(name = "outTimeEnd" , required = false , defaultValue = "")@ApiParam(value = "出场时间结束 yyyy-MM-dd HH:mm:ss")String outTimeEnd ,
										             @RequestParam(name = "coefficientStart" , required = false , defaultValue = "-1")@ApiParam(value = "风险系数起始")Double coefficientStart , 
										             @RequestParam(name = "coefficientEnd" , required = false , defaultValue = "-1")@ApiParam(value = "风险系数结束")Double coefficientEnd ,
										             int pageNum , 
										             int size) {
		return this.riskSummaryService.list(memberId, phone, outTimeStart, outTimeEnd, coefficientStart, coefficientEnd , pageNum , size);
	}
	

	
	@ApiOperation(tags = "公共服务" ,value = "风险调整页面数据")
	@RequestMapping(value ="/addDetailedIndex",method = {RequestMethod.POST,RequestMethod.GET})
	public MessageRespResult<RiskSummary> addDetailedIndex(@RequestParam("memberId") @ApiParam(value = "用户id")String memberId) {
		QueryWrapper<RiskSummary> queryWrapper = new QueryWrapper<RiskSummary>();
		queryWrapper.eq("member_id", memberId);
		return MessageRespResult.success("", this.riskSummaryService.getOne(queryWrapper));
	}
}

