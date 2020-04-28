package com.spark.bitrade.biz;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import com.spark.bitrade.common.NewYearExceptionMsg;
import com.spark.bitrade.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.spark.bitrade.common.RedisUtil;
import com.spark.bitrade.common.ReidsKeyGenerator;
import com.spark.bitrade.common.customer.EventConsumer;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.service.IMemberBenefitsService;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.service.ISilkDataDistApiService;
import com.spark.bitrade.service.NewYearAcceptReleaseRecordService;
import com.spark.bitrade.service.NewYearCoinService;
import com.spark.bitrade.service.NewYearDailyTaskService;
import com.spark.bitrade.service.NewYearMemberAcceptService;
import com.spark.bitrade.service.NewYearMemberInfoService;
import com.spark.bitrade.service.NewYearStaticsService;
import com.spark.bitrade.trans.TradeTccCancelEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.MemberTaskVo;

import lombok.Data;

/**
 * 
 * <p>
 * 	用户当日任务功能逻辑
 * </p>
 *
 * @author zhaopeng
 * @since 2020年1月7日
 */
@Service
public class MemberDailyTaskBizService {

    @Autowired
    private NewYearDailyTaskService dailyTaskService;
    @Resource
    private NewYearMemberInfoService newYearMemberInfoService;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private Log logger = LogFactory.getLog(this.getClass());
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private NewYearMemberAcceptService newYearMemberAcceptService ;
    @Resource
    private NewYearStaticsService newYearStaticsService;
    @Resource
    private NewYearCoinService newYearCoinService;
    @Resource
	private ISilkDataDistApiService silkDataDistApiService;
    @Resource
    private IMemberWalletApiService memberWalletApiService;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    private NewYearAcceptReleaseRecordService newYearAcceptReleaseRecordService;
    @Resource
    private IMemberBenefitsService iMemberBenefitsService;
    
    private final long CACHE_SAVE_TIME = 50 * 24 * 60 * 60;//50天
    
    /**
     * 新增完成任务
     * @param key
     * @param memberId
     * @param only
     * @return
     * @author zhaopeng
     * @since 2020年1月8日
     */
    @Transactional
    public void addMemberTask(String key , Long memberId ,String messageId, boolean only , boolean lock , boolean ...checkMessage) {
    	NewYearDailyTask nydt = new NewYearDailyTask();
    	nydt.setMemberId(memberId);
    	nydt.setTaskDateStr(sdf.format(new Date()));
    	nydt.setTaskKey(key);
    	nydt.setMessageId(messageId);
    	if(only) {
    		if(checkMessage != null && checkMessage.length > 0 && checkMessage[0]) {
    			if(this.dailyTaskService.count(new QueryWrapper<NewYearDailyTask>().or(wrapper ->wrapper.eq("member_id", memberId).eq("task_key", key).eq("task_date_str", nydt.getTaskDateStr()))) > 0 ) {
        			return;
        		}
    		}
    		else {
    			if(this.dailyTaskService.count(new QueryWrapper<NewYearDailyTask>().or(wrapper ->wrapper.eq("member_id", memberId).eq("task_key", key).eq("task_date_str", nydt.getTaskDateStr())).or().eq("message_id", messageId)) > 0 ) {
        			return;
        		}
    		}
    	}
    	if(lock) { //锁
    		if(this.redisUtil.incrementLock(ReidsKeyGenerator.taskStatusLock(key, memberId.toString()), 3) > 1) return;
    	}
    	if(this.newYearMemberInfoService.findRecordByMemberId(memberId) == null) {
    		logger.info("账户 -" + memberId + " 未登录过活动页面，初始化....");
            if(this.redisUtil.incrementLock(ReidsKeyGenerator.taskMemberInfoCreateLock(memberId.toString()), 3) <=1) { //初始化信息锁
            	NewYearMemberInfo info = new NewYearMemberInfo();
            	info.setMemberId(memberId);
            	info.setDigTimes(0);
            	info.setCreateTime(new Date());
            	this.newYearMemberInfoService.save(info);
            }
            else {
            	while(this.redisUtil.keyExist(ReidsKeyGenerator.taskMemberInfoCreateLock(memberId.toString()))) {}
            }
    	}

		logger.info("获得挖矿次数--member -" + memberId + "  key:" + key);
    	//挖矿次数加一
    	this.newYearMemberInfoService.incrMemberMiningNumber(memberId);
    	//任务记录
    	this.dailyTaskService.save(nydt);
    	//总次数+1
    	String cacheKey = getKeyOfCacheName(key, memberId.toString());
    	if(StringUtils.isNotBlank(cacheKey)) {
    		this.redisUtil.incrementLock(cacheKey, CACHE_SAVE_TIME);
    	}
    }

