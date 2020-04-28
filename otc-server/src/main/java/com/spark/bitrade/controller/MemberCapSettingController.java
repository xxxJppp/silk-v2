package com.spark.bitrade.controller;


import javax.annotation.Resource;

import com.spark.bitrade.service.AdvertiseService;
import com.spark.bitrade.service.IMemberApiService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.service.MemberCapSettingService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * <p>
 * 用户支付方式对应关系（配置内容与原有结构应保持一致） 前端控制器
 * </p>
 *
 * @author qiliao
 * @since 2020-03-18
 */
@RestController
@RequestMapping("api/v2/memberCapSetting")
@Api(tags = "用户收付款相关")
public class MemberCapSettingController {

	@Resource
	private MemberCapSettingService memberCapSettingService;
	@Resource
	private IMemberApiService memberApiService;
	@Resource
	private AdvertiseService advertiseService;
	/**
	 * 获取用户支付配置信息
	 * 由V1转移到当前服务
	 * @param member
	 * @return
	 * @author zhaopeng
	 * @since 2020年3月18日
	 */
	@RequestMapping(value = "/queryCapSettingByMember" , method = RequestMethod.POST)
	 @ApiOperation(value = "获取用户支付配置信息", tags = "用户收付款相关")
	public MessageRespResult<JSONObject> queryCapSettingByMember(@ApiParam(value = "个人登录信息")@MemberAccount Member member) {
		return this.memberCapSettingService.queryCapSettingByMember(member);
	}

	@RequestMapping(value = "/queryMemberVerified" , method = RequestMethod.POST)
	 @ApiOperation(value = "获取用户效验信息", tags = "用户收付款相关")
	public MessageRespResult<JSONObject> queryMemberVerified(@ApiParam(value = "个人登录信息")@MemberAccount Member member) {
		return this.memberCapSettingService.queryMemberVerified(member);
	}

	/**
	 * 获取用户支付配置信息
	 * 由V1转移到当前服务(内部接口，供v1调用)
	 * @param memberId
	 * @return
	 * @author ss
	 * @since 2020年3月28日
	 */
	@RequestMapping(value = "/queryCapSettingByMemberId" , method = RequestMethod.POST)
	public MessageRespResult<JSONObject> queryCapSettingByMember(Long memberId) {
		Member member = memberApiService.getMember(memberId).getData();
		return this.memberCapSettingService.queryCapSettingByMember(member);
	}

	/**
	 *
	 * @param moneyPassword
	 * @param payKey
	 * @param member
	 * @return
	 */
	@RequestMapping(value = "/unLockCapSetting" , method = RequestMethod.POST)
	 @ApiOperation(value = "解绑指定支付方式", tags = "用户收付款相关")
	public MessageRespResult<Boolean> unLockCapSetting(@ApiParam(value = "支付密码" ,required = true)@RequestParam("pass") String moneyPassword ,@ApiParam(value = "支付键" ,required = true , example = "aliPay")@RequestParam("payKey")String payKey ,@ApiParam(value = "个人登录信息")@MemberAccount Member member ) {
		CurrencyManageController.validatePassword(moneyPassword,member.getJyPassword(),member.getSalt());
		advertiseService.checkOrderAndPutOn(member.getId());
		return this.memberCapSettingService.unLockCapSetting(moneyPassword, payKey, member);
	}

	/**
	 *
	 * @param payCode
	 * @param moneyPassword
	 * @param payKey
	 * @param member
	 * @return
	 */
	@RequestMapping(value = "/lockCapSetting" , method = RequestMethod.POST)
	 @ApiOperation(value = "绑定指定支付方式", tags = "用户收付款相关")
	public MessageRespResult<Boolean> lockCapSetting(@ApiParam(value = "支付信息支付方式内容的json字符串" ,required = true , example = "{\"bank\":\"xxxxxxx\",\"card\":\"2564845555666\"}")@RequestParam("payCode")String payCode ,@ApiParam(value = "支付密码" , required = true)@RequestParam("pass") String moneyPassword ,@ApiParam(value = "支付键" ,example = "bankInfo")@RequestParam("payKey")String payKey ,@ApiParam(value = "个人登录信息")@MemberAccount Member member ) {
		CurrencyManageController.validatePassword(moneyPassword,member.getJyPassword(),member.getSalt());

		return this.memberCapSettingService.lockCapSetting(payCode, moneyPassword, payKey, member);
	}

}

