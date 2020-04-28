package com.spark.bitrade.common.customer;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.spark.bitrade.biz.MemberDailyTaskBizService;
import com.spark.bitrade.biz.MemberDailyTaskBizService.RecordCheckPojo;
import com.spark.bitrade.common.RedisUtil;
import com.spark.bitrade.common.ReidsKeyGenerator;
import com.spark.bitrade.common.ThreadPoolUtils;
import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.NewYearCoin;
import com.spark.bitrade.entity.NewYearConfig;
import com.spark.bitrade.service.IExchangeV2Service;
import com.spark.bitrade.service.NewYearCoinService;
import com.spark.bitrade.service.NewYearConfigService;
import com.spark.bitrade.util.MessageRespResult;

import lombok.Data;

/**
 * 
 * <p>
 * cannel投递到rocketmq的消息在当前类中做消费
 * </p>
 *
 * @author zhaopeng
 * @since 2020年1月7日
 */
@Component
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
	@Value("${mq.newyear.topic}")
	private String newYearTopic;
	@Resource
	private RedisUtil redisUtil;
	@Resource
	private MemberDailyTaskBizService memberDailyTaskBizService;
	@Resource
	private NewYearConfigService newYearConfigService;
	@Resource
	private IExchangeV2Service iExchangeV2Service;
	
	private Gson gson;
	private Log logger = LogFactory.getLog(this.getClass());

	private DefaultMQProducer producer;//延迟消息生产者
	
	private final long TIME = 10 * 60 * 1000;

	@PostConstruct
	public void consumerInit() {
        
		gson = new Gson();
		try {
			//TODO 添加延迟队列生产者
			producer = new DefaultMQProducer("newyear_task_producer");
			producer.setNamesrvAddr(mqAddr);
			producer.setInstanceName("OnlyProducer");
			producer.start();
			

			//TODO 添加延迟队列消费者
			DefaultMQPushConsumer newYearConsumer = new DefaultMQPushConsumer(mqGroup+"_newYear");
			newYearConsumer.setNamesrvAddr(mqAddr);
			newYearConsumer.subscribe(newYearTopic, mqTag);
			newYearConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
			logger.info("start newYearConsumer -> " + mqAddr + " " + newYearTopic + " " + mqGroup+"_newYear" + " " + mqTag);
			newYearConsumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
				boolean consumeSuccess = true;
				for (Message msg : msgs) {
					try {
						String message = new String(msg.getBody(), "utf-8");
						logger.info("处理延迟消息----》" + message);
						String orderId = message.split("#")[0];
						String memberId = message.split("#")[1];
						MessageRespResult<ExchangeOrder>  order = this.iExchangeV2Service.queryOrder(Long.parseLong(memberId), orderId);
						if(order.isSuccess() && order.getData() != null) { //对比时间和状态
							if(order.getData().getStatus() == ExchangeOrderStatus.TRADING //交易中
									|| (order.getData().getStatus() == ExchangeOrderStatus.COMPLETED && ( order.getData().getCompletedTime() - order.getData().getTime()) >= TIME)  //完成交易并且挂单时间超过10分钟
									|| (order.getData().getStatus() == ExchangeOrderStatus.CANCELED && (order.getData().getCanceledTime() - order.getData().getTime()) >= TIME)) { //取消交易并且挂单时间超过10分钟
								this.memberDailyTaskBizService.addMemberTask(TASK_PUT_STATUS, order.getData().getMemberId(),msg.getProperty(MessageConst.PROPERTY_UNIQ_CLIENT_MESSAGE_ID_KEYIDX) , true , true);
							}
						}
					} catch (UnsupportedEncodingException e) {
						logger.error("年终活动延迟消息处理异常-->messageId->" + msg.getProperty(MessageConst.PROPERTY_UNIQ_CLIENT_MESSAGE_ID_KEYIDX)  , e);
						consumeSuccess = false;
					}
				}
				return consumeSuccess ? ConsumeConcurrentlyStatus.CONSUME_SUCCESS
						: ConsumeConcurrentlyStatus.RECONSUME_LATER;
			});
			newYearConsumer.start();
			
			DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(mqGroup);
			consumer.setNamesrvAddr(mqAddr);
			consumer.subscribe(mqTopic, mqTag);
			consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
			logger.info("start consumer -> " + mqAddr + " " + mqTopic + " " + mqGroup + " " + mqTag);
			consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
				boolean consumeSuccess = true;
				//活动时间判断
				List<NewYearConfig> configs = this.newYearConfigService.findNewYearConfig();
				if(CollectionUtils.isEmpty(configs))  {
					logger.info("没有可用活动");
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
				if(System.currentTimeMillis() < configs.get(0).getMineralStartTime().getTime()) {// || System.currentTimeMillis() > configs.get(0).getMineralEndTime().getTime()) {
					logger.info("当前不再活动时间范围");
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
				
				for (Message msg : msgs) {
					try {
						String s = new String(msg.getBody(), "utf-8");
						CanalMessage cm = gson.fromJson(s, new TypeToken<CanalMessage>() {
						}.getType());
						if(CollectionUtils.isEmpty(cm.getData())) continue;
						cm.setMessageId(msg.getProperty(MessageConst.PROPERTY_UNIQ_CLIENT_MESSAGE_ID_KEYIDX));
						if (cm.getType().equals("ALTER") || cm.getType().equals("CREATE") || cm.getType().equals("DELETE")) {
							continue;
						}else {
							if(System.currentTimeMillis() > configs.get(0).getMineralEndTime().getTime()) {
								consumeSuccess = recordCheck(cm);
							}
							else {
								//年终活动获得挖矿机会检查
								logger.info("收到待处理消息...");
								consumeSuccess = checkChangeTable(cm);
							}
						}
					} catch (Exception e) {
						logger.error("年终活动处理数据变化异常-->messageId->" + msg.getProperty(MessageConst.PROPERTY_UNIQ_CLIENT_MESSAGE_ID_KEYIDX) , e );
						consumeSuccess = false;
					}
				}
				return consumeSuccess ? ConsumeConcurrentlyStatus.CONSUME_SUCCESS
						: ConsumeConcurrentlyStatus.RECONSUME_LATER;
			});
			consumer.start();
			logger.info("check trhead start...");
		} catch (Exception e) {
			logger.error("启动年终活动监控失败..." , e);
		}
	}
	
	/**
	 * 发送延迟消息  延迟10min
	 * @param body
	 * @author zhaopeng
	 * @since 2020年1月8日
	 */
	private void producerSend(String body) {
		try {
			Message message = new Message(newYearTopic, "*" ,body.getBytes());
			message.setDelayTimeLevel(15);
			this.producer.send(message);
		} catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
			logger.error("年终任务监控发送延迟任务失败... " , e);
		}
	}
	
	private static final String UPDATE = "UPDATE";
	private static final String INSERT = "INSERT";
	
	private static final String TABLE_MEMBER = "member";
	
	private static final String TABLE_MEMBER_LOGIN = "member_login_history";
	//befor
	private static final String TABLE_EXCHANGE_WALLET_WAL_RECORD = "exchange_wallet_wal_record_";
	
	private static final String TABLE_MEMBER_DEPOSIT = "member_deposit";
	
	private static final String TABLE_MEMBER_TRANSACTION = "member_transaction";
	
	private static final String TABLE_EXCHANGE_ORDER = "exchange_order";
	
	public static final String TASK_REGIEST_SLEF_STATUS = "taskRegiestSlef"; //自身注册
	public static final String TASK_REGIST_STATUS = "taskRegistStatus";//推荐好友注册
	public static final String TASK_LOGIN_STATUS = "taskLoginStatus";//首次登录
	public static final String TASK_EXCHANGE_STATUS = "taskExchangeStatus";//首次币币交易
	public static final String TASK_RECHARGE_STATUS = "taskRechargeStatus";//首次充币
	public static final String TASK_OTC_STATUS = "taskOtcStatus";//首次法币
	public static final String TASK_PUT_STATUS = "taskPutStatus";//挂币币交易买单10分钟
	
	
	
	//用于在查询用户的活动完成情况时的标记
	public static final String[] TASK_LIST = {TASK_REGIST_STATUS , TASK_LOGIN_STATUS , TASK_EXCHANGE_STATUS , TASK_RECHARGE_STATUS , TASK_OTC_STATUS , TASK_PUT_STATUS};
	
	
	/**
	 * 处理关于年终任务活动的数据变动 </br>
	 * 0.个人账号注册  member 新增 
	 * 1.推荐好友注册   member inviter_id 新增 </br>
	 * 2.每日首次登录   member_login_history  新增 </br>
	 * 3.每日首次币币交易   exchange_wallet_wal_record_[0-7] ，trade_type=3 新增 </br>
	 * 4.每日首次充币   member_transaction   新增 </br>
	 * 5.每日首次法币交易买入成交 member_transaction   新增 </br>
	 * 6.每日挂1次币币交易买单超过10分钟 exchange_order ，direction=0 并且 time - canceled_time|completed_time >10分钟（status=0=交易中/1=完成/2=取消），新增 回调延迟消息 </br>
	 * @param cm
	 * @author zhaopeng
	 * @since 2020年1月7日
	 */
	private boolean checkChangeTable(CanalMessage cm) {
		try {
			if(cm.getTable().equals(TABLE_MEMBER_LOGIN)) {
				if(cm.getType().equals(INSERT)) {//登录
					cm.getData().stream().forEach(insert -> {
						JSONObject member = new JSONObject(insert);
						this.memberDailyTaskBizService.addMemberTask(TASK_LOGIN_STATUS, member.getLong("member_id"),cm.getMessageId() , true , true);
					});
				}
			}
			if(cm.getTable().equals(TABLE_MEMBER)) {
				switch (cm.getType()) {
					case INSERT: //推荐好友注册
						cm.getData().stream().forEach(insert -> {
							JSONObject member = new JSONObject(insert);
							this.memberDailyTaskBizService.addMemberTask(TASK_REGIEST_SLEF_STATUS, member.getLong("id"),cm.getMessageId() , true , false);//被邀请人
						});
						break;
					case UPDATE: //推荐好友注册 推荐人
						for(int i = 0 ; i < cm.getOld().size() ; i++) {
							if(cm.getOld().get(i).containsKey("inviter_id")) { 
								com.alibaba.fastjson.JSONObject newUpdate = new com.alibaba.fastjson.JSONObject(cm.getData().get(i));
								if(newUpdate.containsKey("inviter_id") && newUpdate.get("inviter_id") != null && newUpdate.getLong("inviter_id") != 0) {
									this.memberDailyTaskBizService.addMemberTask(TASK_REGIST_STATUS, newUpdate.getLong("inviter_id"),cm.getMessageId() , false , false);//邀请人
								}
							}
						}
						break;
				}
			}
			else if(cm.getTable().startsWith(TABLE_EXCHANGE_WALLET_WAL_RECORD)) {
				if(cm.getType().equals(INSERT)) { //每日首次币币交易
					cm.getData().stream().forEach(insert -> {
						if(insert.containsKey("trade_type") && insert.get("trade_type").toString().equals("3")) {
							this.memberDailyTaskBizService.addMemberTask(TASK_EXCHANGE_STATUS, Long.parseLong(insert.get("member_id").toString()),cm.getMessageId() , true , true);
						}
					});
				}
			}
			else if(cm.getTable().equals(TABLE_MEMBER_TRANSACTION)) {
				if(cm.getType().equals(INSERT)) {
					cm.getData().stream().forEach(insert -> {
						JSONObject memberTransaction = new JSONObject(insert);
						if(memberTransaction.getInt("type") == 4) { //每日首次法币交易买入成交
							this.memberDailyTaskBizService.addMemberTask(TASK_OTC_STATUS, Long.parseLong(insert.get("member_id").toString()),cm.getMessageId() , true , true);
						}
					});
				}
			}
			else if(cm.getTable().equals(TABLE_MEMBER_DEPOSIT)) {
				if(cm.getType().equals(INSERT)) {
					cm.getData().stream().forEach(insert -> {
						JSONObject memberDeposit = new JSONObject(insert);
						if(memberDeposit.getString("txid").length() > 32) { //每日首次充币
							this.memberDailyTaskBizService.addMemberTask(TASK_RECHARGE_STATUS, Long.parseLong(insert.get("member_id").toString()),cm.getMessageId() , true , true);
							
						}
					});
				}
			}
			else if(cm.getTable().equals(TABLE_EXCHANGE_ORDER)) {
				if(cm.getType().equals(INSERT)) {
					cm.getData().stream().forEach(insert -> {
						JSONObject order = new JSONObject(insert);
						logger.info("挂单交易--->" + insert);
						if(order.getInt("direction") == 0) {
							//发送延迟消息
							this.producerSend(order.getString("order_id") + "#" + order.getInt("member_id"));
						}
					});
				}
			}
		} catch (Exception e) {
			logger.error("处理年终活动挖矿次数检查错误 -->" + cm.toString() , e);
			return false;
		}
		return true;
	}
	
	
	/**
	 * 处理活动结束后，用户币币交易释放 锁仓
	 * @param cm
	 * @return
	 * @author zhaopeng
	 * @since 2020年1月14日
	 */
	public boolean recordCheck(CanalMessage cm) {
		try {
			if(cm.getTable().startsWith(TABLE_EXCHANGE_WALLET_WAL_RECORD) && cm.getType().equals(INSERT)) {
				cm.getData().stream().forEach(insert -> {
					if(insert.containsKey("trade_type") && insert.get("trade_type").toString().equals("3")) {
						this.memberDailyTaskBizService.recordCheck(insert.get("coin_unit").toString(), Long.parseLong(insert.get("member_id").toString()), new BigDecimal(insert.get("trade_balance").toString()));
					}
				});
			}
		} catch (Exception e) {
			logger.error("处理年终活动交易释放失败 -->" + cm.toString() , e);
			return false;
		}
		return true;
	}
}
