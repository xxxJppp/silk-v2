package com.spark.bitrade.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.ToString;

@Component
@ConfigurationProperties(prefix = "mq")
@Data
@ToString(includeFieldNames=true)
public class RocketMQCfg {

	private String nameSrvAddr;
	private String canalMessageTopic;
	private String memberTopic;
	private String memberTag;
	private String canalMessageTag;
	private String memberProducerGroupId;
	private String tableAccountRunning;
	
	private String tableMember;
	
	private String tableOtcorder;
	
	
	private String memberFeeDailyStatConsumerGroup;
	private String recommendCommisionConsumerGroup;
	private String exchangeFeeDistributeConsumerGroup;
	private String memberConsumerGroup;
	private String otcConsumerGroup;
}
