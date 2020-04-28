package com.spark.bitrade.biz;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.spark.bitrade.biz.MemberDailyTaskBizService.RecordCheckPojo;
import com.spark.bitrade.common.NewYearExceptionMsg;
import com.spark.bitrade.common.RedisUtil;
import com.spark.bitrade.common.ReidsKeyGenerator;
import com.spark.bitrade.common.ThreadPoolUtils;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.NewYearCoin;
import com.spark.bitrade.entity.NewYearConfig;
import com.spark.bitrade.entity.NewYearMemberAccept;
import com.spark.bitrade.entity.NewYearMemberHas;
import com.spark.bitrade.entity.NewYearMemberInfo;
import com.spark.bitrade.entity.NewYearMemberRecord;
import com.spark.bitrade.entity.NewYearMineral;
import com.spark.bitrade.entity.NewYearProjectAdvertisement;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.mapper.NewYearMemberHasMapper;
import com.spark.bitrade.mapper.NewYearProjectAdvertisementMapper;
import com.spark.bitrade.service.IMemberApiService;
import com.spark.bitrade.service.IMemberBenefitsService;
import com.spark.bitrade.service.NewYearCoinService;
import com.spark.bitrade.service.NewYearConfigService;
import com.spark.bitrade.service.NewYearMemberAcceptService;
import com.spark.bitrade.service.NewYearMemberInfoService;
import com.spark.bitrade.service.NewYearMemberRecordService;
import com.spark.bitrade.service.NewYearMineralService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.DateUtil;
import com.spark.bitrade.util.MessageRespResult;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 我的矿石相关接口业务
 * </p>
 *
 * @author Administrator
 * @since 2019年12月30日
 */
@Service
@Slf4j
public class MyOreService  {

	@Resource
	private RedisUtil redisUtil;
	@Resource
	private NewYearMineralService newYearMineralService;
	@Resource
	private NewYearMemberHasMapper newYearMemberHasMapper;
	@Resource
	private NewYearMemberRecordService newYearMemberRecordService;
	@Resource
	private NewYearMemberInfoService newYearMemberInfoService;
	@Autowired
	private IMemberApiService iMemberApiService;
	@Resource
	private NewYearPushService newYearPushService;
	@Resource
	private NewYearProjectAdvertisementMapper projectAdvertisementMapper;
	@Resource
	private NewYearConfigService newYearConfigService;
	@Resource
	private NewYearCoinService newYearCoinService;
	@Resource
	private NewYearMemberAcceptService newYearMemberAcceptService;
	@Resource
	private MemberDailyTaskBizService memberDailyTaskBizService;
	@Resource
	private IMemberBenefitsService iMemberBenefitsService;

