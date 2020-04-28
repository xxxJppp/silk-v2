package com.spark.bitrade.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.otter.canal.common.utils.JsonUtils;
import com.spark.bitrade.config.RocketMQCfg;
import com.spark.bitrade.constant.PayTypeEnum;
import com.spark.bitrade.consumer.base.BaseAppBizConsumer;
import com.spark.bitrade.entity.MemberBenefitsOrder;
import com.spark.bitrade.entity.MemberFeeDayStat;
import com.spark.bitrade.service.GlobalConfService;
import com.spark.bitrade.service.MemberFeeDayStatService;
import com.spark.bitrade.vo.OtcOrderVo;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class MemberFeeDailyStatConsumer extends BaseAppBizConsumer {
	
	@Autowired
	private RocketMQCfg rocketMQCfg;
	
	@Autowired
	private MemberFeeDayStatService memberFeeDayStatService;
	
	@Autowired
	private GlobalConfService globalConfService;
	
	
	/**
	 * 维护日统计数据
	 */
	@Override
	public boolean consumeNonCanalMessage(String message,String msgId) {
		log.info("========MemberFeeDailyStatConsumer======" + ", message id:" + msgId);
		MemberBenefitsOrder order = JsonUtils.unmarshalFromString(message, MemberBenefitsOrder.class);
		MemberFeeDayStat stat = new MemberFeeDayStat();
		
		if(order.getPayType().intValue() == PayTypeEnum.BUY.getCode()) {
			stat.setBuyCount(1l);
			stat.setBuyUnitQuantity(order.getAmount());
		}
		
		if(order.getPayType().intValue() == PayTypeEnum.LOCK.getCode()) {
			stat.setLockCount(1l);
			stat.setLockUnitQuantity(order.getAmount());
		}
		
		stat.setUnit(this.globalConfService.getMemberRecommendCommisionUnit());
		
		stat.setStatisticDate(order.getCreateTime());
		
		
		boolean flag = this.memberFeeDayStatService.updateDailyStat(stat);
		
		return flag;
	}

	@Override
	public void init() {
		this.consumerGroup = this.rocketMQCfg.getMemberFeeDailyStatConsumerGroup();
		this.topic = this.rocketMQCfg.getMemberTopic();
		this.tag = this.rocketMQCfg.getMemberTag();

		log.info("======================MemberFeeDailyStatConsumer init end==========");
	}

	@Override
	public boolean consumeOtcOrder(OtcOrderVo order , String messageId) {
		return true;
	}
	
}
