package com.spark.bitrade.common.consumer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageConst;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.google.common.reflect.TypeToken;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.spark.bitrade.common.util.RedisKeyGenerator;
import com.spark.bitrade.common.util.RedisUtil;
import com.spark.bitrade.common.util.RiskSetting;
import com.spark.bitrade.common.util.RiskSetting.ConfigSetting;
import com.spark.bitrade.common.util.RiskSetting.RiskDetailedType;
import com.spark.bitrade.common.util.SceneUtil;
import com.spark.bitrade.common.util.SceneUtil.Parames;
import com.spark.bitrade.common.util.ThreadPoolUtils;
import com.spark.bitrade.common.vo.RiskDetailedCacheVo;
import com.spark.bitrade.entity.Coin;
import com.spark.bitrade.entity.ExchangeWallet;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberLoginHistory;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.entity.OtcCoin;
import com.spark.bitrade.entity.RiskControlSettings;
import com.spark.bitrade.entity.RiskScene;
import com.spark.bitrade.service.CoinService;
import com.spark.bitrade.service.ExchangeWalletService;
import com.spark.bitrade.service.ICoinExchange;
import com.spark.bitrade.service.MemberLoginHistoryService;
import com.spark.bitrade.service.MemberService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.service.OtcCoinService;
import com.spark.bitrade.service.RiskControlSettingsService;
import com.spark.bitrade.service.RiskDetailedService;
import com.spark.bitrade.service.RiskSceneService;
import com.spark.bitrade.service.RiskSummaryService;
import com.spark.bitrade.util.MessageRespResult;

/**
 * 对cannal变化做监控
 * @author zhao
 *
 */
@Component
public class EventConsumer {

	@Value("${mq.group}")
	private String mqGroup;
	@Value("${mq.addr}")
	private String mqAddr;
	@Value("${mq.topic}")
	private String mqTopic;
	@Value("${mq.tag}")
	private String mqTag;
	private Log logger = LogFactory.getLog(this.getClass());
	private Gson gson;
	@Resource
	private RiskControlSettingsService riskControlSettingsService;
	@Resource
	private RiskSceneService riskSceneService;
	@Resource
	private OtcCoinService otcCoinService;
	@Resource
	private MemberService memberService;
	@Resource
	private MemberWalletService memberWalletService;
	@Resource
	private ExchangeWalletService exchangeWalletService;
	@Resource
	private RiskSummaryService riskSummaryService;
	@Resource
	private CoinService coinService;
	@Resource 
	private MemberLoginHistoryService memberLoginHistoryService;
	
	
	
	@Resource
	private RedisUtil redisUtil;
	
	private int workThreadNum = 5;//工作线程数
	
	/** 测试用例开始 **/
	//@PostConstruct
	public void _initForTest() {
		gson = new Gson();
		this.serverConfigInit();
		for(int i = 0 ; i < workThreadNum ; i++) {
			ThreadPoolUtils.putThread(new DetailedWorkThread());
		}
		logger.info("风险控制监控异步更新线程启动完成");
	}
	
	public void doTest(String type , String table , List<Map<String, Object>> data) {
		CanalMessage cm = new CanalMessage();
		cm.setMessageId("test"+Math.round(100000000));
		cm.setType(type);
		cm.setTable(table);
		cm.setData(data);
		this.keepWatch(cm);
	}
	/** 测试用例结束 **/
	
	
	