	@Transactional
	public MessageRespResult<String> giveMyOre(Member member , String oreName , int count , Long customerId, HttpServletRequest request) {
		if(count < 1) {
			throw new MessageCodeException(NewYearExceptionMsg.MINING_COUNT_Min_ONE);
		}
		if(customerId == null || customerId == 0L){
			throw new MessageCodeException(NewYearExceptionMsg.CUSTOMER_ID_ERROR);
		}
		if(customerId.longValue() == member.getId().longValue()){
			throw new MessageCodeException(NewYearExceptionMsg.GIVE_IT_TO_YOURSELF);
		}
		MessageRespResult<Member> customer = this.iMemberApiService.getMember(customerId);
		if(!customer.isSuccess()) {
			throw new MessageCodeException(NewYearExceptionMsg.CUSTOMER_ID_ERROR);
		}
		//查看用户指定矿石余量
		QueryWrapper<NewYearMemberHas> queryWrapper = new QueryWrapper<NewYearMemberHas>();
		queryWrapper.eq("member_id", member.getId()).eq("mineral_name", oreName).eq("status", 1);
		if(this.newYearMemberHasMapper.selectCount(queryWrapper) >= count) {
			//合成锁
			if(this.redisUtil.incrementLock(ReidsKeyGenerator.getMineralLock(member.getId().toString()), 5) > 1) {
				throw new MessageCodeException(NewYearExceptionMsg.RE_CONSUME_ORE);
			}
			
			//扣除矿石
			List<NewYearMemberHas> has = this.newYearMemberHasMapper.selectPage(new Page<NewYearMemberHas>(1 , count), queryWrapper).getRecords();
			
			//流水
			NewYearMemberRecord record = new NewYearMemberRecord();
			record.setCount(count);
			record.setCreateTime(new Date());
			record.setMemberId(member.getId());
			record.setMineralId(has.get(0).getMineralId());
			record.setMineralName(has.get(0).getMineralName());
			record.setOpType(2);
			record.setRemark("送给好友" + customerId.toString());
			this.newYearMemberRecordService.save(record);// 赠送
			record.setId(null);
			record.setMemberId(customerId);
			record.setOpType(3);
			record.setRemark("好友"+member.getId().toString()+"赠送");
			this.newYearMemberRecordService.save(record);//获取
			
			
			has.stream().forEach(h ->{
				h.setStatus(0);
				this.newYearMemberHasMapper.updateById(h);
				//用户新增矿石
				h.setCreateTime(new Date());
				h.setFromMemberId(member.getId());
				h.setDirection(0);
				h.setFromWhere(1);
				h.setId(null);
				h.setMemberId(customerId);
				h.setRefId(record.getId());
				// 随机获取一条广告语
				NewYearProjectAdvertisement randomOneRecord = projectAdvertisementMapper.findRandom();
				h.setSponsorDescription(randomOneRecord.getProjectIntroduction());
				h.setStatus(1);
				this.newYearMemberHasMapper.insert(h);
			});
			// 消息通知用户获得矿石
			Map<String, Object> silkPlatInfo = newYearMineralService.findSilkPlatInfo();
			if (silkPlatInfo!=null){
				String language = request.getHeader("language");
				Object msg=silkPlatInfo.get("offlineTitleCn");
				if("en_US".equals(language)){
					msg=silkPlatInfo.get("offlineTitleEn");
				}
				String content=String.format(msg.toString(),member.getId(),count,oreName);
				newYearPushService.sendStationMessage(content,content,customerId);
			}
			this.redisUtil.delKey(ReidsKeyGenerator.getMineralLock(member.getId().toString()));
			return MessageRespResult.success("赠送成功");
		}
		else {
			throw new MessageCodeException(NewYearExceptionMsg.INSUFFICIENT_ORE_STOCK);
		}
	}
	