    private String getKeyOfCacheName(String key , String memberId) {
    	switch (key) {
	        case EventConsumer.TASK_REGIST_STATUS:
	            return ReidsKeyGenerator.getTaskRegistStatus(memberId);
	        case EventConsumer.TASK_EXCHANGE_STATUS:
	            return ReidsKeyGenerator.getTaskExchangeStatus(memberId);
	        case EventConsumer.TASK_LOGIN_STATUS:
	            return ReidsKeyGenerator.getTaskLoginStatus(memberId);
	        case EventConsumer.TASK_OTC_STATUS:
	           return ReidsKeyGenerator.getTaskOtcStatus(memberId);
	        case EventConsumer.TASK_PUT_STATUS:
	            return ReidsKeyGenerator.getTaskPutStatus(memberId);
	        case EventConsumer.TASK_RECHARGE_STATUS:
	            return ReidsKeyGenerator.getTaskRechargeStatus(memberId);
	    }
    	return null;
    }
    
    /**
     * 获取用户每日任务
     *
     * @param  memberId
     * @return
     */
    public List<MemberTaskVo> findMemberDailyTask(Long memberId) {
    	List<NewYearDailyTask> tasks = dailyTaskService.list(new QueryWrapper<NewYearDailyTask>().eq("member_id", memberId).eq("task_date_str", sdf.format(new Date())));
        if(CollectionUtils.isEmpty(tasks)) {
        	tasks = new ArrayList<NewYearDailyTask>();
        }
        List<MemberTaskVo> vos = new ArrayList<MemberTaskVo>();
        for(String key : EventConsumer.TASK_LIST) {
        	boolean find = false;
        	for(NewYearDailyTask task : tasks) {
        		if(task.getTaskKey().equals(key)) {
        			find = true;
        			break;
        		}
        	}
        	if(find) {
        		vos.add(new MemberTaskVo(key, 1, redisUtil.memberTaskCount(key, memberId.toString())));
        	}
        	else {
        		vos.add(new MemberTaskVo(key, 0, redisUtil.memberTaskCount(key, memberId.toString())));
        	}
        }
    	return vos;
    }
    
    @Transactional
    public void newYearStaticsInit(List<NewYearCoin> coins) {
    	String date = sdf.format(new Date());
    	//将初始化操作与更新操作分离
		coins.stream().forEach(coin ->{
    		//添加总奖金记录NewYearStatics 需要判断当天是否对改币种生成了总量记录，如果没有，需要加锁初始化，如果有，直接修改
			if(this.newYearStaticsService.count(new QueryWrapper<NewYearStatics>().eq("coin_unit", coin.getCoinUnit()).eq("collect_date", date)) < 1) { //初始化表
				//多任务开启时锁定一个县城执行初始化操作，其余线程进入下一个初始化操作序列
				if(this.redisUtil.incrementLock(ReidsKeyGenerator.getInitStaticLock(coin.getCoinUnit()), 3) <= 1) {//初始化锁 
					NewYearStatics newStatics = new NewYearStatics();
					newStatics.setCollectDate(date);
					newStatics.setCoinUnit(coin.getCoinUnit());
					newStatics.setSendAmount(new BigDecimal(0));
					newStatics.setLockAmount(new BigDecimal(0));
					newStatics.setReleasedAmount(new BigDecimal(0));
					newStatics.setCreateTime(new Date());
					this.newYearStaticsService.save(newStatics);
					this.redisUtil.delKey(ReidsKeyGenerator.getInitStaticLock(coin.getCoinUnit()));//去锁
				}
			}
		});
    }
    
    /**
     * 开奖后异步工作线程调用开奖事务逻辑
     * @param coins
     * @param config
     * @param member
     * @param memberInfo
     * @param newYearMemberAcceptService
     * @param complNum
     * @author zhaopeng
     * @since 2020年1月15日
     */
    
