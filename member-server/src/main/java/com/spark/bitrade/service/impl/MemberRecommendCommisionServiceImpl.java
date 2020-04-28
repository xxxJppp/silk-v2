package com.spark.bitrade.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.klock.annotation.Klock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.google.common.base.Strings;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.spark.bitrade.constant.BizTypeEnum;
import com.spark.bitrade.constant.Constant;
import com.spark.bitrade.constant.DistributeTypeEnum;
import com.spark.bitrade.constant.ExchangeTypeEnum;
import com.spark.bitrade.constant.MemberLevelTypeEnum;
import com.spark.bitrade.entity.MemberBenefitsOrder;
import com.spark.bitrade.entity.MemberBenefitsSetting;
import com.spark.bitrade.entity.MemberExtend;
import com.spark.bitrade.entity.MemberFeeDayStat;
import com.spark.bitrade.entity.MemberRecommendCommision;
import com.spark.bitrade.entity.MemberRecommendCommisionSetting;
import com.spark.bitrade.mapper.MemberRecommendCommisionMapper;
import com.spark.bitrade.param.ExchangeOrderReceipt;
import com.spark.bitrade.param.MemberBenefitsOrderReceipt;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.service.GlobalConfService;
import com.spark.bitrade.service.ICoinExchange;
import com.spark.bitrade.service.MemberBenefitsExtendsService;
import com.spark.bitrade.service.MemberBenefitsSettingService;
import com.spark.bitrade.service.MemberFeeDayStatService;
import com.spark.bitrade.service.MemberInviteService;
import com.spark.bitrade.service.MemberRecommendCommisionService;
import com.spark.bitrade.service.MemberRecommendCommisionSettingService;
import com.spark.bitrade.service.TokenExchangeRateService;
import com.spark.bitrade.utils.KeyGenerator;
import com.spark.bitrade.utils.TimeUtil;
import com.spark.bitrade.vo.RecommendCommisionVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemberRecommendCommisionServiceImpl
		extends ServiceImpl<MemberRecommendCommisionMapper, MemberRecommendCommision>
		implements MemberRecommendCommisionService {

	@Autowired
	private RedisTemplate redisTemplate;

	@Value("${biz.distributeLevel}")
	private int distributeLevel;



	@Value("${price.url.prefix}")
	private String priceServiceURLPrefix ;

	@Autowired
	private MemberRecommendCommisionSettingService recommendCommisionSettingService;

	@Autowired
	private MemberBenefitsExtendsService memberBenefitsExtendsService;

	@Autowired
	private MemberRecommendCommisionMapper memberRecommendCommisionMapper;

	@Autowired
	private MemberInviteService memberInviteService;

	@Autowired
	private MemberBenefitsSettingService memberBenefitsSettingService;

	@Autowired
	private TokenExchangeRateService tokenExchangeRateService;

	@Autowired
	private GlobalConfService globalConfService;

	@Autowired
	private MemberFeeDayStatService memberFeeDayStatService;

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ICoinExchange iCoinExchange;

	
	private ExchangeOrderReceipt exchangeToken2CommisionTokenRate(ExchangeOrderReceipt orderReceipt) {
		//获取返佣币种
		String tokenExchangeCommisionUnit = this.globalConfService.getTokenExchangeFeeCommisionUnit();

		//原始手续费币种与CNY的汇率
		BigDecimal exchangeToken2CNYRate = this.tokenExchangeRateService.getToken2CNYRate(orderReceipt.getCommisionUnit());
		
		
		
		//原始手续费币种与返佣币的汇率
		BigDecimal txUnit2CommisionUnit = tokenExchangeRateService.getTokenExchangeRate(orderReceipt.getCommisionUnit(), tokenExchangeCommisionUnit);
		//返佣币种与CNY的汇率
		BigDecimal commisionUnit2CNY = tokenExchangeRateService.getToken2CNYRate(tokenExchangeCommisionUnit);
		orderReceipt.setTxUnit2CommisionUnit(txUnit2CommisionUnit);
		orderReceipt.setCommisionUnit2CNY(commisionUnit2CNY);
		
		
		

		//返佣币种交易时间前2小时的小时均价
		BigDecimal total = orderReceipt.getCommisionQuantity().multiply(exchangeToken2CNYRate);
		long toTime = TimeUtil.getCurrentHourTime(orderReceipt.getTxTime()).getTime(),fromTime = TimeUtil.getHourTime(orderReceipt.getTxTime(), 2, "-").getTime();

		int timeInterval = 60;//K线小时线
		if(tokenExchangeCommisionUnit.toUpperCase().equals("BT")) {
			throw new RuntimeException("token exchange commision unit couldn't be BT, please check your db config......");
		}
		//String url = priceServiceURLPrefix + "/market/history?symbol="+tokenExchangeCommisionUnit+"/USDT&from="+ fromTime +"&to="+ toTime +"&resolution="+ timeInterval;
		//ResponseEntity<String> responseEntity = this.restTemplate.getForEntity(url, String.class);
		JSONArray array = this.iCoinExchange.findKHistory(tokenExchangeCommisionUnit+"/USDT", fromTime, toTime, timeInterval+"");
		String body = array.toJSONString();//responseEntity.getBody();
		if(body.equals("[]")) {
    		throw new RuntimeException("Not found token[" + tokenExchangeCommisionUnit + "]\\/BT price");
    	}
	    BigDecimal p = new BigDecimal(0);
    	//获取价格上调比例配置
    	p = getPrice(body).multiply(this.globalConfService.getCommisionTuneRate());
	   
		/*HttpStatus statusCode = responseEntity.getStatusCode();
		if(statusCode.is2xxSuccessful()) {
			
			if(body.equals("[]")) {
				throw new RuntimeException("Not found token[" + tokenExchangeCommisionUnit + "]\\/BT price");
			}
			//获取价格上调比例配置
			p = getPrice(body).multiply(this.globalConfService.getCommisionTuneRate());
		} else {
			//必须取到价格，否则业务无法进行下去
			throw new RuntimeException("Not found " + tokenExchangeCommisionUnit + "/BT price info......");
		}*/


	    BigDecimal commisionUnitTotal = total.divide(p,8, RoundingMode.DOWN);
	    log.info("price:" + p +", total distribute:" + total);
	    log.info("commision unit total:" + commisionUnitTotal);

	    //换算成返佣币种后的总数量
	    orderReceipt.setCommisionUnit(tokenExchangeCommisionUnit);
	    orderReceipt.setCommisionQuantity(commisionUnitTotal);
	    return orderReceipt;

	}

	/**
	 * 币币交易手续费返佣
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	@Klock(keys= {"#orderReceipt.orderMemberId"})
	public void distributeExchageOrderFee(ExchangeOrderReceipt orderReceipt) {
		
			//带上rocket message id，便于排查问题，没有此字段，调试将会很困难
			if (Strings.isNullOrEmpty(orderReceipt.getMqMessageId())) {
				throw new RuntimeException("distributeExchageOrderFee, mq message id is empty");
			}
			//转换成返佣币种
			orderReceipt = exchangeToken2CommisionTokenRate(orderReceipt);
	
			// 计算返还给直接交易用户的
			DistributeResult distributeResult = distributeExchangeFee2TxMember(orderReceipt);
			ExchangeOrderReceipt copy = distributeResult.getCopy();
	
			List<MemberRecommendCommision> commisionList = distributeResult.getList();
			// 计算返还给直接交易用户的邀请人链的
			List<Long> inviterIdList = this.memberInviteService.getMemberInviteChainIdList(orderReceipt.getOrderMemberId());
	
			log.info(orderReceipt.getOrderMemberId() + ", invite chain:" + inviterIdList);
	
			if (null != inviterIdList && !inviterIdList.isEmpty()) {
				for (int recommendLevel = 0; recommendLevel <  inviterIdList.size(); recommendLevel++) {
					//无钱可返，退出
					if (orderReceipt.getCommisionQuantity().setScale(8, RoundingMode.DOWN).compareTo(BigDecimal.ZERO) == 0) {
						log.info("not enough to distribute, skip");
						break;
					}
	
					Object uid = inviterIdList.get(recommendLevel);
	
					long inviterId = new Long(((Integer) uid).intValue());
	
					MemberExtend member = this.memberBenefitsExtendsService.getMemberExtendByMemberId(inviterId);
					if(member != null) {
						boolean levelNoLimit = false;
						boolean levelLimit = false;
		
		
						//检查返佣配置，如果3层与无限层（0层）同时存在，抛出业务错误
						int level = member.getLevelId();
						List<MemberRecommendCommisionSetting> rcsList = this.recommendCommisionSettingService.getRecommentCommisionByMemberLevel(level);
						if(rcsList == null) {
							System.out.println(member);
						}
						for (MemberRecommendCommisionSetting mrcs : rcsList) {
							if(mrcs.getRecommendLevel() == 0) {
								levelNoLimit = true;
							}
							if(mrcs.getRecommendLevel() == 1 || mrcs.getRecommendLevel() == 2 || mrcs.getRecommendLevel() == 3) {
								levelLimit = true;
							}
						}
						if(levelLimit == true && levelNoLimit == true) {
							throw new RuntimeException("recommend commision level setting is ERROR,couldn't exist at the same time ");
						}
						//获得返给当前层次的配置
						MemberRecommendCommisionSetting rcs = getRecommendCommisionRatioByLevel(rcsList, recommendLevel + 1);
		
						if(rcs != null) {
		
							//在指定层次以内，计算返佣数量
							BigDecimal distributeRate = rcs.getCommisionRatio();
							if(distributeRate.compareTo(BigDecimal.ZERO) == 0) {
								log.info("distribute exchange fee rate is zero , do nothing");
								continue;
							}
		
							BigDecimal distribute = copy.getCommisionQuantity().multiply(distributeRate).setScale(8, RoundingMode.DOWN);
		
		
							BigDecimal accumulativeQuantity = this.memberRecommendCommisionMapper.getMemberAccumulativeQuantity(inviterId, getDistributingCommisionList());
							log.info("distributeExchageOrderFee:" + Thread.currentThread().getId() + " query result:" + accumulativeQuantity);
							
							String tokenExchangeCommisionUnit = this.globalConfService.getTokenExchangeFeeCommisionUnit();

							BigDecimal commisionUnit2USDTRate = this.tokenExchangeRateService.commision2USDT(tokenExchangeCommisionUnit);
		
							MemberRecommendCommision mrc = new MemberRecommendCommision(copy.getRefId(), inviterId, copy.getOrderMemberId(), recommendLevel + 1, copy.getCommisionUnit(), 
									distribute, orderReceipt.getCommisionUnit2CNY(), orderReceipt.getTxUnit2CommisionUnit(), DistributeTypeEnum.WAIT_DISTRIBUTE.getCode(),distribute, 30, distribute.multiply(commisionUnit2USDTRate).setScale(8, RoundingMode.DOWN));
		
							commisionList.add(mrc);
							log.info("inviter id:" + inviterId + ", distribute result:" + mrc.toString());
							//减去已经准备返的
							copy.setCommisionQuantity(copy.getCommisionQuantity().subtract(distribute).setScale(8, RoundingMode.DOWN));
						} else {
							//超出指定层次，不返
							log.info("inviter id:" + inviterId + ", skip distribute , because of: recommend level=" + (recommendLevel + 1) + " out of setting:" + rcsList );
		
						}
					} else {
						log.info("distribute process:NOT FOUND USER" +inviterId+ " IN DB");
					}
	
					
				}
			}
			if (null != commisionList && !commisionList.isEmpty()) {
				for (MemberRecommendCommision mrc : commisionList) {
					mrc.setMqMsgId(orderReceipt.getMqMessageId());
				}
				commisionList = commisionList.stream()
						.filter(item -> item.getCommisionQuantity().compareTo(BigDecimal.ZERO) > 0)
						.collect(Collectors.toList());
	
			} else {
				log.info("=======================token exchange commision result is empty=================");
			}
			//存入member_recommend_commision
			if(commisionList != null && !commisionList.isEmpty()) {
				this.memberRecommendCommisionMapper.batchInsert(commisionList);
			}

	}

	/**
	 * 币币交易:返佣给直接交易人
	 * @param orderReceipt
	 * @return
	 */
	public DistributeResult distributeExchangeFee2TxMember(ExchangeOrderReceipt orderReceipt) {
		// 币币交易手续费返还
		String tokenExchangeCommisionUnit = this.globalConfService.getTokenExchangeFeeCommisionUnit();
		MemberExtend orderMember = this.memberBenefitsExtendsService.getMemberExtendByMemberId(orderReceipt.getOrderMemberId());
		MemberBenefitsSetting cfg = this.memberBenefitsSettingService.getBenefitsSettingByMemberLevel(orderMember.getLevelId());

		List<MemberRecommendCommision> list = Lists.newArrayList();
		ExchangeOrderReceipt copy = new ExchangeOrderReceipt();
		try {
			BeanUtils.copyProperties(copy, orderReceipt);
			copy.setCommisionUnit(tokenExchangeCommisionUnit);
			copy.setCommisionQuantity(orderReceipt.getCommisionQuantity());
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		//根据配置获取返佣比例
		if(cfg != null) {
			BigDecimal rate = new BigDecimal(0);
			if (cfg != null) {
				if (orderReceipt.getOrderMatchType() == ExchangeTypeEnum.BUY_TAKER.getCode()) {
					rate = cfg.getBuyDiscount();
				} else if (orderReceipt.getOrderMatchType() == ExchangeTypeEnum.SELL_TAKER.getCode()) {
					rate = cfg.getSellDiscount();
				} else if (orderReceipt.getOrderMatchType() == ExchangeTypeEnum.BUY_MAKER.getCode()) {
					rate = cfg.getEntrustBuyDiscount();
				} else if (orderReceipt.getOrderMatchType() == ExchangeTypeEnum.SELL_MAKER.getCode()) {
					rate = cfg.getEntrustSellDiscount();
				}
			}

			BigDecimal distribute = copy.getCommisionQuantity().multiply(rate).setScale(8, RoundingMode.DOWN);
			if(distribute.compareTo(BigDecimal.ZERO) != 0) {

				BigDecimal accumulativeQuantity = this.memberRecommendCommisionMapper.getMemberAccumulativeQuantity(orderReceipt.getOrderMemberId(), getDistributingCommisionList());
				log.info("distributeExchangeFee2TxMember:" + Thread.currentThread().getId() + " query result:" + accumulativeQuantity);
			

				BigDecimal commisionUnit2USDTRate = this.tokenExchangeRateService.commision2USDT(tokenExchangeCommisionUnit);
				MemberRecommendCommision mrc = new MemberRecommendCommision(copy.getRefId(), orderReceipt.getOrderMemberId(),orderReceipt.getOrderMemberId(), 0, tokenExchangeCommisionUnit, 
						distribute, orderReceipt.getCommisionUnit2CNY(),orderReceipt.getTxUnit2CommisionUnit(), DistributeTypeEnum.WAIT_DISTRIBUTE.getCode(), distribute, 30, distribute.multiply(commisionUnit2USDTRate).setScale(8, RoundingMode.DOWN));

				log.info(orderReceipt.getOrderMemberId() + ", distribute result:" + mrc.toString());
				list.add(mrc);
				copy.setCommisionQuantity(copy.getCommisionQuantity().subtract(distribute).setScale(8, RoundingMode.DOWN));
			}
			return new DistributeResult(copy, list);
		} else {
			log.info("member id:" + orderReceipt.getOrderMemberId() + ", level:" + orderMember.getLevelId() + " haven't MemberBenefitsSetting");
		}

		return new DistributeResult(copy, list);


	}

	/**
	 * 会员购买返佣
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	@Klock(keys= {"#orderReceipt.orderMemberId"})
	public void distributeBenefitsOrder(MemberBenefitsOrderReceipt orderReceipt, MemberBenefitsOrder order) {
			if (Strings.isNullOrEmpty(orderReceipt.getMqMsgId())) {
				throw new RuntimeException("distributeBenefitsOrder mq message id is empty");
			}
			int memberUpgradeOpType = 30;
			String memberRecommendCommisionUnit = this.globalConfService.getMemberRecommendCommisionUnit();
			//锁仓天数必须>=0
			if (orderReceipt.getPayType() == BizTypeEnum.MEMBER_LOCK.getCode() && orderReceipt.getLockDay() == 0) {
				throw new RuntimeException("lock day must be grater than zero......");
			}
	
			MemberExtend me = this.memberBenefitsExtendsService.getMemberExtendByMemberId(orderReceipt.getOrderMemberId());
			int orderMemberLevel = 0;
			if (order.getOperateType() == 20) {
				orderMemberLevel = me.getLevelId();
			}
	
			//如果是会员升级，直接从消息中获取升级后的会员层级，避免消息异步带来的DB查询延迟
			if (order.getOperateType() == memberUpgradeOpType) {
				orderMemberLevel = order.getDestLevel();
			}
	
			List<Long> inviterIdList = this.memberInviteService.getMemberInviteChainIdList(orderReceipt.getOrderMemberId());
			//如果没有直接返佣人，则直接退出
			if (null == inviterIdList || inviterIdList.isEmpty()) {
				log.info("user:" + orderReceipt.getOrderMemberId() + " have not inviter, do nothing.");
				return;
			}
	
			BigDecimal platformUnitRate = this.tokenExchangeRateService.getTokenExchangeRate(orderReceipt.getCommisionUnit(), memberRecommendCommisionUnit);
			BigDecimal platformUnitCnyRate = this.tokenExchangeRateService.getTokenExchangeRate(memberRecommendCommisionUnit, Constant.CNY);
			log.info("commision unit-platform unit:" + orderReceipt.getCommisionUnit() + "-" + memberRecommendCommisionUnit+ "=" + platformUnitRate);
			log.info("platform unit-cny:" + memberRecommendCommisionUnit + "-cny=" + platformUnitCnyRate);
			//未能正常获取市价，退出
			if (platformUnitCnyRate == null || platformUnitRate == null) {
				log.error("market service ERROR ,not found rate commision unit:" + orderReceipt.getCommisionUnit()+ ", platfrom token:" + memberRecommendCommisionUnit);
				return;
			}
	
			ExchangeOrderReceipt copy = new ExchangeOrderReceipt();
	
			try {
				BeanUtils.copyProperties(copy, orderReceipt);
	
				copy.setCommisionUnit(memberRecommendCommisionUnit);
				copy.setCommisionQuantity(orderReceipt.getCommisionQuantity().multiply(platformUnitRate).setScale(8, RoundingMode.DOWN));
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
	
			List<MemberRecommendCommision> commisionList = Lists.newArrayList();
	
			if (null != inviterIdList && inviterIdList.size() >= 1) {
				//直接返佣人
				Object uid = inviterIdList.get(0);
				long directInviterId = 0;
	
				if (uid instanceof Integer) {
					directInviterId = new Long(((Integer) uid).intValue());
				}
				if (uid instanceof Long) {
					directInviterId = (Long) uid;
				}
	
				MemberExtend inviter = this.memberBenefitsExtendsService.getMemberExtendByMemberId(directInviterId);
	
				MemberBenefitsSetting cfg = this.memberBenefitsSettingService.getBenefitsSettingByMemberLevel(inviter.getLevelId());
	
				log.info("order user:" + me.toString() + ", inviter user:" + inviter.toString() + ", cfg:" + cfg.toString()+ ", user id " + orderReceipt.getOrderMemberId() + ", inviter:[" + inviter.getMemberId() + "] is "+ inviter.getLevelId());
	
				//根据配置获取返佣比例
				BigDecimal distributeRate = null;
				if (orderMemberLevel == MemberLevelTypeEnum.NORMAL.getCode()) {
					log.info("normal user have't benefits order fee distribute...because inviter:[" + inviter.getMemberId()
							+ "] is " + inviter.getLevelId());
					return;
				}
				if (orderMemberLevel == MemberLevelTypeEnum.VIP1.getCode()) {
					if (orderReceipt.getPayType() == BizTypeEnum.MEMBER_LOCK.getCode() || orderReceipt.getPayType() == 30) {
						distributeRate = cfg.getVip1LockDiscount();
					}
					if (orderReceipt.getPayType() == BizTypeEnum.MEMBER_BUY.getCode()) {
						distributeRate = cfg.getVip1BuyDiscount();
					}
				}
	
				if (orderMemberLevel == MemberLevelTypeEnum.VIP2.getCode()) {
					if (orderReceipt.getPayType() == BizTypeEnum.MEMBER_LOCK.getCode() || orderReceipt.getPayType() == 30) {
						distributeRate = cfg.getVip2LockDiscount();
					}
					if (orderReceipt.getPayType() == BizTypeEnum.MEMBER_BUY.getCode()) {
						
						
						distributeRate = cfg.getVip2BuyDiscount();
					}
				}
	
				if (orderMemberLevel == MemberLevelTypeEnum.VIP3.getCode()) {
					if (orderReceipt.getPayType() == BizTypeEnum.MEMBER_LOCK.getCode() || orderReceipt.getPayType() == 30) {
						distributeRate = cfg.getVip3LockDiscount();
					}
					if (orderReceipt.getPayType() == BizTypeEnum.MEMBER_BUY.getCode()) {
						
						
						distributeRate = cfg.getVip3BuyDiscount();
					}
				}
	
				if (orderMemberLevel == MemberLevelTypeEnum.AGENT.getCode()) {
					if (orderReceipt.getPayType() == BizTypeEnum.MEMBER_LOCK.getCode() || orderReceipt.getPayType() == 30) {
						distributeRate = cfg.getAgentLockDiscount();
					}
					if (orderReceipt.getPayType() == BizTypeEnum.MEMBER_BUY.getCode()) {
						
						
						distributeRate = cfg.getAgentBuyDiscount();
					}
				}
	
				//返佣比例==0，退出
				if (distributeRate.compareTo(BigDecimal.ZERO) == 0) {
					log.info("distribute member benefits order fee rate is zero , do nothing");
					return;
				}
				log.info("distributeRate:" + distributeRate);
				BigDecimal distribute = new BigDecimal(0);
	
				if (orderReceipt.getPayType() == BizTypeEnum.MEMBER_BUY.getCode()) {
					
					distribute = copy.getCommisionQuantity().multiply(distributeRate).setScale(8, RoundingMode.DOWN);
					
				}
				if (orderReceipt.getPayType() == BizTypeEnum.MEMBER_LOCK.getCode()) {
					distribute = copy.getCommisionQuantity().multiply(distributeRate).divide(new BigDecimal(365), 8, RoundingMode.DOWN).multiply(new BigDecimal(orderReceipt.getLockDay()).setScale(8, RoundingMode.DOWN));
	
				}
				BigDecimal accumulativeQuantity = this.memberRecommendCommisionMapper.getMemberAccumulativeQuantity(directInviterId, getDistributingCommisionList());
				log.info("distributeBenefitsOrder:" + Thread.currentThread().getId() + " query result:" + accumulativeQuantity);

				//String tokenExchangeCommisionUnit = this.globalConfService.getTokenExchangeFeeCommisionUnit();

				BigDecimal commisionUnit2USDTRate = this.tokenExchangeRateService.commision2USDT(memberRecommendCommisionUnit);
				
	
				MemberRecommendCommision mrc = new MemberRecommendCommision(orderReceipt.getRefId(), directInviterId,orderReceipt.getOrderMemberId(), 1, memberRecommendCommisionUnit, distribute, platformUnitCnyRate,
						platformUnitRate, DistributeTypeEnum.WAIT_DISTRIBUTE.getCode(), distribute, orderReceipt.getPayType(), distribute.multiply(commisionUnit2USDTRate).setScale(8, RoundingMode.DOWN));
	
				commisionList.add(mrc);
	
			}
			if (null != commisionList && !commisionList.isEmpty()) {
				for (MemberRecommendCommision mrc : commisionList) {
					mrc.setMqMsgId(orderReceipt.getMqMsgId());
				}
				commisionList = commisionList.stream().filter(item -> item.getCommisionQuantity().compareTo(BigDecimal.ZERO) > 0).collect(Collectors.toList());
	
			} else {
				log.info("====================member buy===commision result is empty=================");
			}
			if(null != commisionList && !commisionList.isEmpty()) {
	
				this.memberRecommendCommisionMapper.batchInsert(commisionList);
			}
			//维护每日统计数据
			MemberRecommendCommision mrc = commisionList.get(0);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
			MemberFeeDayStat mfds = this.memberFeeDayStatService.getMemberFeeDayStatByDay(sdf.format(orderReceipt.getPayTime()));
			MemberFeeDayStat stat = new MemberFeeDayStat();
			stat.setStatisticDate(orderReceipt.getPayTime());
	
			if (mrc.getBizType() == BizTypeEnum.MEMBER_LOCK.getCode()) {
				stat.setLockCommision(mrc.getCommisionQuantity());
				stat.setLockCount(1l);
				stat.setLockUnitQuantity(orderReceipt.getCommisionQuantity());
			}
			if (mrc.getBizType() == BizTypeEnum.MEMBER_BUY.getCode()) {
				
				
				stat.setBuyCommision(mrc.getCommisionQuantity());
				stat.setBuyUnitQuantity(orderReceipt.getCommisionQuantity());
				stat.setBuyCount(1l);
			}
			if (null != mfds) {
				stat.setVersion(mfds.getVersion());
				stat.setUnit(mfds.getUnit());
			}
			if (null == mfds) {
				stat.setUnit(memberRecommendCommisionUnit);
			}
	
			this.memberFeeDayStatService.updateDailyStat(stat);

	}
	
	private String getDistributingCommisionList() {
		String distributingCommisionIdList = (String)this.redisTemplate.opsForValue().get(KeyGenerator.getDistributingCommisionList());
		if(Strings.isNullOrEmpty(distributingCommisionIdList)) {
			distributingCommisionIdList = "";
		}
		return distributingCommisionIdList;
	}

	@Override
	public List<MemberRecommendCommision> getMemberRecommendCommisionByStatus(int status) {
		return this.memberRecommendCommisionMapper.getMemberRecommendCommisionByStatus(status);
	}

	@Override
	public boolean updateDistributeStatus(List<MemberRecommendCommision> successDistributeList) {
		// TODO(shine) batch update
		for (MemberRecommendCommision rc : successDistributeList) {
			this.memberRecommendCommisionMapper.updateDistributeStatus(rc);
		}
		return false;
	}
	
	@Override
	public boolean updateDistributingStatus(List<Long> ids) {
		Collection<MemberRecommendCommision> mrcList = this.listByIds(ids);
		for (MemberRecommendCommision memberRecommendCommision : mrcList) {
			memberRecommendCommision.setDistributeStatus(11);
			memberRecommendCommision.setUpdateTime(new Date());
		}
		boolean flag = this.updateBatchById(mrcList);
		return flag;
	}

	@Override
	public List<MemberRecommendCommision> countMemberRecommendCommision(Long memberId) {
		return this.baseMapper.countMemberRecommendCommisionByBizType(memberId);
	}

	@Override
	public IPage<MemberRecommendCommision> getRecommendCommisionBySend(Long memberId, PageParam param) {
		QueryWrapper<MemberRecommendCommision> queryWrapper = new QueryWrapper<>();
		queryWrapper.lambda().eq(MemberRecommendCommision::getDeliverToMemberId, memberId)
				.eq(MemberRecommendCommision::getDistributeStatus, "20")
				.orderByDesc(MemberRecommendCommision::getDistributeTime);
		if (StringUtils.isNotBlank(param.getStartTime())) {
			queryWrapper.ge(MemberRecommendCommision.CREATE_TIME, param.getStartTime());
		}
		if (StringUtils.isNotBlank(param.getEndTime())) {
			queryWrapper.le(MemberRecommendCommision.CREATE_TIME, param.getEndTime());
		}

		return this.baseMapper.selectPage(new Page<MemberRecommendCommision>(param.getPage(), param.getPageSize()),
				queryWrapper);
	}

	@Override
	public List<RecommendCommisionVo> findRecommendCommisionListMapper(Page<RecommendCommisionVo> commisionPage, Long memberId, String startTime, String endTime) {
		List<RecommendCommisionVo> list = this.baseMapper.findMemberRecommendCommisionList(commisionPage, memberId, startTime, endTime);
		return list;
	}

	@Override
	public IPage<MemberRecommendCommision> getRecommendCommisionLists(Long memberId, PageParam param, int bizType) {
		QueryWrapper<MemberRecommendCommision> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq(MemberRecommendCommision.DELIVER_TO_MEMBER_ID, memberId);
		queryWrapper.ne(MemberRecommendCommision.ORDER_MEMBER_ID, memberId);
		queryWrapper.ne(MemberRecommendCommision.BIZ_TYPE, BizTypeEnum.TOKEN_EXCHANGE.getCode());
		queryWrapper.orderByDesc(MemberRecommendCommision.DISTRIBUTE_TIME);
		if (StringUtils.isNotBlank(param.getStartTime())) {
			queryWrapper.ge(MemberRecommendCommision.CREATE_TIME, param.getStartTime());
		}
		if (StringUtils.isNotBlank(param.getEndTime())) {
			queryWrapper.le(MemberRecommendCommision.CREATE_TIME, param.getEndTime());
		}
		return this.baseMapper.selectPage(new Page<MemberRecommendCommision>(param.getPage(), param.getPageSize()),
				queryWrapper);
	}

	public MemberRecommendCommisionSetting getRecommendCommisionRatioByLevel(
			List<MemberRecommendCommisionSetting> rcsList, int level) {
		MemberRecommendCommisionSetting notLimit = null;
		MemberRecommendCommisionSetting result = null;
		for (MemberRecommendCommisionSetting mrcs : rcsList) {
			if (mrcs.getRecommendLevel() == level) {
				result = mrcs;
			}
			if (mrcs.getRecommendLevel() == 0) {
				notLimit = mrcs;
			}
		}
		return null == result ? notLimit : result;
	}

	@Override
	public IPage<MemberRecommendCommision> getRecommendCommisionList(Long memberId, PageParam param, int bizType) {
		QueryWrapper<MemberRecommendCommision> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq(MemberRecommendCommision.DELIVER_TO_MEMBER_ID, memberId);
		queryWrapper.eq(MemberRecommendCommision.ORDER_MEMBER_ID, memberId);
		queryWrapper.eq(MemberRecommendCommision.BIZ_TYPE, BizTypeEnum.TOKEN_EXCHANGE.getCode());
		queryWrapper.orderByDesc(MemberRecommendCommision.DISTRIBUTE_TIME);
		if (StringUtils.isNotBlank(param.getStartTime())) {
			queryWrapper.ge(MemberRecommendCommision.CREATE_TIME, param.getStartTime());
		}
		if (StringUtils.isNotBlank(param.getEndTime())) {
			queryWrapper.le(MemberRecommendCommision.CREATE_TIME, param.getEndTime());
		}
		return this.baseMapper.selectPage(new Page<MemberRecommendCommision>(param.getPage(), param.getPageSize()),
				queryWrapper);
	}

	public static BigDecimal getPrice(String s) {
		System.out.println(JSONArray.isValid(s));

		JSONArray arr = JSONArray.parseArray(s);
		BigDecimal bd = new BigDecimal(0);
		for (int i = 0; i < arr.size() - 1; i++) {
			// 开盘、最高、最低、收盘价
			String[] a = arr.get(i).toString().replace("[", "").replace("]", "").split(",");

			bd = bd.add(new BigDecimal(a[4]));
		}
		BigDecimal p = bd.divide(new BigDecimal(2)).setScale(8, RoundingMode.DOWN);
		System.out.println(p);
		return p;
	}


}

@Data
@AllArgsConstructor
class DistributeResult {
	ExchangeOrderReceipt copy;
	List<MemberRecommendCommision> list;
}