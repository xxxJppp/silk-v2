package com.spark.bitrade.service;

import com.spark.bitrade.entity.CurrencyManage;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.PaySetting;
import com.spark.bitrade.util.MessageRespResult;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 支付方式配置 服务类
 * </p>
 *
 * @author qiliao
 * @since 2020-03-18
 */
public interface PaySettingService extends IService<PaySetting> {

	MessageRespResult<List<PaySetting>> getAllOpenPaySetting(Long baseId);

	/**
	 * 通过法币获取法币的交易方式的paykey
	 * @param cm
	 * @return
	 */
	String getPaySettingByCurrency(CurrencyManage cm);
}
