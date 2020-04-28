package com.spark.bitrade.consumer;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spark.bitrade.config.RocketMQCfg;
import com.spark.bitrade.constant.BizTypeEnum;
import com.spark.bitrade.consumer.base.BaseAccountRunningConsumer;
import com.spark.bitrade.param.ExchangeOrderReceipt;
import com.spark.bitrade.service.MemberRecommendCommisionService;
import com.spark.bitrade.utils.MixUtil;
import com.spark.bitrade.vo.AccountRunning;
import com.spark.bitrade.vo.OtcOrderVo;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class ExchangeFeeDistributeConsumer extends BaseAccountRunningConsumer {
	
	@Autowired
	private RocketMQCfg rocketMQCfg;


	@Autowired
	private MemberRecommendCommisionService memberRecommendCommisionService;


	private  boolean process(AccountRunning ar,String msgId)  {
		log.info ("===ExchangeFeeDistributeConsumer==== message id:" + msgId);
		if(ar.getFee().compareTo(BigDecimal.ZERO) == 0) {
			log.info(ar.toString() + " found fee is 0, skip ...");
			return true;
		}

		// 剔除买入ESP 手续费返佣
		if ("ESP".equals(ar.getCoinUnit())) {
			log.info(ar.toString() + " ESP 不返佣....");
			return true;
		}

		// 剔除卖ESP 手续费返佣
		if ("USDT".equals(ar.getCoinUnit()) && ar.getRefId().startsWith("R")) {
			log.info(ar.toString() + " ESP 不返佣....");
			return true;
		}



		try {
			this.memberRecommendCommisionService.distributeExchageOrderFee(new ExchangeOrderReceipt(ar.getRefId(), ar.getMemberId(), ar.getCoinUnit(), ar.getOrderMatchType(),
					ar.getFee(), ar.getCreateTime(), msgId));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage() , e);
		}
		return false;
	}

	
	/**
	 * 币币交易手续费返佣-消费Canal Message
	 */
	@Override
	public boolean consumeAccountRunning(AccountRunning ar,String msgId) {
		log.debug(ar.toString());
		try {
			Thread.sleep(MixUtil.getRandom());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return this.process(ar,msgId);
	}

	@Override
	public void init() {
		this.consumerGroup = this.rocketMQCfg.getExchangeFeeDistributeConsumerGroup();
		this.topic = this.rocketMQCfg.getCanalMessageTopic();
		this.tag = this.rocketMQCfg.getCanalMessageTag();

		log.info("======================ExchangeFeeDistributeConsumer init end==========");
	}


	@Override
	public boolean consumeOtcOrder(OtcOrderVo order , String messageId) {
		return true;
	}

	
}
