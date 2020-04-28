package com.spark.bitrade.producer;

import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.spark.bitrade.config.RocketMQCfg;
import com.spark.bitrade.vo.MQMessage;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Producer {
	private DefaultMQProducer producer;
	
	@Autowired
	private RocketMQCfg rocketMqCfg;

	@PostConstruct
	public void init() {
		producer = new DefaultMQProducer(this.rocketMqCfg.getMemberProducerGroupId());
		producer.setNamesrvAddr(this.rocketMqCfg.getNameSrvAddr());
		try {
			producer.start();
			log.info("producer started success");
		} catch (MQClientException e) {
			e.printStackTrace();
			log.error("producer started failed");
		}
	}

	public String send(MQMessage mqMsg) {
		Message msg = null;
		try {
			msg = new Message(mqMsg.getTopic(), mqMsg.getTag(), mqMsg.getMessage().getBytes(RemotingHelper.DEFAULT_CHARSET));
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			SendResult sendResult = producer.send(msg);
			SendStatus sendStatus = sendResult.getSendStatus();
			if( sendStatus ==  SendStatus.SEND_OK) {
				
				String s = null;
				try {
					s = new String(msg.getBody(),"utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					log.error(e.getMessage());
				}
				log.info(s + "==============producer send message" + sendResult.getMsgId() + "================" + mqMsg.getTopic());
				return sendResult.getMsgId();
			}
		} catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
			e.printStackTrace();
			log.error("send message + " + mqMsg.getMessage() + " failed");
		}
		return null;
	}

	@PreDestroy
	public void destory() {
		producer.shutdown();
		log.info("producer stoped");
	}
}
