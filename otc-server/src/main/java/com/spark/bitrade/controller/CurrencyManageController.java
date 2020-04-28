package com.spark.bitrade.controller;


import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.CurrencyManage;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.OtcCoin;
import com.spark.bitrade.entity.PaySetting;
import com.spark.bitrade.service.CurrencyManageService;
import com.spark.bitrade.service.OtcCoinService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * <p>
 * 法币管理 前端控制器
 * </p>
 *
 * @author qiliao
 * @since 2020-03-18
 */
@RestController
@RequestMapping("api/v2/currencyManage")
@Api(tags = "法币相关")
public class CurrencyManageController {

	@Resource
	private CurrencyManageService currencyManageService;

	/**
	 * 获取用户的默认法币
	 * @param member
	 * @return
	 */
	@RequestMapping(value = "/getMemberCurrency" , method = RequestMethod.POST)
    @ApiOperation(value = "获取用户的默认法币", tags = "法币相关")
	public MessageRespResult<CurrencyManage> getMemberCurrenc(@ApiParam(value = "个人登录信息")@MemberAccount Member member) {
		return this.currencyManageService.getMemberPaySetting(member);
	}
	
	@RequestMapping(value = "/getMemberCurrencyUnitById" , method = RequestMethod.POST)
    @ApiOperation(value = "服务内部调用", tags = "法币相关")
	public String getMemberCurrencyUnitById(@RequestParam("memberId")Long memberId) {
		Member member = new Member();
		member.setId(memberId);
		MessageRespResult<CurrencyManage> result = this.currencyManageService.getMemberPaySetting(member);
		return result.getData() == null ? "":result.getData().getUnit();
	}

	/**
	 * 获取用户的默认法币(内部接口供v1的otc调用)
	 * @param memberId
	 * @return
	 */
	@RequestMapping(value = "/getMemberCurrencyByMemberId" , method = RequestMethod.POST)
    @ApiOperation(value = "获取用户的默认法币", tags = "法币相关")
	public MessageRespResult<CurrencyManage> getMemberCurrencyByMemberId(Long memberId) {
		Member member = new Member();
		member.setId(memberId);
		return this.currencyManageService.getMemberPaySetting(member);
	}

	/**
	 * 根据法币ID获取法币信息(内部接口供v1的otc调用)
	 * @param currencyId
	 * @return
	 */
	@RequestMapping(value = "/getCurrencyById" , method = RequestMethod.POST)
	public MessageRespResult<CurrencyManage> getCurrencyById(Long currencyId) {
		return MessageRespResult.success4Data(currencyManageService.getById(currencyId));
	}

	/**
	 * 全部法币列表
	 * @return
	 */
	@RequestMapping(value = "/no-auth/getAllCurrency" , method = RequestMethod.POST)
	 @ApiOperation(value = "全部法币列表", tags = "法币相关")
	public MessageRespResult<List<CurrencyManage>> getAllCurrency() {
		return MessageRespResult.success("", this.currencyManageService.list(new QueryWrapper<CurrencyManage>().eq("currency_state", "1").orderByAsc("currency_order")));
	}

	/**
	 * 首次设置默认法币
	 * @param member
	 * @param baseId
	 * @return
	 */
	@RequestMapping(value = "/setMemberCurrency", method = RequestMethod.POST)
	@ApiOperation(value = "首次设置默认法币", tags = "法币相关")
	public MessageRespResult<String> setMemberCurrenc(@ApiParam(value = "支付密码" ,required = true)@RequestParam("pass") String moneyPassword ,@ApiParam(value = "个人登录信息")@MemberAccount Member member , @ApiParam(value = "法币id" ,required = true)@RequestParam("baseId")Long baseId) {
		validatePassword(moneyPassword,member.getJyPassword(),member.getSalt());
		return this.currencyManageService.setMemberPaySetting(member, baseId);
	}

	/**
	 * 更改默认法币
	 * @param member
	 * @param baseId
	 * @param moneyPassword
	 * @return
	 */
	@RequestMapping(value = "/updateMmeberCurrenc", method = RequestMethod.POST)
	@ApiOperation(value = "修改默认法币", tags = "法币相关")
	public MessageRespResult<CurrencyManage> updateMmeberCurrenc(@ApiParam(value = "个人登录信息")@MemberAccount Member member ,
																 @ApiParam(value = "法币id" ,required = true)@RequestParam("baseId")Long baseId ,
																 @ApiParam(value = "支付密码" ,required = true)@RequestParam("pass") String moneyPassword) {
		validatePassword(moneyPassword,member.getJyPassword(),member.getSalt());
		return this.currencyManageService.updateMmeberCurrenc(member, baseId);
	}

	public static void validatePassword(String moneyPassword, String jyPassword, String salt) {
        AssertUtil.hasText(moneyPassword, CommonMsgCode.MISSING_JYPASSWORD);
        AssertUtil.hasText(jyPassword, CommonMsgCode.NO_SET_JYPASSWORD);
        String jyPass = new SimpleHash("md5", moneyPassword, salt, 2).toHex().toLowerCase();
        AssertUtil.isTrue(jyPassword.equals(jyPass), CommonMsgCode.ERROR_JYPASSWORD);
    }

	//币种法币对应
	@Resource
	private OtcCoinService otcCoinService;

	@RequestMapping(value = "/no-auth/selectCoinForCurrency", method = RequestMethod.POST)
	@ApiOperation(value = "根据币种获取对应法币集合", tags = "法币相关")
	public MessageRespResult<List<Map<String, Object>>> selectCoinForCurrency() {
		return this.otcCoinService.selectCoinForCurrency();
	}

	/*@RequestMapping(value = "/selectCurrencyForCoin", method = RequestMethod.POST)
	@ApiOperation(value = "根据法币获取对应币种集合", tags = "法币相关")
	public MessageRespResult<Map<String, List<OtcCoin>>> selectCurrencyForCoin(@ApiParam(value = "法币id（字符串）" ,required = true)@RequestParam("baseId")String[] baseId) {
		return this.otcCoinService.selectCurrencyForCoin(baseId);
	}*/
}