	@PostConstruct
	public void _init() {
		gson = new Gson();
		this.serverConfigInit();
		try {
			DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(mqGroup);
			consumer.setNamesrvAddr(mqAddr);
			consumer.subscribe(mqTopic, mqTag);
			consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
			logger.info("start consumer -> " + mqAddr + " " + mqTopic + " " + mqGroup + " " + mqTag);
			consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
				boolean consumeSuccess = true;
				if(!this.redisUtil.keyExist(ConfigSetting.RISK_OPEN_KEY) || this.redisUtil.getVal(ConfigSetting.RISK_OPEN_KEY).toString().equals("0")) {
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
							keepWatch(cm);
						}
					} catch (Exception e) {
						logger.error("进入监控场景处理异常" , e);
						consumeSuccess = false;
					}
				}
				return consumeSuccess ? ConsumeConcurrentlyStatus.CONSUME_SUCCESS
						: ConsumeConcurrentlyStatus.RECONSUME_LATER;
			});
			consumer.start();
			logger.info("风险控制监控启动完成");
			
			for(int i = 0 ; i < workThreadNum ; i++) {
				ThreadPoolUtils.putThread(new DetailedWorkThread());
			}
			logger.info("风险控制监控异步更新线程启动完成");
		} catch (Exception e) {
			logger.error("启动风险控制监控消费者失败..." , e);
		}
	}
	
	
	private static final String UPDATE = "UPDATE";
	
	/**
	 * 服务配置初始化
	 */
	private void serverConfigInit() {
		//加载基础配置
		if(this.riskControlSettingsService.getOne(new QueryWrapper<RiskControlSettings>().eq("set_key", ConfigSetting.RISK_OPEN_KEY)) == null) {
			RiskControlSettings openKey = new RiskControlSettings();
			openKey.setSetKey(ConfigSetting.RISK_OPEN_KEY);
			openKey.setSetName(ConfigSetting.RISK_OPEN_NAME);
			openKey.setSetVal("0");//默认关闭
			this.riskControlSettingsService.save(openKey);
		}
		if(this.riskControlSettingsService.getOne(new QueryWrapper<RiskControlSettings>().eq("set_key", ConfigSetting.RISK_COEFFICIENT_KEY)) == null) {
			RiskControlSettings openKey = new RiskControlSettings();
			openKey.setSetKey(ConfigSetting.RISK_COEFFICIENT_KEY);
			openKey.setSetName(ConfigSetting.RISK_COEFFICIENT_NAME);
			openKey.setSetVal("0");
			this.riskControlSettingsService.save(openKey);
		}
		this.riskControlSettingsService.loadSettings();
		
		//加载场景
		this.riskSceneService.loadScene();
		

		logger.info("开始初始化数据检查");
		if(this.redisUtil.keyExist("risk:exchangeCoin:init")) {
			logger.info("初始化已执行过一次，取消重复初始化");
			return;
		}
		this.redisUtil.setVal("risk:exchangeCoin:init", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		if(!this.redisUtil.keyExist("risk:exchangeCoin:all")) {
			logger.info("缓存中未发现历史汇率最低值，取消初始化");
			return;
		}
		JSONObject ecs = JSON.parseObject(this.redisUtil.getVal("risk:exchangeCoin:all").toString());
		if(!ecs.containsKey("USDT/CNYT") || !ecs.containsKey("BT/USDT")) {
			logger.info("缓存中未发现主币种转换汇率，取消初始化");
			return;
		}
		//历史数据初始化
		//已有数据
		List<Long> hasMemberIds = this.riskSummaryService.list().stream().map(mp -> mp.getMemberId()).collect(Collectors.toList());
		//数据量大，分批次处理
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -1);
		for(int i = 1 ;  ; i++) {
			Page<MemberLoginHistory> page = new Page<MemberLoginHistory>(i, 200);
			QueryWrapper<MemberLoginHistory> queryWrapper = new QueryWrapper<MemberLoginHistory>();
			queryWrapper.groupBy("member_id");
			queryWrapper.ge("login_time", calendar.getTime());
			List<MemberLoginHistory> members = null;
			//过滤已有
			if(org.apache.commons.collections.CollectionUtils.isNotEmpty(hasMemberIds)) {
				queryWrapper.notIn("id", hasMemberIds);
			}
			members = this.memberLoginHistoryService.page(page, queryWrapper).getRecords();
			//没有新数据，结束初始化
			if(org.apache.commons.collections.CollectionUtils.isEmpty(members)) {
				break;
			}
			//资产统计
			for(MemberLoginHistory mb : members) {
				ThreadPoolUtils.putThread(new InitThread(mb, ecs));
			}
		}
		
	}
	
	class InitThread extends Thread {//为了在初始化时，不将同一个账号的数据连续加入缓存，造成异步线程无法同时工作，将放入缓存的操作作为异步

		private MemberLoginHistory mb;
		
		private JSONObject ecs;
		
		public InitThread(MemberLoginHistory mlh ,JSONObject ecs) {
			this.mb = mlh;
			this.ecs = ecs;
		}
		
		@Override
		public void run() {
			if(mb.getMemberId() == null) return;
			logger.info("处理用户-->" + mb.getMemberId() + " 数据");
			Map<String, BigDecimal> coin_balance = new HashMap<String, BigDecimal>();
			//主
			memberWalletService.list(new QueryWrapper<MemberWallet>().eq("member_id", mb.getMemberId())).stream().forEach(each -> {
				Coin coin = coinService.getById(each.getCoinId());
				if(ecs.containsKey(coin.getUnit() + "/BT") || ecs.containsKey(coin.getUnit() + "/USDT")) {
					//如果当前币种在缓存中有最小汇率，则加入待计算
					coin_balance.put(coin.getUnit(), each.getBalance().add(each.getFrozenBalance()).add(each.getLockBalance()));
				}
			});
			//币
			exchangeWalletService.list(new QueryWrapper<ExchangeWallet>().eq("member_id", mb.getMemberId())).stream().forEach(each -> {
				if(ecs.containsKey(each.getCoinUnit() + "/BT") || ecs.containsKey(each.getCoinUnit() + "/USDT")) {
					if(coin_balance.containsKey(each.getCoinUnit())) { //累加
						coin_balance.put(each.getCoinUnit(), each.getBalance().add(each.getFrozenBalance()).add(coin_balance.get(each.getCoinUnit())));
					}
					else {
						coin_balance.put(each.getCoinUnit(), each.getBalance().add(each.getFrozenBalance()));
					}
				}
			});
			
			if(coin_balance.keySet().size() > 0) {
				//计算与BT或USDT的转换额度
				for(Map.Entry<String, BigDecimal> cb : coin_balance.entrySet()) {
					if(cb.getValue().compareTo(BigDecimal.ZERO) != 1) continue;
					BigDecimal ex = null;
					String ecName = null;
					if(ecs.containsKey(cb.getKey() + "/BT")) {
						ex = ecs.getBigDecimal(cb.getKey() + "/BT");
						ecName = "BT";
					}
					else {
						ex = ecs.getBigDecimal(cb.getKey() + "/USDT");
						ecName = "USDT";
					}
					BigDecimal balance = cb.getValue().multiply(ex); //转换后金额
					if(ecName.equals("BT")) { //先转usdt 再转cnyt
						balance = balance.multiply(ecs.getBigDecimal("BT/USDT"));
					}
					balance = balance.multiply(ecs.getBigDecimal("USDT/CNYT"));
					
					
					//加入流程等待执行
					
					
					RiskDetailedCacheVo rd = new RiskDetailedCacheVo();
					rd.setAmount(cb.getValue());
					rd.setConvertAmount(balance);
					rd.setCreateTime(new Date());
					rd.setExchange(balance.divide(cb.getValue()));
					rd.setExchangeSource("CNYT");
					rd.setMessageId(null);
					rd.setRfId(null);
					rd.setDetailedDesc("初始化钱包");
					rd.setUnti(cb.getKey());
					rd.setWorkNumber(1);
					rd.setCheck(false);
					rd.setMemberId(mb.getMemberId());
					rd.setInOut("1");
					rd.setTypeDesc(RiskDetailedType.INIT_DATA);
					rd.setAbstractKey(RiskSetting.getAbstrackKey(rd.getAmount(), rd.getUnti(), rd.getMemberId(), rd.getInOut(), rd.getCreateTime()));
					redisUtil.leftPush(RedisKeyGenerator.detailedWorkList(), JSON.toJSONString(rd));
					
				}
			}
		} 
		
	}
	
	private static final String TABLE_OTC_ORDER = "otc_order";
	
	private void keepWatch(CanalMessage cm) {
		
		logger.info("进入场景筛选---->" + cm.getTable() + "   " + cm.getType());
		if(cm.getTable().equals(TABLE_OTC_ORDER)) { //法币场景
			if(cm.getType().equals(UPDATE)) {
				cm.getData().stream().forEach(insert -> {
					JSONObject otcOrder = new JSONObject(insert);

					if(otcOrder.containsKey("status") && otcOrder.containsKey("advertise_type") && otcOrder.getIntValue("status") == 3 
							&& otcOrder.containsKey("number") && otcOrder.containsKey("price") && otcOrder.containsKey("money") && otcOrder.containsKey("member_id") && otcOrder.containsKey("coin_id") && otcOrder.containsKey("id")) {
						OtcCoin otcCoin = this.otcCoinService.getById(otcOrder.getLongValue("coin_id"));
						RiskDetailedCacheVo rd = new RiskDetailedCacheVo();
						rd.setAmount(otcOrder.getBigDecimal("number"));
						rd.setConvertAmount(otcOrder.getBigDecimal("money"));
						rd.setCreateTime(new Date());
						rd.setExchange(otcOrder.getBigDecimal("price"));
						rd.setExchangeSource("CNYT");
						rd.setMessageId(cm.getMessageId());
						rd.setRfId(otcOrder.getLong("id"));
						rd.setDetailedDesc("");
						rd.setUnti(otcCoin.getUnit());
						rd.setWorkNumber(1);
						rd.setCheck(false);
						if(otcOrder.getIntValue("advertise_type") == 0) {//买入
							rd.setMemberId(otcOrder.getLong("member_id"));
							rd.setInOut("1");
							rd.setTypeDesc(RiskDetailedType.RISK_FB_BUY);
							rd.setAbstractKey(RiskSetting.getAbstrackKey(rd.getAmount(), rd.getUnti(), rd.getMemberId(), rd.getInOut(), rd.getCreateTime()));
							this.redisUtil.leftPush(RedisKeyGenerator.detailedWorkList(), JSON.toJSONString(rd));
							rd.setMemberId(otcOrder.getLong("customer_id"));
							rd.setInOut("0");
							rd.setTypeDesc(RiskDetailedType.RISK_FB_PAY);
							rd.setAbstractKey(RiskSetting.getAbstrackKey(rd.getAmount(), rd.getUnti(), rd.getMemberId(), rd.getInOut(), rd.getCreateTime()));
							this.redisUtil.leftPush(RedisKeyGenerator.detailedWorkList(), JSON.toJSONString(rd));
						}
						else if(otcOrder.getIntValue("advertise_type") == 1){//卖出
							rd.setMemberId(otcOrder.getLong("member_id"));
							rd.setInOut("0");
							rd.setTypeDesc(RiskDetailedType.RISK_FB_PAY);
							rd.setAbstractKey(RiskSetting.getAbstrackKey(rd.getAmount(), rd.getUnti(), rd.getMemberId(), rd.getInOut(), rd.getCreateTime()));
							this.redisUtil.leftPush(RedisKeyGenerator.detailedWorkList(), JSON.toJSONString(rd));
							rd.setMemberId(otcOrder.getLong("customer_id"));
							rd.setInOut("1");
							rd.setTypeDesc(RiskDetailedType.RISK_FB_BUY);
							rd.setAbstractKey(RiskSetting.getAbstrackKey(rd.getAmount(), rd.getUnti(), rd.getMemberId(), rd.getInOut(), rd.getCreateTime()));
							this.redisUtil.leftPush(RedisKeyGenerator.detailedWorkList(), JSON.toJSONString(rd));
						}
					}
				});
			}
		}
		else {
			if(!CollectionUtils.isEmpty(this.getAllScene())) { //通用场景
				for(RiskScene rs : this.getAllScene()) {
					if(rs.getOpenScene().equals("1")) continue;//场景启用状态
					if(cm.getTable().equals(rs.getTableName().toLowerCase()) && cm.getType().equals(rs.getEvents().toUpperCase())) { //匹配场景
						List<Parames> parames = null;
						if(StringUtils.isNotBlank(rs.getParameter())) {
							try {
								parames = JSONArray.parseArray(rs.getParameter(), Parames.class);
							} catch (Exception e) {
								logger.error("场景参数配置错误-->" + rs.getSceneDesc());
							}
						}
						for(Map<String, Object> mp : cm.getData()) {
							boolean check = true;
							JSONObject scene = new JSONObject(mp);
							//场景取值字段筛选
							if(!scene.containsKey(rs.getAmountFormatter()) || !scene.containsKey(rs.getMemberFormatter()) || !scene.containsKey(rs.getUnitFormatter())) continue;
							if(parames != null) { //场景过滤条件筛选
								for(Parames ps : parames) {
									if(!scene.containsKey(ps.getCheckColumn()) ||
											!this.checkParames(scene.getDoubleValue(ps.getCheckColumn()), ps.getCheckType(), ps.getCheckVal())) {
										check = false;
										break;
									}
								}
								if(!check) continue;
							}
							//处理场景数据
							RiskDetailedCacheVo rd = new RiskDetailedCacheVo();
							rd.setAmount(scene.getBigDecimal(rs.getAmountFormatter()));
							rd.setCreateTime(new Date());
							rd.setExchangeSource(rs.getExchangeSource());
							rd.setMemberId(scene.getLong(rs.getMemberFormatter()));
							rd.setMessageId(cm.getMessageId());
							rd.setRfId(scene.getLong("id"));
							rd.setTypeDesc(rs.getSceneDesc());
							rd.setUnti(scene.getString(rs.getUnitFormatter()));
							rd.setWorkNumber(1);
							rd.setInOut(rs.getInOut());
							rd.setDetailedDesc("");
							rd.setAbstractKey(RiskSetting.getAbstrackKey(rd.getAmount(), rd.getUnti(), rd.getMemberId(), rd.getInOut(), rd.getCreateTime()));
							BigDecimal exchange = this.exchangeConvert(rd.getUnti(), rs.getExchangeSource());
							if(exchange == null) {
								rd.setCheck(true);
							}
							else {
								rd.setExchange(exchange);
								rd.setConvertAmount(rd.getAmount().multiply(exchange));
								rd.setCheck(false);
							}
							this.redisUtil.leftPush(RedisKeyGenerator.detailedWorkList(), JSON.toJSONString(rd));
						}
					}
				}
			}
		}
	}
	
	@Resource
	private ICoinExchange iCoinExchange;
	
	/**
	 * 获取汇率
	 * @param base
	 * @param convert
	 * @return
	 */
	private BigDecimal exchangeConvert(String base , String convert) {
		BigDecimal exchange = null;
		if(base.equals(convert)) return new BigDecimal(1);
		for(int i = 0 ; i < 5 ; i ++) { //汇率获取尝试5次
			//交易币种与cnyt汇率
			MessageRespResult<BigDecimal> getChange = this.iCoinExchange.getCnytExchangeRate(base.toUpperCase());
			BigDecimal baseExchange = null;
			BigDecimal convertExchange = null;
			if(getChange.isSuccess()) {
				baseExchange = getChange.getData().compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal(1) : getChange.getData();
			}
			if(baseExchange != null) {
				MessageRespResult<BigDecimal> getConvertChange = this.iCoinExchange.getCnytExchangeRate(convert.toUpperCase());
				if(getConvertChange.isSuccess() ) {
					convertExchange = getConvertChange.getData().compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal(1) : getConvertChange.getData();
					//假设 a : cnyt = 1:5    / 0.2
					//     b : cnyt = 2:5    / 0.4
					//     a : b = 1:5 / 2:5    / 0.2 / 0.4 = 1:2
					exchange = baseExchange.divide(convertExchange , 6 ,RoundingMode.DOWN);
					return exchange;
				}
			}
		}
		return exchange;
	}
	
	private boolean checkParames(double data , String type , double checkData) {
		if(type.equals(SceneUtil.EQ)) {
			return data == checkData;
		}
		else if(type.equals(SceneUtil.LT)) {
			return data < checkData;
		}
		else if(type.equals(SceneUtil.MT)) {
			return data > checkData;
		}
		return false;
	}
	
	private List<RiskScene> getAllScene() {
		if(this.redisUtil.keyExist(RedisKeyGenerator.riskSceneGet())) {
			String sceneJsons = this.redisUtil.getVal(RedisKeyGenerator.riskSceneGet()).toString();
			return JSONArray.parseArray(sceneJsons, RiskScene.class);
		}
		return null;
	}
	
	public static boolean WORK_CHECK_DO = true;//更新操作是否继续执行,防止在服务停止时错误的终止了异步任务
	@Resource
	private RiskDetailedService riskDetailedService;
	/**
	 * 执行更新操作线程
	 * @author zhao
	 *
	 */
	class DetailedWorkThread extends Thread {

		@Override
		public void run() {
			while(WORK_CHECK_DO) {
				String json = redisUtil.rightPop(RedisKeyGenerator.detailedWorkList());
				if(StringUtils.isBlank(json)) { //无处理消息
					try {
						sleep(2000);
						continue;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				RiskDetailedCacheVo rdcv = JSON.parseObject(json, RiskDetailedCacheVo.class);
				
				try {
					if(rdcv.isCheck()) { //当前消息在入缓存时没有获取到汇率
						BigDecimal exchange = exchangeConvert(rdcv.getUnti(), rdcv.getExchangeSource());
						if(exchange != null) {
							rdcv.setExchange(exchange);//TODO 通过接口获取汇率
							rdcv.setConvertAmount(rdcv.getAmount().multiply(exchange));
							rdcv.setCheck(false);
						}
						else {
							throw new RuntimeException("获取汇率失败");
						}
					}
					//防止使用inc操作使得锁键长时间存在
					if(redisUtil.keyExist(RedisKeyGenerator.getSummaryUpdateKey(rdcv.getMemberId().toString()))) {
						redisUtil.leftPush(RedisKeyGenerator.detailedWorkList(), rdcv.toJson());
						sleep(500);
						continue;
					}
					if(redisUtil.increment(RedisKeyGenerator.getSummaryUpdateKey(rdcv.getMemberId().toString()), 2) > 1) {//更新锁
						redisUtil.leftPush(RedisKeyGenerator.detailedWorkList(), rdcv.toJson());
						sleep(500);
						continue;
					}
					logger.info("处理异步数据--->" + json);
					riskDetailedService.updateDetailed(rdcv);
				} catch (Exception e) {
					rdcv.setWorkNumber(rdcv.getWorkNumber() + 1);
					if(rdcv.getWorkNumber() <= 3) { //最多执行3次处理尝试
						redisUtil.leftPush(RedisKeyGenerator.detailedWorkList(), rdcv.toJson());
					}
					else {
						logger.error("异步处理数据3次尝试异常，丢弃消息----->" + rdcv.toJson() , e);
					}
				}
			}
		}
		
	}
}
