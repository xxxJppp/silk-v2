package com.spark.bitrade.consumer;

import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.otter.canal.common.utils.JsonUtils;
import com.spark.bitrade.config.RocketMQCfg;
import com.spark.bitrade.constant.PayTypeEnum;
import com.spark.bitrade.consumer.base.BaseAppBizConsumer;
import com.spark.bitrade.entity.MemberBenefitsOrder;
import com.spark.bitrade.entity.MemberExtend;
import com.spark.bitrade.param.MemberBenefitsOrderReceipt;
import com.spark.bitrade.service.MemberBenefitsExtendsService;
import com.spark.bitrade.service.MemberRecommendCommisionService;
import com.spark.bitrade.utils.MixUtil;
import com.spark.bitrade.vo.OtcOrderVo;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class RecommendCommisionConsumer extends BaseAppBizConsumer {
	
	@Autowired
	private RocketMQCfg rocketMQCfg;
	
	@Autowired
	private MemberRecommendCommisionService memberRecommendCommisionService;
	
	@Autowired
	private MemberBenefitsExtendsService memberBenefitsExtendsService;

	/**
	 * 会员购买推荐人返佣
	 */
	@Override
	public boolean consumeNonCanalMessage(String message,String msgId) {
 		log.info("========RecommendCommisionConsumer========="  + ", message id:" + msgId);
 		try {
			Thread.sleep(MixUtil.getRandom());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			MemberBenefitsOrder order = JsonUtils.unmarshalFromString(message, MemberBenefitsOrder.class);
			
			MemberBenefitsOrderReceipt orderReceipt = new MemberBenefitsOrderReceipt();
			orderReceipt.setRefId(order.getOrderNumber());
			
			MemberExtend m = this.memberBenefitsExtendsService.getMemberExtendById(order.getMemberExtendId());
			if(m == null) {
				throw new RuntimeException("Not found order member Id:" + order.toString() + ", message id:" + msgId);
			}
			orderReceipt.setOrderMemberId(m != null ? m.getMemberId():0);
			
			orderReceipt.setCommisionUnit(order.getUnit());
			orderReceipt.setCommisionQuantity(order.getAmount());
			orderReceipt.setPayType(order.getPayType());
			orderReceipt.setPayTime(order.getPayTime());
			
			if(orderReceipt.getPayType() == PayTypeEnum.LOCK.getCode()) {
				if(null == order.getStartTime() || null == order.getEndTime()) {
					throw new RuntimeException("lock period couldn't be null......");
				}
				orderReceipt.setLockDay(DateUtil.betweenDay(order.getStartTime(), order.getEndTime(), true));
			}
			orderReceipt.setMqMsgId(msgId);
			this.memberRecommendCommisionService.distributeBenefitsOrder(orderReceipt, order);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		
		return false;
	}

	@Override
	public void init() {
		this.consumerGroup = this.rocketMQCfg.getRecommendCommisionConsumerGroup();
		this.topic = this.rocketMQCfg.getMemberTopic();
		this.tag = this.rocketMQCfg.getMemberTag();

		log.info("======================RecommendCommisionConsumer init end==========");
	}

	@Override
	public boolean consumeOtcOrder(OtcOrderVo order , String messageId) {
		return true;
	}

}