    @Transactional
	public void lotteryWorkMethod(List<NewYearCoin> coins, NewYearConfig config, Member member, NewYearMemberInfo memberInfo,
			long complNum , Integer appId) {
		String date = sdf.format(new Date());
    	
		try {
			MessageRespResult<SilkDataDist> silk = silkDataDistApiService.findOne("NEW_YEAR_CONFIG", "TOTAL_ACCOUNT");
	        if(!silk.isSuccess() || silk.getData() == null) {
	        	throw new RuntimeException("奖励公共账户获取失败");
	        }
			
			List<SendMoney> sends = new ArrayList<SendMoney>();
			//添加个人奖金领奖记录 NewYearMemberAccept
			coins.stream().forEach(coin ->{
				BigDecimal allCount = coin.getWardAmount().divide(new BigDecimal(complNum), 8 ,RoundingMode.DOWN);//当前币种应获取赠送总额
				BigDecimal lock = allCount.multiply(coin.getLockPercent()).setScale(8, RoundingMode.DOWN);//锁仓数量
				BigDecimal send = allCount.subtract(lock);//即时发放数量
				BigDecimal everyDayMax = lock.multiply(config.getEverydaysMaxRelease());//每日最大释放
				NewYearMemberAccept accept = new NewYearMemberAccept();
				accept.setMemberId(member.getId());
				accept.setToken(memberInfo.getToken());
				accept.setCoinUnit(coin.getCoinUnit());
				accept.setSendedAmount(send);
				accept.setLockAmount(lock);
				accept.setReleasedAmount(new BigDecimal(0));
				accept.setEveryMaxReleasedAmount(everyDayMax);
				accept.setTotalAmount(allCount);
				accept.setCreateTime(new Date());
				this.newYearMemberAcceptService.save(accept);
				sends.add(new SendMoney(coin.getCoinUnit(), allCount, lock));
				this.newYearStaticsService.addSendAndLock(send, lock, coin.getCoinUnit(), date);//总奖金记录发放和锁仓数量更新
				this.newYearCoinService.addSendCost(coin.getId(), allCount); //奖励币种更新已发放数量
			});

	        Long totalId = Long.valueOf(silk.getData().getDictVal());

	        for(SendMoney sm : sends) {
	        	//主账号流水单号集合  和 用户账号流水单号集合传入的作用是用来记录，因为有多个币种， 如果一个交易产生错误，需要把所有的全部回滚
				sendCost( member.getId(), totalId, sm.getNum(), sm.getLock() ,sm.getUnti(), memberInfo.getToken());
	        }
		} catch (Exception e) { //记录日志，然后再回滚
			logger.error("年终活动开奖奖励发放异常，异常用户["+member.getIdNumber()+"]" ,e);
			throw new RuntimeException(e);
		}
	}
    
