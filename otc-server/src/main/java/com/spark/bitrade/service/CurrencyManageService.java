package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.CurrencyManage;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.util.MessageRespResult;

/**
 * <p>
 * 法币管理 服务类
 * </p>
 *
 * @author qiliao
 * @since 2020-03-18
 */
public interface CurrencyManageService extends IService<CurrencyManage> {

	
	MessageRespResult<CurrencyManage> getMemberPaySetting(Member member);
	
	MessageRespResult<String> setMemberPaySetting(Member member , Long baseId);
	
	MessageRespResult<CurrencyManage> updateMmeberCurrenc(Member member , Long baseId);
}
