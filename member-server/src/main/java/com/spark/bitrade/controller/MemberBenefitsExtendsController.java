package com.spark.bitrade.controller;


import java.util.List;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spark.bitrade.api.MemberFeignApi;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.PageMemberVo;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.service.MemberInviteService;
import com.spark.bitrade.util.HttpRequestUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;

import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 会员扩展表，与原member表一对一 前端控制器
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@RestController
@RequestMapping("api/v2/member/memberBenefitsExtends")
@Api(tags = "会员个人中心接口")
public class MemberBenefitsExtendsController {
	
	@Autowired
	private MemberInviteService memberInviteService;

	@Autowired
	private MemberFeignApi memberFeignApi;
	

	/**
	 * 个人中心查询邀请人员列表
	 *
	 * @return
	 */
	@ApiOperation(value = "个人中心查询邀请人员列表", notes = "个人中心查询邀请人员列表")
	@PostMapping("/inviterList")
	public MessageRespResult<PageMemberVo> findMemberInviter(@MemberAccount Member member, PageParam param) {
		String apiKey = HttpRequestUtil.getApiKey();
		MessageRespResult<PageMemberVo> record = memberFeignApi.findInvitationRecord(0, apiKey, param.getPage(), param.getPageSize());
		return record;
	}
}
