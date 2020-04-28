package com.spark.bitrade.consumer.base;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.reflect.TypeToken;
import com.spark.bitrade.config.RocketMQCfg;
import com.spark.bitrade.constant.Constant;
import com.spark.bitrade.convertor.ObjectConvertor;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.utils.ClazzUtil;
import com.spark.bitrade.utils.JsonUtil;
import com.spark.bitrade.vo.AccountRunning;
import com.spark.bitrade.vo.CanalAccountRunning;
import com.spark.bitrade.vo.CanalMember;
import com.spark.bitrade.vo.CanalMessage;
import com.spark.bitrade.vo.OtcOrderVo;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public abstract class ConsumerService {

	@Autowired
    private RocketMQCfg rocketMQCfg;
    
    @Setter
    @Getter
    protected String consumerGroup;
    
    @Getter
    @Setter
    protected String topic;
    
    @Getter
    @Setter
    protected String tag;
    
    @Autowired
    private RedisTemplate redisTemplate;
	    
    @PostConstruct
    public void defaultMQPushConsumer() {
    	init();
    	log.info("canal 消费者初始化");
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(this.rocketMQCfg.getNameSrvAddr());
//        consumer.setInstanceName(MixUtil.getLocalMachineInfo() + "_" + customCfg.getPort());
//        consumer.setVipChannelEnabled(false);
        try {
        	consumer.subscribe(this.topic, this.tag);

        	 consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        	 consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
             	boolean consumeSuccess = false;
                 for(Message msg:msgs){
                	String messageId =  msg.getProperty(MessageConst.PROPERTY_UNIQ_CLIENT_MESSAGE_ID_KEYIDX);
                	//System.out.println(messageId);
                     try {
                     	log.info("收到canal消息-----------------------------");
 						String s = new String(msg.getBody(),"utf-8");
 						if(msg.getTopic().equals(this.rocketMQCfg.getCanalMessageTopic())) {

 							CanalMessage cm = JsonUtil.json2Object(s, new TypeToken<CanalMessage>(){}.getType());
 							if(cm.getType().equals("ALTER") || cm.getType().equals("DELETE") || cm.getType().equals("CREATE")) {
 								return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
 							}
 							//2020/4/7 添加法币交易返佣监控  zhao
 							if(cm.getTable().equals(this.rocketMQCfg.getTableOtcorder())) { //TODO 影响订单判定
 								if(cm.getType().equals("INSERT")) {
 									consumeSuccess = true;
 								}
 								//log.info(JSON.toJSONString("法币--------------------->" +cm));
 								if(cm.getType().equals("UPDATE")) {
 									for(int i = 0 ; i < cm.getData().size() ; i++) {
 										try {
 											JSONObject updateJson = JSON.parseObject(s);
 											if(updateJson.containsKey("old") ) {
 												if(!updateJson.getJSONArray("old").getJSONObject(i).containsKey("status")) {
 													consumeSuccess = true;
 													log.info("修改内容不需要处理");
 													continue;
 												}
 											}
 											
											JSONObject update = new JSONObject(cm.getData().get(i));
											//已完成或申诉关闭
											//log.info("判断--->id  " + update.containsKey("id")+"   status  " + update.containsKey("status") +"    or " + (update.getIntValue("status") == 3 || update.getIntValue("status") == 5) +"    " + update.getIntValue("status"));
											if(update.containsKey("id") && update.containsKey("status") && (update.getIntValue("status") == 3 || update.getIntValue("status") == 5)) {
												//log.info("可处理订单-->" + JSON.toJSONString(cm));
												OtcOrderVo order = new OtcOrderVo();
												order.setId(update.getLongValue("id"));
												order.setStatus(update.getIntValue("status"));
		 	 									consumeSuccess = this.consumeOtcOrder(order , messageId);
											}
											else {
												consumeSuccess = true;
											}
										} catch (Exception e) {
											log.error("mq推送法币交易返佣数据格式错误，丢弃消息--【"+cm.getData().get(i)+"】");
											consumeSuccess = true;
										}
 									}
 								}
 							}
 							
							if(cm.getTable().equals(this.rocketMQCfg.getTableMember())) {
								List<Map<String, Object>> list = cm.getData();
								for (Map<String, Object> map : list) {
									CanalMember cm1 = ClazzUtil.mapToBean(map, Class.forName(Constant.CLAZZ_MEMBER));
									Member m = ObjectConvertor.convert2Member(cm1);
									consumeSuccess = this.consumeMember(m,messageId);
								}
							}
							
							if(cm.getTable().startsWith(this.rocketMQCfg.getTableAccountRunning())) {
								
								if(cm.getType().equals("UPDATE")) {
									return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
								}
								
								if(cm.getType().equals("INSERT")) {
									List<Map<String, Object>> list = cm.getData();
									for (Map<String, Object> map : list) {
										CanalAccountRunning car = ClazzUtil.mapToBean(map, Class.forName(Constant.CLAZZ_ACCOUNT_RUNNING));
										AccountRunning ar = ObjectConvertor.convert2AccountRunning(car);
										consumeSuccess = this.processAccountRunning(ar, messageId);
									}
								}
							}
 						} else {
 							consumeSuccess = this.consumeNonCanalMessage(s,messageId);
 						}
 						
 					} catch (UnsupportedEncodingException e) {
 						e.printStackTrace();
 					} 
                      catch (ClassNotFoundException e) {
 						e.printStackTrace();
 					} 
                     catch (Exception e) {
 						e.printStackTrace();
 					}
                 }
                 return consumeSuccess ?   ConsumeConcurrentlyStatus.CONSUME_SUCCESS : ConsumeConcurrentlyStatus.RECONSUME_LATER ;
             });

             consumer.start();
            System.out.println("[Consumer 已启动]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public abstract boolean consumeNonCanalMessage(String message, String msgId);
    
    public abstract void init();
    
    public boolean processAccountRunning(AccountRunning car, String msgId) {
    	
    	return this.consumeAccountRunning(car,msgId);
    	
    }

    public abstract boolean consumeAccountRunning(AccountRunning car,String msgId);
    
    public abstract boolean consumeMember(Member member,String msgId);
    
    public abstract boolean consumeOtcOrder(OtcOrderVo order , String msgId);
}