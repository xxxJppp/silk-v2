package com.spark.bitrade.service;

import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberCapSetting;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;

import io.swagger.annotations.ApiParam;

/**
 * <p>
 * 用户支付方式对应关系（配置内容与原有结构应保持一致） 服务类
 * </p>
 *
 * @author qiliao
 * @since 2020-03-18
 */
public interface MemberCapSettingService extends IService<MemberCapSetting> {

	MessageRespResult<JSONObject> queryCapSettingByMember(Member member);
	
	MessageRespResult<Boolean> lockCapSetting(String payCode ,String moneyPassword ,String payKey , Member member);
	
	MessageRespResult<Boolean> unLockCapSetting(String moneyPassword ,String payKey , Member member);
	
	MessageRespResult<JSONObject> queryMemberVerified(Member member);
}
