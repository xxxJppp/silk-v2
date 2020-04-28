package com.spark.bitrade.consumer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.config.RocketMQCfg;
import com.spark.bitrade.constant.BizTypeEnum;
import com.spark.bitrade.constant.DistributeTypeEnum;
import com.spark.bitrade.consumer.base.ConsumerService;
import com.spark.bitrade.entity.Appeal;
import com.spark.bitrade.entity.CurrencyRuleSetting;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberBenefitsExtends;
import com.spark.bitrade.entity.MemberFeeDayStat;
import com.spark.bitrade.entity.MemberRecommendCommision;
import com.spark.bitrade.entity.OtcCoin;
import com.spark.bitrade.entity.OtcOrder;
import com.spark.bitrade.mapper.MemberRecommendCommisionMapper;
import com.spark.bitrade.service.AppealService;
import com.spark.bitrade.service.CurrencyRuleSettingService;
import com.spark.bitrade.service.MemberBenefitsExtendsService;
import com.spark.bitrade.service.MemberBenefitsSettingService;
import com.spark.bitrade.service.MemberFeeDayStatService;
import com.spark.bitrade.service.MemberLevelService;
import com.spark.bitrade.service.OtcCoinService;
import com.spark.bitrade.service.OtcOrderService;
import com.spark.bitrade.service.TokenExchangeRateService;
import com.spark.bitrade.utils.KeyGenerator;
import com.spark.bitrade.utils.RedisUtil;
import com.spark.bitrade.utils.ThreadPoolUtils;
import com.spark.bitrade.vo.AccountRunning;
import com.spark.bitrade.vo.OtcOrderVo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * <p>
 * 	法币交易返佣
 * </p>
 *
 * @author zhao
 * @since 2020年4月7日
 */
@Component
@Slf4j
public class OtcOrderConsumer extends ConsumerService {

	@Autowired
	private RocketMQCfg rocketMQCfg;
	@Autowired
	private RedisUtil redisUtil;
	
	@Override
	public boolean consumeNonCanalMessage(String message, String msgId) {
		return true;
	}

	@Override
	public void init() {
		this.consumerGroup = this.rocketMQCfg.getOtcConsumerGroup();
		this.topic = this.rocketMQCfg.getCanalMessageTopic();
		this.tag = this.rocketMQCfg.getCanalMessageTag();
		ThreadPoolUtils.putThread(new OtcOrderWorkThread());
	}

	
	@Override
	public boolean consumeAccountRunning(AccountRunning car, String msgId) {
		return true;
	}

	@Override
	public boolean consumeMember(Member member, String msgId) {
		return true;
	}

	@Override
	public boolean consumeOtcOrder(OtcOrderVo order , String messageId) {
		//放入到缓存中等待任务线程执行，防止丢失
		order.setMessageId(messageId);
		this.redisUtil.leftPush(KeyGenerator.getOtcOrderWorksKey(), JSON.toJSONString(order));
		log.info("加入otc订单返佣任务--->id-->" + order.getId());
		return true;
	}

	/**
	 * 
	 * <p>
	 * 			返佣任务线程
	 * </p>
	 *
	 * @author zhao
	 * @since 2020年4月7日
	 */
	class OtcOrderWorkThread extends Thread {

		@Override
		public void run() {
			log.info("启动otc法币订单返佣任务线程");
			while(true) {
				String workStr = null;
				try {
					if(StringUtils.isEmpty(workStr = redisUtil.rightPop(KeyGenerator.getOtcOrderWorksKey()))) {
						this.sleep(2000); //无任务时2s检查一次
						continue;
					}
					OtcOrderVo order = JSON.parseObject(workStr, OtcOrderVo.class);
					otcOrderWork(order);
				} catch (Exception e) {
					log.error("处理返佣异常--->[" + workStr + "]" , e);
				}
			}
		}
	}
	
