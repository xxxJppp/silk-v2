package com.spark.bitrade.common.consumer;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.spark.bitrade.common.message.CanalMessage;

/**
 * 
 * <p>
 * cannel投递到rocketmq的消息在当前类中做消费
 * </p>
 *
 * @author 赵鹏
 * @since 2020年1月7日
 */
//@Component
//暂时不启用功能
public class EventConsumer {

	@Value("${mq.group}")
	private String mqGroup;
	@Value("${mq.addr}")
	private String mqAddr;
	@Value("${mq.topic}")
	private String mqTopic;
	@Value("${mq.tag}")
	private String mqTag;

	private Gson gson;

	private Log logger = LogFactory.getLog(this.getClass());

	@PostConstruct
	public void consumerInit() {
		gson = new Gson();
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(mqGroup);
		consumer.setNamesrvAddr(mqAddr);
		try {
			consumer.subscribe(mqTopic, mqTag);
			//consumer.setMessageModel(MessageModel.BROADCASTING);
			consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
			logger.info("start consumer -> " + mqAddr + " " + mqTopic + " " + mqGroup + " " + mqTag);
			consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
				boolean consumeSuccess = false;
				for (Message msg : msgs) {
					try {
						String s = new String(msg.getBody(), "utf-8");
						CanalMessage cm = gson.fromJson(s, new TypeToken<CanalMessage>() {
						}.getType());
						cm.setMessageId(msg.getProperty(MessageConst.PROPERTY_UNIQ_CLIENT_MESSAGE_ID_KEYIDX));
						if (cm.getType().equals("ALTER") || cm.getType().equals("CREATE")) {
							return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
						}
						// List<Map<String, Object>> list = cm.getData();
						logger.info(cm.getType() + "     收到消息---->" + JSON.toJSONString(cm));
						consumeSuccess = true;
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return consumeSuccess ? ConsumeConcurrentlyStatus.CONSUME_SUCCESS
						: ConsumeConcurrentlyStatus.RECONSUME_LATER;
			});
			consumer.start();
			logger.info("consumer start success ...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
