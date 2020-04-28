package com.spark.bitrade.controller;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.PaySetting;
import com.spark.bitrade.service.PaySettingService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * <p>
 * 支付方式配置 前端控制器
 * </p>
 *
 * @author qiliao
 * @since 2020-03-18
 */
@RestController
@RequestMapping("api/v2/paySetting")
@Api(tags = "系统收付款配置")
public class PaySettingController {

	@Resource
	private PaySettingService paySettingService;
	
	/**
	 * 获取当前系统可用的支付方式
	 * @return
	 * @param baseId 法币id
	 * @author zhaopeng
	 * @since 2020年3月18日
	 */
	@RequestMapping(value = "/getPaySettings" , method = RequestMethod.POST)
	@ApiOperation(value = "获取当前系统可用的支付方式", tags = "系统收付款配置")
	public MessageRespResult<List<PaySetting>> getPaySettings(@ApiParam(value = "法币id，如果不指定，则返回全部支付方式，指定时，返回指定法币绑定的支付方式" ,required = false)@RequestParam(name = "baseId" , defaultValue = "-1" ,required = false)Long baseId) {
		return this.paySettingService.getAllOpenPaySetting(baseId);
	}
}