	@Autowired
	private OtcOrderService otcOrderService;
	@Autowired
	private MemberBenefitsExtendsService memberBenefitsExtendsService;
	@Autowired
	private AppealService appealService;
	@Autowired
	private CurrencyRuleSettingService currencyRuleSettingService;
	@Autowired
	private MemberBenefitsSettingService memberBenefitsSettingService;
	@Autowired
	private MemberLevelService memberLevelService;
	@Autowired
	private OtcCoinService otcCoinService;
	@Autowired
	private MemberRecommendCommisionMapper memberRecommendCommisionMapper;
	@Autowired
	private MemberFeeDayStatService memberFeeDayStatService;
	@Autowired
	private TokenExchangeRateService tokenExchangeRateService;
	/**
	 *  任务处理体
	 * @param order
	 * @author zhaopeng
	 * @since 2020年4月7日
	 */
	@Transactional
	public void otcOrderWork(OtcOrderVo order) {
		//原订单
		OtcOrder dbOrder = this.otcOrderService.getById(order.getId());	
		if(dbOrder == null) {
			log.error("订单不存在-->" + order.getId());
			return;
		}
		OtcCoin dbCoin = this.otcCoinService.getById(dbOrder.getCoinId());
	
		if(dbCoin == null) {
			log.error("币种不存在-->" + dbOrder.getCoinId());
			return ;
		}
		//仅处理订单状态为已完成或申诉关闭
		if(dbOrder.getStatus().intValue() == 3 || dbOrder.getStatus().intValue() == 5) { 
			
			//申诉订单中无需方法佣金的场景------------------------------------------------------------------------------
			if(dbOrder.getStatus().intValue() == 5) {//申诉
				//申诉订单
				Appeal appeal = this.appealService.getOne(new QueryWrapper<Appeal>().eq("order_id", order.getId()).eq("status", 1));
				if(appeal == null) {
					log.error("未查询到订单["+dbOrder.getId()+"]申诉信息");
					return;
				}
				if(dbOrder.getAdvertiseType().intValue() == 0) { //用户卖出
					//若用户申请申诉且为胜方
					if((appeal.getInitiatorId().longValue() == dbOrder.getCustomerId().longValue() && appeal.getIsSuccess().intValue() == 1 )
							||
							//商家为申诉方且为败方
						(appeal.getInitiatorId().longValue() == dbOrder.getMemberId().longValue() && appeal.getIsSuccess().intValue() == 0)) {
						log.info("用户卖出申诉不满足发放佣金条件-->" + dbOrder.getOrderSn());
						return ;//不发放佣金
					}
				}
				else { //用户买入
					//若用户申请申诉且为败方
					if((appeal.getInitiatorId().longValue() == dbOrder.getCustomerId().longValue() && appeal.getIsSuccess().intValue() == 0 )
							||
							//商家为申诉方且为胜方
						(appeal.getInitiatorId().longValue() == dbOrder.getMemberId().longValue() && appeal.getIsSuccess().intValue() == 1)) {
						log.info("用户买入申诉不满足发放佣金条件-->" + dbOrder.getOrderSn());
						return ;//不发放佣金
					}
				}
			}
			//---------------------------------------------------------------------------------------------------------------------------
			
			List<MemberRecommendCommision> commisions = new ArrayList<MemberRecommendCommision>();
			BigDecimal commisionUnit2USDTRate = this.tokenExchangeRateService.commision2USDT(dbCoin.getUnit());
			//返佣计算
			//获取商家返佣比例
			CurrencyRuleSetting ruleSetting = this.currencyRuleSettingService.getOne(new QueryWrapper<CurrencyRuleSetting>().eq("rule_key", "USDC_BUSINESS_COMMISSION").eq("rule_state", 1));
			BigDecimal dis = new BigDecimal(ruleSetting.getRuleValue());
			if(dbOrder.getNumber().multiply(dis).compareTo(BigDecimal.ZERO) == 1) {
				commisions.add(new MemberRecommendCommision(dbOrder.getOrderSn(), dbOrder.getMemberId() ,dbOrder.getMemberId(), 0,dbCoin.getUnit() , dbOrder.getNumber().multiply(dis), BigDecimal.ZERO,
						BigDecimal.ZERO, DistributeTypeEnum.WAIT_DISTRIBUTE.getCode(), dbOrder.getNumber().multiply(dis), 40, dbOrder.getNumber().multiply(dis).multiply(commisionUnit2USDTRate).setScale(8, RoundingMode.DOWN)));
			}
			
			//检查缓存
			if(!this.redisUtil.keyExist("member:level:otc:config")) {
				List<MemberLevelOfCurrencyBean> beans = new ArrayList<MemberLevelOfCurrencyBean>();
	        	this.memberBenefitsSettingService.list().stream().forEach(each -> {
	        		MemberLevelOfCurrencyBean bean = new MemberLevelOfCurrencyBean();
	        		bean.setCurrencyBusinessDiscount((each.getCurrencyBusinessDiscount() != null && each.getCurrencyBusinessDiscount().compareTo(BigDecimal.ZERO) == 1) ? each.getCurrencyBusinessDiscount().toPlainString() : "");
	        		bean.setCurrencyDiscount((each.getCurrencyDiscount() != null && each.getCurrencyDiscount().compareTo(BigDecimal.ZERO) == 1) ? each.getCurrencyDiscount().toPlainString() : "");
	        		bean.setCurrencyId(each.getCurrencyId());
	        		bean.setCurrencyUnit((each.getCurrencyId() == null || each.getCurrencyId().longValue() == 0) ? null : this.otcCoinService.getById(each.getCurrencyId()).getUnit());
	        		bean.setLevelId(each.getLevelId());
	        		bean.setLevelName(this.memberLevelService.getById(each.getLevelId()).getNameEn());
	        		beans.add(bean);
	        	});
	        	this.redisUtil.setVal("member:level:otc:config", JSONArray.toJSONString(beans));
			}
			List<MemberLevelOfCurrencyBean> beans = JSONArray.parseArray(this.redisUtil.getVal("member:level:otc:config").toString(), MemberLevelOfCurrencyBean.class);
			//当存在会员等级时，执行上级返佣逻辑，否则跳过
			if(!CollectionUtils.isEmpty(beans)) {
				//获取商家的上级账户 会员等级
				MemberBenefitsExtends businessSuperior = this.memberBenefitsExtendsService.getSuperiorAccountLevelId(dbOrder.getMemberId());
				MemberBenefitsExtends memberSuperior = this.memberBenefitsExtendsService.getSuperiorAccountLevelId(dbOrder.getCustomerId());
				//log.info("商家id-->" + businessSuperior.getMemberId());
				//log.info("用户id-->" + memberSuperior.getMemberId());
				//商家上级返佣
				if(businessSuperior != null) { 
					//log.info("商家上级会员等级--->" + memberSuperior.getLevelId() + "   " + memberSuperior.getMemberId());
					MemberLevelOfCurrencyBean superior = getMemberLevel(beans, businessSuperior.getLevelId());
					if(superior != null && StringUtils.isNotBlank(superior.getCurrencyUnit()) && superior.getCurrencyUnit().equals(dbCoin.getUnit())) {
						BigDecimal businessInvGet = null;
						if(StringUtils.isNoneBlank(superior.getCurrencyBusinessDiscount())) { //当前用户为经纪人，需要使用直推商家返利策略
							businessInvGet = dbOrder.getNumber().multiply(new BigDecimal(superior.getCurrencyBusinessDiscount()));
							log.info("------------------->" + dbOrder.getNumber() + "     " + superior.getCurrencyBusinessDiscount() +"    "+ businessInvGet +"     " + commisionUnit2USDTRate);
						}
						else if(StringUtils.isNoneBlank(superior.getCurrencyDiscount())) { //当前用户为vip，需要使用直推会员返利策略
							businessInvGet = dbOrder.getNumber().multiply(new BigDecimal(superior.getCurrencyDiscount()));
						}
						if(businessInvGet != null) {
							commisions.add(new MemberRecommendCommision(dbOrder.getOrderSn(), businessSuperior.getMemberId() ,dbOrder.getMemberId(), 1,dbCoin.getUnit() , businessInvGet, BigDecimal.ZERO,
									BigDecimal.ZERO, DistributeTypeEnum.WAIT_DISTRIBUTE.getCode(), businessInvGet, 60, businessInvGet.multiply(commisionUnit2USDTRate).setScale(8, RoundingMode.DOWN)));
						}
					}
				}
				
				//用户上级返佣
				if(memberSuperior != null) { 
					//log.info("用户上级会员等级--->" + memberSuperior.getLevelId()+ "   " + memberSuperior.getMemberId());
					MemberLevelOfCurrencyBean superior = getMemberLevel(beans, memberSuperior.getLevelId());
					if(superior != null && superior.getCurrencyUnit().equals(dbCoin.getUnit())) {
						if(StringUtils.isNoneBlank(superior.getCurrencyDiscount())) { //当前用户为vip，需要使用直推会员返利策略
							BigDecimal memberInvGet = dbOrder.getNumber().multiply(new BigDecimal(superior.getCurrencyDiscount()));
							commisions.add(new MemberRecommendCommision(dbOrder.getOrderSn(), memberSuperior.getMemberId() ,dbOrder.getCustomerId(), 1,dbCoin.getUnit() , memberInvGet, BigDecimal.ZERO,
									BigDecimal.ZERO, DistributeTypeEnum.WAIT_DISTRIBUTE.getCode(), memberInvGet, 50, memberInvGet.multiply(commisionUnit2USDTRate).setScale(8, RoundingMode.DOWN)));
						}
					}
				}
			}
			//返佣执行
			if(!CollectionUtils.isEmpty(commisions)) {

				log.info("订单返佣执行->" + commisions.size() + "    " + dbOrder.getOrderSn());
				commisions = commisions.stream().filter(item -> item.getCommisionQuantity().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
				commisions.stream().forEach(each -> {
					each.setMqMsgId(order.getMessageId());
				});
				this.memberRecommendCommisionMapper.batchInsert(commisions);
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
				MemberFeeDayStat mfds = this.memberFeeDayStatService.getMemberFeeDayStatByDay(sdf.format(dbOrder.getPayTime()));
				MemberFeeDayStat stat = new MemberFeeDayStat();
				stat.setStatisticDate(dbOrder.getPayTime());
		
				if (null != mfds) {
					stat.setVersion(mfds.getVersion());
					stat.setUnit(mfds.getUnit());
				}
				if (null == mfds) {
					stat.setUnit(dbCoin.getUnit());
				}
		
				this.memberFeeDayStatService.updateDailyStat(stat);
			}
		}
	}
	
	/**
	 * 返回用户对应等级信息
	 * @param beans
	 * @param leveId
	 * @return
	 * @author zhaopeng
	 * @since 2020年4月8日
	 */
	public MemberLevelOfCurrencyBean getMemberLevel(List<MemberLevelOfCurrencyBean> beans , int leveId) {
		List<MemberLevelOfCurrencyBean> filter = beans.stream().filter(ft -> ft.getLevelId() == leveId).collect(Collectors.toList());
		return CollectionUtils.isEmpty(filter) ? null : (StringUtils.isBlank(filter.get(0).getCurrencyUnit()) ? null : filter.get(0));
	}
	
	//会员等级与返佣相关信息
	@Data
    static class MemberLevelOfCurrencyBean {
    	private long levelId;//会员等级编号
    	private String levelName;//会员等级名称
    	private long currencyId;//法币编号
    	private String currencyUnit;//法币名
    	//便于缓存，转换为字符串
    	private String currencyBusinessDiscount;//直推商家交易返还
    	private String currencyDiscount;//直推会员交易返还
    	
    }
}
