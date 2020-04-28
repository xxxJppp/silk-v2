package com.spark.bitrade.controller;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.entity.RiskControlSettings;
import com.spark.bitrade.service.RiskControlSettingsService;
import com.spark.bitrade.util.MessageRespResult;

import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 风险控制设置内容 前端控制器
 * </p>
 *
 * @author qiliao
 * @since 2020-02-25
 */
@RestController
@RequestMapping("/api/v2/riskControlSettings")
public class RiskControlSettingsController {

	@Resource
	private RiskControlSettingsService riskControlSettingsService;
	
	@ApiOperation(tags = "公共服务" ,value = "风控设置项")
	@RequestMapping(value ="/list",method = {RequestMethod.POST,RequestMethod.GET})
	public IPage<RiskControlSettings> list(int pageNum , int size) {
		return this.riskControlSettingsService.page(new Page<RiskControlSettings>(pageNum ,size));
	}
	
}