    /**
     * 回滚当前发放全部奖励
     * @param memberId
     * @param baseId 主账户id
     * @param base 主账户产生的交易流水号集合
     * @param member 用户账户产生的交易流水号集合
     * @author zhaopeng
     * @since 2020年1月15日
     */
    private void sendCanal(Long memberId , Long baseId , List<Long> base , List<Long> member ,List<Long> lock) {
    	for(int i = 0 ; i < base.size() ; i++) {
    		try {
                MessageRespResult<Boolean> resultCancel = memberWalletApiService.tradeTccCancel(memberId, member.get(i));
                MessageRespResult<Boolean> memberCancel = memberWalletApiService.tradeTccCancel(baseId, base.get(i));
                MessageRespResult<Boolean> lockCancel = memberWalletApiService.tradeTccCancel(memberId, lock.get(i));
                ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultCancel);
                ExceptionUitl.throwsMessageCodeExceptionIfFailed(memberCancel);
                ExceptionUitl.throwsMessageCodeExceptionIfFailed(lockCancel);
                AssertUtil.isTrue(resultCancel.getData(), CommonMsgCode.ERROR);
                AssertUtil.isTrue(memberCancel.getData(), CommonMsgCode.ERROR);
                AssertUtil.isTrue(lockCancel.getData(), CommonMsgCode.ERROR);
            } catch (Exception ex) {
            	try {
            		kafkaTemplate.send("acct-trade-tcc-cancel", "tcc-cancel",
                            JSON.toJSONString(new TradeTccCancelEntity(memberId, member.get(i))));
                    kafkaTemplate.send("acct-trade-tcc-cancel", "tcc-cancel",
                            JSON.toJSONString(new TradeTccCancelEntity(baseId, base.get(i))));
					kafkaTemplate.send("acct-trade-tcc-cancel", "tcc-cancel",
					        JSON.toJSONString(new TradeTccCancelEntity(memberId, lock.get(i))));
    			} catch (Exception e) {
    				logger.error("年终活动资金发放异常，发送kafka取消支付消息失败" ,e);
    			}
            }
    	}
    }
    
    /**
     * 资金划转确认
     * @param memberId
     * @param totalId 主账号
     * @param pay 应发放的全额数量
     * @param unit
     * @param lock 应锁仓数量
     * @param token
     * @return
     * @author zhaopeng
     * @since 2020年1月15日
     */
    private boolean sendCost( Long memberId ,
							 Long totalId , BigDecimal pay , BigDecimal lock ,String unit , String token) {
    	//获取总账户钱包
		MessageRespResult<MemberWallet> tw = memberWalletApiService.getWalletByUnit(totalId, unit);
		//获取用户钱包
		MessageRespResult<MemberWallet> mw = memberWalletApiService.getWalletByUnit(memberId, unit);
		AssertUtil.isTrue(mw.isSuccess()&&tw.isSuccess(),CommonMsgCode.SERVICE_UNAVAILABLE);
		//总账户扣除
		MemberWallet tmw = tw.getData();
		Integer totalTrade = newYearCoinService.trade(tmw.getId(), BigDecimal.ZERO,pay.negate(), BigDecimal.ZERO);
		//流水
		Date date = new Date();
		long l = System.currentTimeMillis();
		MemberTransaction apT=new MemberTransaction();
		apT.setAmount(pay.negate());
		apT.setCreateTime(date);
		apT.setMemberId(totalId);
		apT.setSymbol(unit);
		apT.setType(TransactionType.ACTIVITY_AWARD);
		apT.setRefId(token);
		apT.setComment("年终活动扣除");
		apT.setFlag(0);
		apT.setFee(BigDecimal.ZERO);
		newYearCoinService.addTransaction(apT);
		AssertUtil.isTrue(totalTrade>0,NewYearExceptionMsg.ACCOUNT_FROZEN_BALANCE_INSUFFICIENT);


		//用户增加全额奖励
		MemberWallet memberWallet = mw.getData();
		MemberTransaction send=new MemberTransaction();
		date.setTime(l+1000);
		send.setAmount(pay);
		send.setCreateTime(date);
		send.setMemberId(memberId);
		send.setSymbol(unit);
		send.setType(TransactionType.ACTIVITY_AWARD);
		send.setRefId(token);
		send.setComment("年终活动");
		send.setFlag(0);
		send.setFee(BigDecimal.ZERO);
		Integer sendBoolean = newYearCoinService.trade(memberWallet.getId(), pay, BigDecimal.ZERO, BigDecimal.ZERO);
		newYearCoinService.addTransaction(send);
		AssertUtil.isTrue(sendBoolean>0,CommonMsgCode.ERROR);


		//用户锁仓
		date.setTime(l+2000);
		MemberTransaction lockM=new MemberTransaction();
		lockM.setAmount(lock.negate());
		lockM.setCreateTime(date);
		lockM.setMemberId(memberId);
		lockM.setSymbol(unit);
		lockM.setType(TransactionType.FESTIVAL_NUMBER_LOCK);
		lockM.setRefId(token);
		lockM.setComment("年终活动锁仓");
		lockM.setFlag(0);
		lockM.setFee(BigDecimal.ZERO);
		Integer lockBoolean = newYearCoinService.trade(memberWallet.getId(), lock.negate(), BigDecimal.ZERO, lock);
		newYearCoinService.addTransaction(lockM);

		AssertUtil.isTrue(lockBoolean>0,NewYearExceptionMsg.ACCOUNT_LOCK_BALANCE_INSUFFICIENT);

		return true;
    }
    
    /**
     * 
     * <p>
     * 	发放记录暂存
     *  统一处理
     * </p>
     *
     * @author Administrator
     * @since 2020年1月15日
     */
    @Data
    class SendMoney {
		private String unti;//币种
		private BigDecimal num;//发放总数
		private BigDecimal lock;//锁仓
		
		public SendMoney(String unit , BigDecimal num , BigDecimal lock) {
			this.num = num;
			this.unti = unit;
			this.lock = lock;
		}
	}
    
    /**
     * 交易释放锁仓
     * @param unit
     * @param memberId
     * @param balance
     * @author zhaopeng
     * @since 2020年2月3日
     */
    public void recordCheck(String unit , Long memberId , BigDecimal balance) {
    	//判断当前用户是否有释放条件
    	NewYearMemberAccept accept = this.newYearMemberAcceptService.getOne(new QueryWrapper<NewYearMemberAccept>().eq("member_id", memberId).eq("coin_unit", unit));
    	if(accept == null || (accept.getLockAmount().compareTo(accept.getReleasedAmount()) == 0)) return;//释放完成
    	logger.info("进入释放缓存---->"+memberId+"  "+unit+"  "+balance.toString());
    	String times = sdf.format(new Date());
    	this.redisUtil.lSet(ReidsKeyGenerator.getrecordCheckList(times), JSON.toJSONString(new RecordCheckPojo(unit, memberId, balance , times)));
    	
    }
	
    @Transactional
    public void checkStart(RecordCheckPojo pojo) {
    	//尝试建立当日领奖统计数据
    	this.newYearStaticsInit(this.newYearCoinService.list());
    	
    	if((redisUtil.keyExist(ReidsKeyGenerator.getMemberUnitNum(pojo.getMemberId().toString() , pojo.getCoinUnit())) //存在当日释放剩余额度 
	    		&& new BigDecimal(redisUtil.get(ReidsKeyGenerator.getMemberUnitNum(pojo.getMemberId().toString() , pojo.getCoinUnit())).toString()).compareTo(BigDecimal.ZERO)	== 1)//当日释放额度剩余大于0
	    		|| !redisUtil.keyExist(ReidsKeyGenerator.getMemberUnitNum(pojo.getMemberId().toString() , pojo.getCoinUnit())) //当日还没有设置剩余额度
	    			) { 
	    		//加锁
	    		if(redisUtil.keyExist(ReidsKeyGenerator.getRecordCheckLock(pojo.getMemberId().toString(), pojo.getCoinUnit()))) {
	    			this.redisUtil.lSet(ReidsKeyGenerator.getrecordCheckList(pojo.getTimes()), JSON.toJSONString(pojo)); 
	    			return;
	    		}
	    		if(redisUtil.incrementLock(ReidsKeyGenerator.getRecordCheckLock(pojo.getMemberId().toString(), pojo.getCoinUnit()), 3) > 1) {
	    			this.redisUtil.lSet(ReidsKeyGenerator.getrecordCheckList(pojo.getTimes()), JSON.toJSONString(pojo)); //出现并发时，将当前执行的内容扔到队尾，下一次执行
	    			return;
	    		}
	    		if(!redisUtil.keyExist(ReidsKeyGenerator.getMemberUnitNum(pojo.getMemberId().toString() , pojo.getCoinUnit()))) { //如果没有设置当日释放剩余额度
	    			//剩余未释放总额
	    			NewYearMemberAccept accept = this.newYearMemberAcceptService.getOne(new QueryWrapper<NewYearMemberAccept>().eq("member_id", pojo.getMemberId()).eq("coin_unit", pojo.getCoinUnit()));
	    			BigDecimal less = accept.getLockAmount().subtract(accept.getReleasedAmount());
	    			//当日可释放最大
	    			BigDecimal thisDayMax = accept.getEveryMaxReleasedAmount().compareTo(less) == -1 ? accept.getEveryMaxReleasedAmount() : less;
	    			this.redisUtil.set(ReidsKeyGenerator.getMemberUnitNum(pojo.getMemberId().toString() , pojo.getCoinUnit()) , thisDayMax.toString() , 25 * 60 * 60);
	    			if(thisDayMax.compareTo(BigDecimal.ZERO) == 0) return;
	    		}
	
				//交易后应释放数量
				NewYearCoin coin = this.newYearCoinService.getOne(new QueryWrapper<NewYearCoin>().eq("coin_unit", pojo.getCoinUnit()));
				if(coin == null) return;
				BigDecimal lessNum = pojo.getBalance().multiply(coin.getEveryReleasePercent());
				//当日可释放
				BigDecimal thisDayMax = new BigDecimal(this.redisUtil.get(ReidsKeyGenerator.getMemberUnitNum(pojo.getMemberId().toString() , pojo.getCoinUnit())).toString());
				if(thisDayMax.compareTo(BigDecimal.ZERO) == 0) return;//为0不处理
				
				//本次实际释放
				BigDecimal canLess = lessNum.compareTo(thisDayMax) == -1 ? lessNum : thisDayMax;
				//释放金额并修改数据库已释放信息，添加释放记录
				//NewYearAcceptReleaseRecord
				NewYearAcceptReleaseRecord nyarr = new NewYearAcceptReleaseRecord();
				nyarr.setMemberId(pojo.getMemberId());
				nyarr.setToken(this.newYearMemberInfoService.findRecordByMemberId(pojo.getMemberId()).getToken());
				nyarr.setAcceptAmount(canLess);
				nyarr.setCoinUnit(pojo.getCoinUnit());
				nyarr.setType(1);
				nyarr.setCreateTime(new Date());
				this.newYearAcceptReleaseRecordService.save(nyarr);
			
				//NewYearMemberAccept
				NewYearMemberAccept accept = this.newYearMemberAcceptService.getOne(new QueryWrapper<NewYearMemberAccept>().eq("member_id", pojo.getMemberId()).eq("coin_unit", pojo.getCoinUnit()));
				accept.setReleasedAmount(accept.getReleasedAmount().add(canLess));
				accept.setUpdateTime(new Date());
				this.newYearMemberAcceptService.updateById(accept);
				
				//NewYearStatics
				this.newYearStaticsService.addReleased(canLess, pojo.getCoinUnit(), sdf.format(new Date()));
				
				//释放金额
				WalletTradeEntity released = new WalletTradeEntity(); 
				released.setType(TransactionType.FESTIVAL_NUMBER_LOCK_RELEASED);
				released.setRefId(nyarr.getToken());
				released.setMemberId(pojo.getMemberId());
				released.setCoinUnit(pojo.getCoinUnit());
				released.setTradeBalance(canLess); //发放到可用
				released.setTradeLockBalance(canLess.negate());//移除锁仓
				released.setComment("年终活动令牌奖励释放");
		        MessageRespResult<WalletChangeRecord> allSendResult = memberWalletApiService.tradeTccTry(released);
		        if(allSendResult.isSuccess()) {
		        	MessageRespResult<Boolean> memberConfirm = memberWalletApiService.tradeTccConfirm(released.getMemberId(), allSendResult.getData().getId());
		        	if(memberConfirm.isSuccess() && memberConfirm.getData()) {
						//修改redis中用户指定币种当日可释放金额
		        		this.redisUtil.set(ReidsKeyGenerator.getMemberUnitNum(pojo.getMemberId().toString() , pojo.getCoinUnit()) , thisDayMax.subtract(canLess).toString() , 25 * 60 * 60);
		        		return ;
		        	}
		        }
		        //异常回滚
		        this.redisUtil.lSet(ReidsKeyGenerator.getrecordCheckList(pojo.getTimes()), JSON.toJSONString(pojo));
		        throw new RuntimeException("年终奖励锁仓资金释放异常" + "["+JSON.toJSONString(pojo)+"]");
    	}
    }
    
	@Data
	public static class RecordCheckPojo { //个人指定币种交易可能存在多次，在队列执行时可能存在指定币种并发，而不可以直接使用缓存锁来处理，将待处理信息放入redis集合，单个处理
		private String coinUnit;
		private Long memberId;
		private BigDecimal balance;
		private String times;
		
		public RecordCheckPojo() {}
		public RecordCheckPojo(String coinUnit , Long memberId , BigDecimal balance , String times) {
			this.coinUnit = coinUnit;
			this.memberId = memberId;
			this.balance = balance;
			this.times = times;
		}
	}


}