	/**
	 * 合成令牌
	 * @param member
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月31日
	 */
	@Transactional
	public MessageRespResult<Map<String,String>> synthesisOre(Member member) {
		if(this.redisUtil.setHasVal(ReidsKeyGenerator.getSynthesisMemberIdSetKey(), member.getId().toString())) {
			throw new MessageCodeException(NewYearExceptionMsg.RE_SYNTHESIZE);
		}
		NewYearConfig config = this.newYearConfigService.findNewYearConfig().get(0);
		if(System.currentTimeMillis() > config.getLuckyStartTime().getTime()) {
			throw new MessageCodeException(NewYearExceptionMsg.PUT_SYNTHESIS_TIME_OUT);
		}
		
		//判断账号合成记录
		NewYearMemberInfo info = this.newYearMemberInfoService.findRecordByMemberId(member.getId());
		
		//剩余矿石判断
		Set<Object> types = this.redisUtil.getSet(ReidsKeyGenerator.getMineralType());
		if(CollectionUtils.isEmpty(types)) {
			throw new MessageCodeException(NewYearExceptionMsg.NO_ORE_CONFIGURATION_FOUND);
		}
		List<Map<String, Object>> memberHas = this.newYearMemberHasMapper.myOreCount(member.getId());
		Map<String, Integer> counts = new HashMap<String, Integer>();
		memberHas.stream().forEach(rt ->{counts.put(rt.get("mn").toString(), Integer.parseInt(rt.get("ct").toString()));});
		for(Object type : types) {
			if(!counts.containsKey(type.toString()) || counts.get(type.toString()) < 1) {
				throw new MessageCodeException(NewYearExceptionMsg.LACK_OF_ORE);
			}
		}
		
		//合成锁
		if(this.redisUtil.incrementLock(ReidsKeyGenerator.getMineralLock(member.getId().toString()), 5) > 1) {
			throw new MessageCodeException(NewYearExceptionMsg.RE_CONSUME_ORE);
		}
		
		//合成
		for(Object type : types) {
			QueryWrapper<NewYearMemberHas> queryWrapper = new QueryWrapper<NewYearMemberHas>();
			queryWrapper.eq("member_id", member.getId());
			queryWrapper.eq("status", 1);
			queryWrapper.eq("mineral_name", type.toString());
			NewYearMemberHas has = null ;
			try {
				has = this.newYearMemberHasMapper.selectList(queryWrapper).get(0);
				if(has == null) {
					throw new MessageCodeException(NewYearExceptionMsg.LACK_OF_ORE);
				}
			} catch (Exception e) {
				throw new MessageCodeException(NewYearExceptionMsg.LACK_OF_ORE);
			}
			has.setStatus(0);//扣除
			this.newYearMemberHasMapper.updateById(has);
			//流水
			NewYearMemberRecord record = new NewYearMemberRecord();
			record.setCount(1);
			record.setCreateTime(new Date());
			record.setMemberId(member.getId());
			record.setMineralId(has.getMineralId());
			record.setMineralName(has.getMineralName());
			record.setOpType(4);
			record.setRemark("合成钥匙");
			this.newYearMemberRecordService.save(record);
		}
		
		//生成令牌
		info.setToken(this.redisUtil.getMineralToken());
		info.setTokenTime(new Date());
		// 随机获取一条广告语
		NewYearProjectAdvertisement randomOneRecord = projectAdvertisementMapper.findRandom();
		info.setTokenSponsorDescription(randomOneRecord.getCoinNam()+randomOneRecord.getProjectIntroduction());
		this.newYearMemberInfoService.updateById(info);
		
		this.redisUtil.synthesis(member);//合成后缓存处理
		//合成后海报信息内容内容 项目方币种，项目方介绍
		Map<String,String> resultInfo = Maps.newHashMap();
		resultInfo.put("unit",randomOneRecord.getCoinNam());
		resultInfo.put("projectIntroduction",randomOneRecord.getProjectIntroduction());
		return MessageRespResult.success4Data(resultInfo);
	}
	
