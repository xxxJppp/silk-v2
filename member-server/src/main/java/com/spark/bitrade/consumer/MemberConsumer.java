package com.spark.bitrade.consumer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spark.bitrade.config.RocketMQCfg;
import com.spark.bitrade.consumer.base.BaseMemberConsumer;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.service.MemberInviteService;
import com.spark.bitrade.vo.OtcOrderVo;

import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class MemberConsumer extends BaseMemberConsumer {
	
	@Autowired
	private RocketMQCfg rocketMQCfg;
	
	@Autowired
	private MemberInviteService memberInviteService;


	

	@Override
	public void init() {
		this.consumerGroup = this.rocketMQCfg.getMemberConsumerGroup();
		this.topic = this.rocketMQCfg.getMemberTopic();
		this.tag = this.rocketMQCfg.getMemberTag();


		log.info("======================ExchangeFeeDistributeConsumer init end==========");
	}

	@Override
	public boolean consumeMember(Member member,String msgId) {
		
		List<Long> inviterIdList = this.memberInviteService.createMemberInviteChain(member.getId());
		if(null != inviterIdList) return true;
		return true;
	}

	@Override
	public boolean consumeOtcOrder(OtcOrderVo order , String messageId) {
		return true;
	}

	
}