	/**
	 * 获取最新合成记录
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月31日
	 */
	public MessageRespResult<List<String>> synthesisNewest() {
        List<Object> top = this.redisUtil.lGet(ReidsKeyGenerator.getSynthesisMemberTop10(),0L,-1L);
        if(CollectionUtils.isEmpty(top)) {
            return MessageRespResult.success4Data(new ArrayList<>());
        }
        List<String> resultList = Lists.newArrayList();
        top.stream().forEach(item ->{
			StringBuilder result = new StringBuilder();
			String[] arr = item.toString().split("_");
            result.append(arr[0] + ":");
            String time = arr[1];
            BigDecimal mins = DateUtil.diffMinute(new Date(Long.parseLong(time)));
            long d = 0L;
			long h = 0L;
			long s = 0L;
			long m = mins.longValue();
            if(mins.compareTo(new BigDecimal(60)) >= 0){
                //超过一个小时
                h = m / 60;
                m = m % 60;
                if(h / 24 > 1){
                    //超过一天
                    d = h / 24;
                    h = h % 24;
                }
            }
			if(mins.compareTo(BigDecimal.ONE) < 0){
				s = mins.multiply(new BigDecimal(60)).longValue();
			}
			if(s == 0L){
				s = 1L;
			}
			result.append((d > 0L) ? (d + "天") : "");
			result.append((h > 0L) ? (h + "小时") : "");
			result.append((m > 0L) ? (m + "分钟") : "");
			result.append((s > 0) ? (s + "秒") : "");
			result.append("前合成了太阳令牌");
			resultList.add(result.toString());
        });
        return MessageRespResult.success4Data(resultList);
	}
	/**
	 * 获取用户不同种类矿石数量
	 * @param member
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月30日
	 */
	public List<Map<String, Object>> myOreCount(Member member) {
		if(!this.redisUtil.keyExist(ReidsKeyGenerator.getMineralType()) || this.redisUtil.getKeyLength(ReidsKeyGenerator.getMineralType()) < 1) { //加锁添加矿石类型
			long lock = this.redisUtil.incrementLock(ReidsKeyGenerator.getMineralTypeLock(), 6);
			if(lock > 1) { //处理加载矿石配置
				long lockTime = System.currentTimeMillis();
				while(!this.redisUtil.keyExist(ReidsKeyGenerator.getMineralType())) {
					if(System.currentTimeMillis() - lockTime > 5000) { //等待超时
						throw new MessageCodeException(NewYearExceptionMsg.NO_ORE_CONFIGURATION_FOUND);
					}
				}
			}
			else { //加入矿石
				List<NewYearMineral> list = this.newYearMineralService.list();
				if(CollectionUtils.isNotEmpty(list)) {
					this.redisUtil.updateMineralType(list.stream().map(NewYearMineral::getMineralName).collect(Collectors.toList()).toArray(new String[list.size()]));
				}
				else {
					throw new MessageCodeException(NewYearExceptionMsg.NO_ORE_CONFIGURATION_FOUND);
				}
			}
		}
		Set<Object> minerals = this.redisUtil.getSet(ReidsKeyGenerator.getMineralType());
		//-----------前端要求返回list
		if(CollectionUtils.isNotEmpty(minerals)) {
			List<Map<String, Object>> result = this.newYearMemberHasMapper.myOreCount(member.getId());
			minerals.stream().forEach(item -> {
				//给返回结果加上用户没有的矿石数量为0
				AtomicBoolean flag = new AtomicBoolean(false);
				result.stream().forEach(res -> {
					if(item.toString().equals(res.get("mn"))){
						flag.set(true);
					}
				});
				if(!flag.get()){
					Map<String, Object> map = Maps.newHashMap();
					map.put("mn",item);
					map.put("ct",0);
					result.add(map);
				}
			});
			Map<String, Object> counts = Maps.newHashMap();
			//已合成的令牌
			QueryWrapper<NewYearMemberInfo> queryWrapper = new QueryWrapper<NewYearMemberInfo>();
			queryWrapper.eq("member_id", member.getId());
			queryWrapper.isNotNull("token");
			counts.put("mn", "太阳令牌");
			counts.put("ct", this.newYearMemberInfoService.count(queryWrapper) > 0 ? 1 : 0);
			//给返回结果加上太阳令牌数量
			result.add(counts);
			return result;
		}
		//---------------------------------
		throw new MessageCodeException(NewYearExceptionMsg.NO_ORE_CONFIGURATION_FOUND);
	}

	/**
	 * 获取当前已完成合成钥匙用户总数
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月30日
	 */
	public Map<String,Object> completedMemberCount() {
		List<NewYearConfig> newYearConfig = newYearConfigService.findNewYearConfig();
		Map<String,Object> result = Maps.newHashMap();
		result.put("startTime",newYearConfig.get(0).getLuckyStartTime());
		result.put("memberCount",this.redisUtil.completeMemberCount());
		return result;
	}
	
	/**
	 *  我的矿石流水
	 * @param member
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月31日
	 */
	public IPage<NewYearMemberRecord> myOreHistory(Member member , int pageNum , int size) {
		IPage<NewYearMemberRecord> page = new Page<NewYearMemberRecord>(pageNum, size);
		QueryWrapper<NewYearMemberRecord> queryWrapper = new QueryWrapper<NewYearMemberRecord>();
		queryWrapper.eq("member_id", member.getId());
		queryWrapper.orderByDesc("create_time");
		return this.newYearMemberRecordService.page(page, queryWrapper);
	}
	
	/**
	 * 获奖记录
	 * @param member
	 * @return
	 * @author zhaopeng
	 * @since 2020年2月4日
	 */
	public MessageRespResult<List<String>> lotteryList(Member member) {
		List<NewYearMemberAccept> accepts = this.newYearMemberAcceptService.list(new QueryWrapper<NewYearMemberAccept>().eq("member_id", member.getId()));
		if(CollectionUtils.isEmpty(accepts)) {
			return MessageRespResult.success("", new HashMap<String , Object>());//返回正常奖励应得数目
		}
		//NewYearMemberInfo memberInfo = this.newYearMemberInfoService.findRecordByMemberId(member.getId());
		Map<String, BigDecimal> result = accepts.stream().collect(Collectors.toMap(NewYearMemberAccept::getCoinUnit, NewYearMemberAccept::getTotalAmount));
		List<String> data = new ArrayList<String>();
		NewYearProjectAdvertisement randomOneRecord = projectAdvertisementMapper.findRandom();
		if(randomOneRecord!=null){
			data.add(randomOneRecord.getCoinNam() + " 恭喜您获得");
		}
		for(String key : result.keySet()) {
			data.add(result.get(key).compareTo(BigDecimal.ZERO) == 0 ? key : result.get(key).toString()+key);
		}
		return MessageRespResult.success("", data);
	}
	
	/**
	 * 开奖
	 * @param member
	 * @return
	 * @author zhaopeng
	 * @since 2020年1月13日
	 */
	public MessageRespResult<List<String>> lottery(Member member , Integer appId) {
		//检查开奖时间
		NewYearConfig config = this.newYearConfigService.findNewYearConfig().get(0);
		AssertUtil.isTrue(System.currentTimeMillis() > config.getLuckyStartTime().getTime() && System.currentTimeMillis() < config.getLuckyEndTime().getTime(), NewYearExceptionMsg.SYNTHESIS_TIME_OUT);
		//检查用户是否有开奖权限
		AssertUtil.isTrue(this.redisUtil.setHasVal(ReidsKeyGenerator.getSynthesisMemberIdSetKey(), member.getId()), NewYearExceptionMsg.HAS_NOT_SYNTHESIS_ORE);
		AssertUtil.isTrue(!this.redisUtil.setHasVal(ReidsKeyGenerator.getLotteryMembers(), member.getId()), NewYearExceptionMsg.RE_SYNTHESIS_ORE);
		AssertUtil.notNull(this.newYearMemberInfoService.getOne(new QueryWrapper<NewYearMemberInfo>().eq("member_id", member.getId()).isNotNull("token")),NewYearExceptionMsg. HAS_NOT_SYNTHESIS_ORE);//没有合成令牌记录
		//加锁
		AssertUtil.isTrue(this.redisUtil.incrementLock(ReidsKeyGenerator.getLotteryLock(member.getId().toString()), 3) <= 1, NewYearExceptionMsg.RE_SYNTHESIS_ORE);
		
		//获取单人次开奖获取奖金金额
		List<NewYearCoin> coins = this.newYearCoinService.list();
		AssertUtil.notEmpty(coins, NewYearExceptionMsg.HAS_NOT_MONEY);
	    NewYearMemberInfo memberInfo = this.newYearMemberInfoService.findRecordByMemberId(member.getId());
	    AssertUtil.notNull(memberInfo, NewYearExceptionMsg.HAS_NOT_SYNTHESIS_ORE);
	    AssertUtil.notNull(memberInfo.getToken(), NewYearExceptionMsg.HAS_NOT_SYNTHESIS_ORE);
		long complNum = this.redisUtil.setLength(ReidsKeyGenerator.getSynthesisMemberIdSetKey()); //合成令牌总人数
		AssertUtil.isTrue(complNum > 0,NewYearExceptionMsg. HAS_NOT_SYNTHESIS_ORE);
		//Map<String, Object> result = new HashMap<String, Object>();
		List<String> result = new ArrayList<String>();
		NewYearProjectAdvertisement randomOneRecord = projectAdvertisementMapper.findRandom();
		if(randomOneRecord!=null){
			result.add(randomOneRecord.getCoinNam() + " 恭喜您获得");
		}
		//result.add(memberInfo.getTokenSponsorDescription());
		coins.stream().forEach(coin ->{
			BigDecimal allCount = coin.getWardAmount().divide(new BigDecimal(complNum), 8 ,RoundingMode.DOWN);//当前币种应获取赠送总额
			result.add(allCount + coin.getCoinUnit());
		});
		MessageRespResult<Integer> vipSend = this.iMemberBenefitsService.giveMemberVip1(member.getId(), appId);
		log.info("赠送会员结果---->" + JSON.toJSONString(vipSend));
		if(vipSend.getData().intValue() == 0) {
			result.add("1个月VIP1会员");
			NewYearMemberAccept accept = new NewYearMemberAccept();
			accept.setMemberId(member.getId());
			accept.setToken(memberInfo.getToken());
			accept.setCoinUnit("1个月VIP1会员");
			accept.setSendedAmount(new BigDecimal(0));
			accept.setLockAmount(new BigDecimal(0));
			accept.setReleasedAmount(new BigDecimal(0));
			accept.setEveryMaxReleasedAmount(new BigDecimal(0));
			accept.setTotalAmount(new BigDecimal(0));
			accept.setCreateTime(new Date());
			this.newYearMemberAcceptService.save(accept);
		}
		/**
		 * 
		 * <p>
		 * 	开奖异步工作线程
		 * </p>
		 *
		 * @author zhaopeng
		 * @since 2020年1月15日
		 */
		class lotteryWork extends Thread {
			private List<NewYearCoin> coins;
			private NewYearConfig config;
			private Member member;
			private NewYearMemberInfo memberInfo;
			private long complNum;
			
			
			public lotteryWork(List<NewYearCoin> coins, NewYearConfig config, Member member, NewYearMemberInfo memberInfo,
					long complNum) {
				this.coins = coins;
				this.config = config;
				this.member = member;
				this.memberInfo = memberInfo;
				this.complNum = complNum;
			}


			@Override
			public void run() {
				//放入新bean函数开启事务
				memberDailyTaskBizService.newYearStaticsInit(coins);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				memberDailyTaskBizService.lotteryWorkMethod(coins, config, member, memberInfo, complNum , appId);
			}
		}
		
		
		ThreadPoolUtils.putThread(new lotteryWork(coins, config, member, memberInfo ,complNum)); //将开奖逻辑加入异步，提高开奖即时效率
		
		this.redisUtil.setVal(ReidsKeyGenerator.getLotteryMembers(), member.getId());//将当前用户设置为已开奖
		this.redisUtil.delKey(ReidsKeyGenerator.getLotteryLock(member.getId().toString()));//去锁
		return MessageRespResult.success("", result);//返回正常奖励应得数目
	}
	
	
	public MessageRespResult<Boolean> isOpen(Member member) {
		if(this.redisUtil.setHasVal(ReidsKeyGenerator.getLotteryMembers(), member.getId())) {
			return MessageRespResult.success("", true);
		}
		return MessageRespResult.success("", false);
	}
	
	public MessageRespResult<Integer> lotterCount() {
		if(redisUtil.keyExist(ReidsKeyGenerator.getLotteryMembers())) {
			return MessageRespResult.success("", this.redisUtil.setLength(ReidsKeyGenerator.getLotteryMembers()));
		}
		return MessageRespResult.success("", 0);
	}
	
	private SimpleDateFormat sdf_ymd = new SimpleDateFormat("yyyyMMdd");

	@Async
	public void timeFree() {
		//凌晨
		Calendar calenaar = Calendar.getInstance();
		calenaar.add(Calendar.DAY_OF_MONTH, -1);
		
		while(true) { //释放时间，执行全部待释放
			Object text =  null;
			try {
				text =redisUtil.rGet(ReidsKeyGenerator.getrecordCheckList(sdf_ymd.format(calenaar.getTime())));
			} catch (Exception e) {
				break;//key 不存在
			}
			
			try {
				if(text == null || StringUtils.isBlank(text.toString())) {
					break;//释放完成
				}
				RecordCheckPojo rcp = JSON.parseObject(text.toString() , RecordCheckPojo.class);
				if(rcp.getBalance().compareTo(BigDecimal.ZERO) == 0 || rcp.getBalance().compareTo(BigDecimal.ZERO) == -1) {
					continue;
				}
				log.info("释放锁仓--->" + rcp.getCoinUnit()  +" " + rcp.getMemberId() + " " + rcp.getBalance().toString()) ;
				memberDailyTaskBizService.checkStart(rcp);
			} catch (Exception e) {
				log.error("释放锁仓业务执行异常["+text.toString()+"]" , e);
			}
		}
	}
}
