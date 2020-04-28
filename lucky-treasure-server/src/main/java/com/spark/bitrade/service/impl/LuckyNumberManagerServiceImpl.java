package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.biz.LuckPushService;
import com.spark.bitrade.common.LuckyGameRedisUtil;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.controller.param.ListBullParam;
import com.spark.bitrade.controller.param.ListLuckyNumberParam;
import com.spark.bitrade.controller.vo.FxhApiDate;
import com.spark.bitrade.controller.vo.LuckyNumberListVo;
import com.spark.bitrade.controller.vo.LuckyRunBullListVo;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.enums.LuckyErrorCode;
import com.spark.bitrade.enums.NumberStatusEnum;
import com.spark.bitrade.enums.SettleStatus;
import com.spark.bitrade.mapper.LuckyJoinInfoMapper;
import com.spark.bitrade.mapper.LuckyManageCoinMapper;
import com.spark.bitrade.mapper.LuckyNumberManagerMapper;
import com.spark.bitrade.service.*;
import com.spark.bitrade.trans.TradeTccCancelEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 欢乐幸运号活动信息表 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-16
 */
@Service
@Slf4j
public class LuckyNumberManagerServiceImpl extends ServiceImpl<LuckyNumberManagerMapper, LuckyNumberManager> implements LuckyNumberManagerService {

    @Resource
    private LuckyNumberManagerMapper luckyNumberManagerMapper;
    @Resource
    private LuckyJoinInfoMapper luckyJoinInfoMapper;
    @Resource
    private LuckyGameRedisUtil luckyGameRedisUtil;
    @Resource
    private LuckyManageCoinMapper luckyManageCoinMapper;
	@Autowired
    private IMemberWalletApiService memberWalletApiService;
	@Autowired
	private ISilkDataDistApiService silkDataDistApiService;
//	@Autowired
//	private TickerApiHttpClient tickerApiHttpClient;
	@Resource
    private KafkaTemplate<String, String> kafkaTemplate;
	@Resource
	private LuckyJoinInfoService luckyJoinInfoService;
	@Autowired
	private LuckPushService luckPushService;
	/**
	 * 幸运号活动列表
	 * 包含指定条件下的参与人数、票池总数、当前参与人信息
		 * 
		 * @param param
		 * @return
		 * @author zhaopeng
		 * @since 2019年12月17日
	 */
	@Override
	public MessageRespResult<IPage<LuckyNumberListVo>> numberGameList(ListLuckyNumberParam param) {
		 QueryWrapper<LuckyNumberManager> queryWrapper = new QueryWrapper<LuckyNumberManager>();
		 Page<LuckyNumberManager> orderPage = new Page<>(param.getPage(), param.getPageSize());
		 queryWrapper.eq("act_type", 0); //幸运号
		 queryWrapper.eq("delete_state", 0); //正常状态
		 queryWrapper.eq("hidden", 0);
		 if(param.getMemberId() != null && param.getMemberId() != null && StringUtils.isNotBlank(param.getOnlyMine()) && !param.getOnlyMine().equals("0")) {
			 //获取用户参与的活动列表
			 Set<String> gameIds = luckyGameRedisUtil.getSetKey(String.format(LuckyGameRedisUtil.ACTIVITY_RECORD_ALL_QUERY, param.getMemberId().toString()));
			 if(CollectionUtils.isEmpty(gameIds)) { //没有参与记录
				 return MessageRespResult.success4Data(new Page<LuckyNumberListVo>(param.getPage(), param.getPageSize()));
			 }
			queryWrapper.in("id", gameIds);
		 }
		 if(StringUtils.isNotBlank(param.getCoinUnit())) {
			 queryWrapper.eq("unit", param.getCoinUnit());
		 }
		 if(param.getStatus() != null && StringUtils.isNotBlank(param.getStatus().getCnName())) {
			 if(param.getStatus().getCnName().equals(NumberStatusEnum.NOT_START.getCnName())) {
				 orderPage.setAsc("lucky_time");
				 queryWrapper.gt("start_time", new Date());
			 }
			 else if(param.getStatus().getCnName().equals(NumberStatusEnum.HAVE_BEEN.getCnName())) {
				 orderPage.setAsc("lucky_time");
				 queryWrapper.lt("start_time", new Date());
				 queryWrapper.gt("lucky_time", new Date());
			 }
			 else if(param.getStatus().getCnName().equals(NumberStatusEnum.END.getCnName())) {
				 orderPage.setDesc("lucky_time");
				 queryWrapper.lt("lucky_time", new Date());
			 }
		 }
		 if(StringUtils.isNotBlank(param.getStartTime())) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				queryWrapper.gt("lucky_time", sdf.parse(param.getStartTime()));
			} catch (ParseException e) {
				log.error("幸运欢乐号列表查询时间格式转换错误 [" + param.getStartTime() + "]" );
			}
		 }
		 if(StringUtils.isNotBlank(param.getEndTime())) {
			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					queryWrapper.lt("lucky_time", sdf.parse(param.getEndTime()));
				} catch (ParseException e) {
					log.error("幸运欢乐号列表查询时间格式转换错误 [" + param.getEndTime() + "]" );
				}
		 }
	     IPage<LuckyNumberManager> luckyNumberManagerPage = this.baseMapper.selectPage(orderPage, queryWrapper);
	    if(CollectionUtils.isEmpty(luckyNumberManagerPage.getRecords())) {
	    	return MessageRespResult.success4Data(new Page<LuckyNumberListVo>(param.getPage(), param.getPageSize()));
	    }
	     List<LuckyNumberListVo> result = new ArrayList<LuckyNumberListVo>();
	     luckyNumberManagerPage.getRecords().stream().forEach(lnm->{
	    	 LuckyNumberListVo lnl = new LuckyNumberListVo(lnm);
	    	 result.add(setVoInfo(lnl, param.getMemberId()));
	     });
	     IPage<LuckyNumberListVo> voList = new Page<LuckyNumberListVo>(luckyNumberManagerPage.getCurrent() ,luckyNumberManagerPage.getSize() , luckyNumberManagerPage.getTotal() , luckyNumberManagerPage.isSearchCount());
		 voList.setRecords(result);
	     return MessageRespResult.success4Data(voList);
	}
    
	
	/**
	 * 指定幸运号活动信息
	 * 包含参与人数、票池总数、当前参与人信息
		 * 
		 * @param param
		 * @return
		 * @author zhaopeng
		 * @since 2019年12月17日
	 */
	@Override
	public MessageRespResult<LuckyNumberListVo> numberGameInfo(ListLuckyNumberParam param) {
		if(param.getGameId() == null || param.getGameId() == 0) {
			 return MessageRespResult.error("没有指定活动");
		}
		 QueryWrapper<LuckyNumberManager> queryWrapper = new QueryWrapper<LuckyNumberManager>();
		 Page<LuckyNumberManager> orderPage = new Page<>(param.getPage(), param.getPageSize());
		 queryWrapper.eq("act_type", 0); //幸运号
		 queryWrapper.eq("delete_state", 0); //正常状态
		 queryWrapper.eq("id",param.getGameId());
		 IPage<LuckyNumberManager> luckyNumberManagerPage = this.baseMapper.selectPage(orderPage, queryWrapper);
	     if(CollectionUtils.isEmpty(luckyNumberManagerPage.getRecords())) {
	    	 return MessageRespResult.error("没有指定活动");
	    }
	     LuckyNumberListVo lnl = new LuckyNumberListVo(luckyNumberManagerPage.getRecords().get(0));
    	if(param.getMemberId() != null && param.getMemberId() != 0) {
    		lnl = setVoInfo(lnl, param.getMemberId());
    		this.luckyJoinInfoService.readAck(param.getMemberId(), param.getGameId().toString(), 1);
    	}
    	
		return MessageRespResult.success("查询成功", lnl);
	}

	/**
	 * 附加信息
	 * @param vo
	 * @param memberId
	 * @return
	 * @author zhaopeng
	 * @since 2019年12月16日
	 */
	private LuckyNumberListVo setVoInfo(LuckyNumberListVo vo , Long memberId) {
		if(vo.getIsSettlement() == BooleanEnum.IS_FALSE && vo.getStartTime().getTime() < System.currentTimeMillis()) { //当前活动已开始且未结算，需要从缓存中取当前统计值
   		 Map<String, Integer> count = this.luckyGameRedisUtil.getJoinCount(vo.getId().toString());
   		 if(count != null) {
   			vo.setJoinMemberCount(count.get("member"));
   			vo.setJoinTicketCount(count.get("ticket"));
   		 }
   	 }
   	 if(memberId != null && memberId != 0) { //本人参与
   		vo.setJoins(this.luckyJoinInfoMapper.selectInfoByMemberAndGameId(vo.getId(), memberId));
   		List<String> winTick = new ArrayList<String>();
   		BigDecimal winMoney = new BigDecimal(0);
   		BigDecimal winAppendMoney = new BigDecimal(0);
   		BigDecimal appendWx = new BigDecimal(0);
   		if(!CollectionUtils.isEmpty(vo.getJoins())) {
	   		for(int i = 0 ; i < vo.getJoins().size();i++) {
	   			if(vo.getJoins().get(i).getWin() == BooleanEnum.IS_TRUE) {
	   				winTick.add(vo.getJoins().get(i).getJoinInfo());
	   				winMoney = winMoney.add(vo.getJoins().get(i).getAwardAmount());
	   				winAppendMoney = winAppendMoney.add(vo.getJoins().get(i).getAddAwardAmount());
	   				if(vo.getJoins().get(i).getAppendWx() == BooleanEnum.IS_TRUE) {
	   					appendWx = appendWx.add(vo.getJoins().get(i).getAddAwardAmount());
	   				}
	   			}
	   		}
   		}
   		vo.setMemberWinTickets(winTick);
   		vo.setMemberWinMoney(winMoney);
   		vo.setMemberWinAppendMoney(winAppendMoney);
   		vo.setMemberAppednWx(appendWx);
   	 }
   	 else {
   		vo.setJoins(new ArrayList<LuckyJoinInfo>());
   		vo.setMemberWinTickets(new ArrayList<String>());
   		vo.setMemberWinMoney(new BigDecimal(0));
   		vo.setMemberWinAppendMoney(new BigDecimal(0));
   		vo.setMemberAppednWx(new BigDecimal(0));
   	 }
   	 return vo;
	}

	/**
	 * 幸运号活动定时任务
		 * 
		 * @return
		 * @author zhaopeng
		 * @since 2019年12月17日
	 */
    @Override
	@Async
	public MessageRespResult<String> luckyGameSchedule() {
		MessageRespResult<SilkDataDist> silk = silkDataDistApiService.findOne("LUCKY_CONFIG", "TOTAL_ACCOUNT_ID");
		AssertUtil.isTrue(silk.isSuccess()&&silk.getData()!=null,LuckyErrorCode.RECEIVE_ACCOUNT_NOT_FOUND);
		Long totalId=Long.valueOf(silk.getData().getDictVal());

    	QueryWrapper<LuckyNumberManager> queryWrapper = new QueryWrapper<LuckyNumberManager>();
    	queryWrapper.eq("is_settlement", BooleanEnum.IS_FALSE);
    	//未结算信息
		List<LuckyNumberManager> list = this.baseMapper.selectList(queryWrapper);
		if(!CollectionUtils.isEmpty(list)) {
			list.stream().forEach(lnm -> {
				System.out.println(lnm == null);
				if(lnm.getActType() == 0) { //欢乐幸运号
					try {
						getService().lunckNumberGameSchedule(lnm);
					} catch (Exception e) {
						log.error("欢乐幸运号定时任务异常 , id ->["+lnm.getId().toString()+"]" , e);
					}
				}
				else if(lnm.getActType() == 1) { //牛
					try {
						getService().luckyBullGameSchedule(lnm,totalId);
					}catch (Exception e){
						log.error("小牛快跑定时任务异常 , id ->["+lnm.getId().toString()+"]" , e);
					}
				}
			});
		}
		return MessageRespResult.success("处理完成");
	}

    /**
     * 幸运号活动定时处理任务
     * @param numberManager
     * @author zhaopeng
     * @since 2019年12月17日
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public void lunckNumberGameSchedule(LuckyNumberManager numberManager) {
    	//开启任务锁
    	if(luckyGameRedisUtil.increment(String.format(LuckyGameRedisUtil.ACTIVITY_GAME_STATE_CHANGE_LOCK, numberManager.getId().toString()), LuckyGameRedisUtil.DELTA_ADD, 60) > 1) {
    		return ;
    	}

    	long thisTime = System.currentTimeMillis();
    	int gameState = 0;
    	if(numberManager.getStartTime().getTime() > thisTime) { //未开始
    		gameState = 1;
    	}
    	else if(numberManager.getLuckyTime().getTime() > thisTime) { //进行中    结束时间默认在开奖时间前10分钟，所以直接将开奖时间做为进行中状态结束
    		gameState = 2;
    	}
    	else  { //已结束
    		gameState = 3;
    	}
    	//对比缓存中的状态，并做对应处理
    	String key = String.format(LuckyGameRedisUtil.ACTIVITY_GAME_STATE, numberManager.getId().toString());
    	if(!luckyGameRedisUtil.redisKeyExist(key)) {
    		luckyGameRedisUtil.setKey(key, gameState + ""); //没有记录，创建新记录
    	}
    	int cacheState = Integer.parseInt(luckyGameRedisUtil.getKey(key));
    	if(cacheState !=  gameState || (cacheState == 3 && gameState == 3)) { //状态已变更
    		if(gameState != 3) {
    			String membersKey = String.format(LuckyGameRedisUtil.ACTIVITY_JOIN_MEMBER_COUNT, numberManager.getId().toString());
        		if(luckyGameRedisUtil.redisKeyExist(membersKey) && luckyGameRedisUtil.getSetKeySize(membersKey) > 0) { //如果当前活动有参与人
            		Set<String> members = luckyGameRedisUtil.getSetKey(membersKey);
            		members.stream().forEach(mb -> {
                		//推送状态变更数到前端
            			luckyJoinInfoService.actChangeSend(numberManager.getId().toString(), numberManager.getId().toString(), false);
            		});
        		}
    		}
    		else { //如果当前状态为结束
        		if(luckyGameRedisUtil.redisKeyExist(String.format(LuckyGameRedisUtil.ACTIVITY_JOIN_TICKET_COUNT, numberManager.getId().toString()))) { //活动有参与人

        			//参与总人数
        			int joinMemberCount = luckyGameRedisUtil.getSetKey(String.format(LuckyGameRedisUtil.ACTIVITY_JOIN_MEMBER_COUNT, numberManager.getId().toString())).size();
        			//购买总票数
        			int payTicket = Integer.parseInt(luckyGameRedisUtil.getKey(String.format(LuckyGameRedisUtil.ACTIVITY_JOIN_TICKET_COUNT, numberManager.getId().toString())));
        			//票池总金额
        			BigDecimal payMoneyCount = numberManager.getAmount().multiply(new BigDecimal(payTicket));
            			if(Integer.parseInt(
        						luckyGameRedisUtil.getKey(
        								String.format(LuckyGameRedisUtil.ACTIVITY_JOIN_TICKET_COUNT, numberManager.getId().toString())
        								)) >= numberManager.getMinTicketNum()) { //活动结算满足开奖要求

            				//随机获取中奖票号
                			Set<String> winTicket = luckyGameRedisUtil.getLuckyNumberWinTicket(numberManager.getId().toString(), numberManager.getWinNum());
                			//获得票号集合
                			QueryWrapper<LuckyJoinInfo> queryWrapper = new QueryWrapper<LuckyJoinInfo>();
                			queryWrapper.eq("num_id", numberManager.getId())
                			.in("join_info", winTicket);
                			List<LuckyJoinInfo> winners = luckyJoinInfoMapper.selectList(queryWrapper);
                			//获奖人数
                			int winnerSize =winners .stream().collect(Collectors.groupingBy(LuckyJoinInfo::getMemberId,Collectors.counting())).keySet().size();
                			
                			//平台佣金
            				BigDecimal serverGet = numberManager.getServiceCharge().multiply(payMoneyCount);
                			//票池剩余总金额
                			payMoneyCount = payMoneyCount.subtract(serverGet);
                			//平均票奖金 0.0001
                			BigDecimal avgWinMoney = payMoneyCount.divide(new BigDecimal(winners.size()), 4, RoundingMode.HALF_DOWN);
                			//即发奖金
                			BigDecimal nowSendMoney = avgWinMoney.divide(new BigDecimal(10)).multiply(new BigDecimal(9));
                			//分享后发放奖金
                			BigDecimal appendSendMoney = avgWinMoney.divide(new BigDecimal(10));
                			//更新奖票记录
                			this.capitalFlow(winners, nowSendMoney, numberManager.getUnit(), appendSendMoney, "幸运号中奖即时发放奖金", BooleanEnum.IS_TRUE  , new BigDecimal(0));

                			numberManager.setPlatformProfit(serverGet);
                			numberManager.setJoinMemberCount(joinMemberCount);
                			numberManager.setJoinMemberAmount(payMoneyCount.add(serverGet));
                			numberManager.setJoinTicketCount(payTicket);
                			numberManager.setWinTickets(String.join(",", winTicket));
                			numberManager.setWinMemberCount(winnerSize);
                			numberManager.setWinNum(winTicket.size());
            			}
            			else { //活动结算不满足开奖要求
                			numberManager.setPlatformProfit(new BigDecimal(0));
                			numberManager.setJoinMemberCount(joinMemberCount);
                			numberManager.setJoinMemberAmount(payMoneyCount);
                			numberManager.setJoinTicketCount(payTicket);
                			numberManager.setWinTickets("");
                			numberManager.setWinMemberCount(0);
                			numberManager.setWinNum(0);

                			// 返回资金
                			QueryWrapper<LuckyJoinInfo> allWrapper = new QueryWrapper<LuckyJoinInfo>();
                			allWrapper.eq("num_id", numberManager.getId());
                			List<LuckyJoinInfo> all = luckyJoinInfoMapper.selectList(allWrapper);
                			this.capitalFlow(all, new BigDecimal(0), numberManager.getUnit(), new BigDecimal(0), "幸运号返还资金", BooleanEnum.IS_FALSE , numberManager.getAmount());
            			}
        		}
        		else { //活动没有参与人
        			numberManager.setPlatformProfit(new BigDecimal(0));
        			numberManager.setJoinMemberCount(0);
        			numberManager.setJoinMemberAmount(new BigDecimal(0));
        			numberManager.setJoinTicketCount(0);
        			numberManager.setWinTickets("");
        			numberManager.setWinMemberCount(0);
        			numberManager.setWinNum(0);
        		}
    			numberManager.setIsSettlement(BooleanEnum.IS_TRUE);
        		//更新活动数据到DB
    			this.baseMapper.updateById(numberManager);
        		//活动结束更新缓存
        		luckyGameRedisUtil.gameOver(numberManager.getId().toString());
        		//发送站内信
				getService().sendSilkInfo(numberManager.getId(),1,numberManager.getName());
        	}
    	}
    	//关闭任务锁
    	luckyGameRedisUtil.delKey(String.format(LuckyGameRedisUtil.ACTIVITY_GAME_STATE_CHANGE_LOCK, numberManager.getId().toString()));
    }

    /**
     * 小牛快跑 开奖
     *
     * @param manager
     * @author zhaopeng
     * @since 2019年12月17日
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public void luckyBullGameSchedule(LuckyNumberManager manager,Long totalId) {
    	//判断状态
		Date endTime = manager.getEndTime();
		Date luckyTime = manager.getLuckyTime();
		//结算
		manager.setIsSettlement(BooleanEnum.IS_TRUE);
        //查询出当前 小牛的涨幅
        Long managerId = manager.getId();
        List<LuckyManageCoin> bulls = luckyManageCoinMapper.findByActId(managerId);
		if(CollectionUtils.isEmpty(bulls)){
			return;
		}
		//参加该活动的用户
		Set<String> joinMemberIds = luckyGameRedisUtil.getSetKey(String.format(LuckyGameRedisUtil.ACTIVITY_JOIN_MEMBER_COUNT, managerId));
		Date currentDate = new Date();
		boolean isSend=false;
		for (LuckyManageCoin bull:bulls){
			//调用接口 查询小牛涨幅并更新到 表中

			BigDecimal startPrice = bull.getStartPrice();
			//赛牛开始更新涨幅 没有价格的时候会更新
			if(currentDate.after(endTime)&&currentDate.before(luckyTime)&&startPrice==null){

				BigDecimal start=getService().usdtRate(bull.getCoinUnit());
				int i = luckyManageCoinMapper.updateStartPrice(bull.getId(),  start);
				Assert.isTrue(i>0,"更新涨幅失败");
				isSend=true;
			}
			//活动结束更新涨幅
			if(startPrice==null){
				startPrice=getService().usdtRate(bull.getCoinUnit());
			}
			if(currentDate.after(luckyTime)){
				BigDecimal end=getService().usdtRate(bull.getCoinUnit());
				BigDecimal subtract = end.subtract(startPrice);
				BigDecimal in=BigDecimal.ZERO;
				if(startPrice.compareTo(BigDecimal.ZERO)!=0){
					in=subtract.divide(startPrice,4,RoundingMode.HALF_UP);
				}
				int i = luckyManageCoinMapper.updateIncrease(bull.getId(), in, end);
				Assert.isTrue(i>0,"更新涨幅失败");
			}
		}
		if (isSend){
			//赛牛开始发送消息
			joinMemberIds.forEach(j->{
				luckyJoinInfoService.actChangeSend(j,managerId.toString(),false);
			});
		}

		//未到开奖时间
		if(luckyTime.after(currentDate)){
			//未到开奖时间
			return;
		}
		bulls = luckyManageCoinMapper.findByActId(managerId);
		//活动状态 开奖..
        //排序
		sortBulls(bulls);
		//中奖小牛
		LuckyManageCoin luckyBull = bulls.get(0);
        //判断下注种类 是否超过1
		Integer coinCounts = luckyJoinInfoMapper.countCoinByActId(managerId);
		if(coinCounts<2){
			List<LuckyJoinInfo> notLucky = luckyJoinInfoMapper.findLuckyMemberByActId(managerId);
			//退回 设置参赛人数相关
			setJoinMemberCount(manager);
			//设置中奖票数
			manager.setPlatformProfit(BigDecimal.ZERO);
			manager.setWinMemberCount(0);
			manager.setRemarks("和局");
			manager.setWinTickets(luckyBull.getCoinUnit());
			int i = luckyNumberManagerMapper.updateById(manager);
			Assert.isTrue(i>0,"结算小牛快跑失败");
			notLucky.forEach(not->{
				//退款
				try {
					//如果这个小牛是中奖小牛才退款
					if(not.getJoinInfo().equals(luckyBull.getCoinUnit())){
						getService().returnTicket(not,manager);
					}
					//活动结束发送消息
					luckyJoinInfoService.actChangeSend(not.getMemberId().toString(),managerId.toString(),false);
				}catch (Exception e){
					log.info("退款失败:{}",not.getMemberId());
					not.setSettleStatus(SettleStatus.RETURN_FAILED);
					luckyJoinInfoMapper.updateById(not);
				}
			});
			if(!notLucky.isEmpty()&&notLucky.get(0).getJoinInfo().equals(luckyBull.getCoinUnit())){
				subtractTotal(manager.getUnit(),manager.getAmount().multiply(new BigDecimal(notLucky.size()).multiply(new BigDecimal(1).subtract(manager.getServiceCharge()))),
						TransactionType.LUCKY_RETURN_BULL,"小牛快跑资金返还,扣款",totalId);
			}

			luckyGameRedisUtil.gameOver(managerId.toString());
			return;
		}


		List<LuckyJoinInfo> luckys = luckyJoinInfoMapper.findLuckyMemberByActIdAndCoin(managerId, luckyBull.getCoinUnit());
		BigDecimal ward = setManageEndData(manager, luckyBull,luckys);
		int i = luckyNumberManagerMapper.updateById(manager);
		Assert.isTrue(i>0,"结算小牛快跑失败");
		if(!CollectionUtils.isEmpty(luckys)){
			//中奖用户平分的金额
			BigDecimal memberWard=ward.subtract(manager.getPlatformProfit());
			//单人奖励
			BigDecimal singleWard = memberWard.divide(new BigDecimal(manager.getWinNum()), 4, RoundingMode.DOWN);
			//单人应得奖励
			BigDecimal should90=singleWard.multiply(new BigDecimal(0.9)).setScale(8,RoundingMode.DOWN);
			//分享奖励
			BigDecimal shareWard=singleWard.multiply(new BigDecimal(0.1)).setScale(8,RoundingMode.DOWN);
			//奖励发放
			luckys.forEach(lucky->{
				lucky.setAwardAmount(should90);
				lucky.setAddAwardAmount(shareWard);
				lucky.setAppendWx(BooleanEnum.IS_FALSE);
				lucky.setWin(BooleanEnum.IS_TRUE);
				lucky.setSettleStatus(SettleStatus.HAS_SEND);
				try {
					getService().sendWard(lucky,manager.getUnit());
					//中奖发送消息
					luckyJoinInfoService.actChangeSend(lucky.getMemberId().toString(),managerId.toString(),true);
				}catch (Exception e){
					log.info("==================用户ID:{},奖励发放失败=================",lucky.getMemberId());
					lucky.setSettleStatus(SettleStatus.SEND_FAILED);
					luckyJoinInfoMapper.updateById(lucky);
				}
			});
			subtractTotal(manager.getUnit(),should90.multiply(new BigDecimal(manager.getWinNum())),
					TransactionType.LUCKY_RETURN_BULL,"小牛快跑资金返还,扣款",totalId);
		}

		//活动结束 发送消息
		joinMemberIds.forEach(j->{
			luckyJoinInfoService.actChangeSend(j,managerId.toString(),false);
		});

		luckyGameRedisUtil.gameOver(managerId.toString());
		//站内信推送
		getService().sendSilkInfo(managerId,1,manager.getName());

    }
	@Transactional(rollbackFor = RuntimeException.class,propagation = Propagation.REQUIRES_NEW)
    public void sendWard(LuckyJoinInfo lucky,String coinUnit){
		int i = luckyJoinInfoMapper.updateById(lucky);
		Assert.isTrue(i>0,"更新奖励失败");
		//账户增加
		WalletTradeEntity entity=new WalletTradeEntity();
		entity.setType(TransactionType.LUCKY_WIN_BULL);
		entity.setRefId(String.valueOf(lucky.getId()));
		entity.setMemberId(lucky.getMemberId());
		entity.setCoinUnit(coinUnit);
		entity.setTradeBalance(lucky.getAwardAmount());
		entity.setComment("小牛快跑-->中奖了");
		MessageRespResult<Boolean> result = memberWalletApiService.trade(entity);
		ExceptionUitl.throwsMessageCodeExceptionIfFailed(result);
	}

	@Transactional(rollbackFor = RuntimeException.class,propagation = Propagation.REQUIRES_NEW)
	public void returnTicket(LuckyJoinInfo lucky,LuckyNumberManager manager){
    	lucky.setSettleStatus(SettleStatus.HAS_RETURED);
		int i = luckyJoinInfoMapper.updateById(lucky);
		Assert.isTrue(i>0,"退回票失败");
		WalletTradeEntity entity=new WalletTradeEntity();
		entity.setType(TransactionType.LUCKY_RETURN_BULL);
		entity.setRefId(String.valueOf(lucky.getId()));
		entity.setMemberId(lucky.getMemberId());
		entity.setCoinUnit(manager.getUnit());
		entity.setTradeBalance(manager.getAmount().multiply(new BigDecimal(1).subtract(manager.getServiceCharge())));
		entity.setComment("小牛快跑-->合局--退回票");
		MessageRespResult<Boolean> result = memberWalletApiService.trade(entity);
		ExceptionUitl.throwsMessageCodeExceptionIfFailed(result);


	}

	private void subtractTotal(String coinUnit,BigDecimal payAmount,TransactionType transactionType,String comment,Long totalId){
		//扣总账户
		try {
			//接受账号加钱
			WalletTradeEntity reveive=new WalletTradeEntity();
			reveive.setType(transactionType);
			reveive.setMemberId(totalId);
			reveive.setCoinUnit(coinUnit);
			reveive.setTradeBalance(payAmount);
			reveive.setComment(comment);
			MessageRespResult<Boolean> reveiveResult = memberWalletApiService.trade(reveive);
			ExceptionUitl.throwsMessageCodeExceptionIfFailed(reveiveResult);
		}catch (Exception e){
			log.info("总帐户扣钱失败:{}", ExceptionUtils.getFullStackTrace(e));
		}
	}

	/**
	 * 排序筛选中奖小牛
	 * @param bulls
	 */
    public void sortBulls(List<LuckyManageCoin> bulls){
		//排序 从高到底排列 筛选中奖小牛
		Collections.sort(bulls, new Comparator<LuckyManageCoin>() {
			@Override
			public int compare(LuckyManageCoin o1, LuckyManageCoin o2) {
				BigDecimal increase1 = Optional.ofNullable(o1.getIncrease()).orElse(BigDecimal.ZERO);
				BigDecimal increase2 = Optional.ofNullable(o2.getIncrease()).orElse(BigDecimal.ZERO);
				if (increase1.compareTo(increase2)==0){
					String coinUnit = o1.getCoinUnit();
					String coinUnit1 = o2.getCoinUnit();
//					Integer c1 = luckyJoinInfoMapper.countLuckyMemberByActIdAndCoin(o1.getNumId(), coinUnit);
//					Integer c2 = luckyJoinInfoMapper.countLuckyMemberByActIdAndCoin(o2.getNumId(), coinUnit1);
//					if(c1==c2){
						Date da1 = luckyJoinInfoMapper.findJoinTimeByBull(o1.getNumId(), coinUnit);
						Date da2 = luckyJoinInfoMapper.findJoinTimeByBull(o2.getNumId(), coinUnit1);
						if(da1==null){
							return -1;
						}
						if(da2==null){
							return -1;
						}
						return da1.before(da2)?1:-1;
//					}
//					return c1-c2;
				}
				return increase2.compareTo(increase1);
			}
		});

	}

	private void setJoinMemberCount(LuckyNumberManager manager){
		Long managerId = manager.getId();
		//更新中奖金额 中奖人数到 manager
		Map<String, Integer> joinCount = luckyGameRedisUtil.getJoinCount(managerId.toString());
		//参与票数
		Integer joinTicketCount=joinCount.get("ticket");
		manager.setJoinTicketCount(joinTicketCount);
		//参与总金额
		BigDecimal joinMemberAmount=manager.getAmount().multiply(new BigDecimal(joinTicketCount));
		manager.setJoinMemberAmount(joinMemberAmount);
		//参与人数
		Integer joinMemberCount=joinCount.get("member");
		manager.setJoinMemberCount(joinMemberCount);
	}

	/**
	 * 填充manger数据
	 * @param manager
	 * @param luckyBull
	 * @param luckys
	 * @return
	 */
    private BigDecimal setManageEndData(LuckyNumberManager manager,LuckyManageCoin luckyBull,List<LuckyJoinInfo> luckys){
		Long managerId = manager.getId();
		this.setJoinMemberCount(manager);

		//中奖票数
		Integer winNum=luckys.size();
		manager.setWinNum(winNum);
		//中奖票号
		String winTickets=luckyBull.getCoinUnit();
		manager.setWinTickets(winTickets);

		//中奖人数
		Set<Long> winMemberCountSet = luckys.stream().map(lc -> lc.getMemberId()).collect(Collectors.toSet());
		Integer winMemberCount=winMemberCountSet.size();
		manager.setWinMemberCount(winMemberCount);
		//奖金
		BigDecimal ward=manager.getAmount().multiply(new BigDecimal(manager.getJoinTicketCount()));
		//平台收益 奖金*手续费率
		BigDecimal platformProfit=ward.multiply(manager.getServiceCharge());
		if(winNum==0){
			platformProfit=ward;
		}
		manager.setPlatformProfit(platformProfit);

		log.info("活动ID:{}中奖人数:{},中奖票数:{}",managerId,winMemberCount,winNum);
    	return ward;
	}

    @Override
    public List<LuckyRunBullListVo> listBulls(ListBullParam param, IPage page,List<Long> memberActIds) {
        return baseMapper.listBulls(param, page,memberActIds);
    }

	@Override
	public LuckyRunBullListVo detailBull(Long actId) {
		return baseMapper.detailBull(actId);
	}

	@Override
	public List<Long> findMemberActIds(Long memberId) {
		return baseMapper.findMemberActIds(memberId);
	}

	@Override
	public LuckyNumberManager findLastSettleLucky(Long memberId) {
		return baseMapper.findLastSettleLucky(memberId);
	}

	@Override
	public List<LuckyManageCoin> findRealCoinBulls(Long actId) {

		List<LuckyManageCoin> coins = luckyManageCoinMapper.findByActId(actId);

		for (LuckyManageCoin c:coins){
			BigDecimal currentPrice = usdtRate(c.getCoinUnit());
			BigDecimal startPrice = Optional.ofNullable(c.getStartPrice()).orElse(currentPrice);
			if(c.getStartPrice()==null){
				c.setStartPrice(startPrice);
			}

			if(c.getEndPrice()==null){
				c.setEndPrice(currentPrice);
			}
			if(c.getIncrease()==null){
				if(startPrice.compareTo(BigDecimal.ZERO)==0){
					c.setIncrease(BigDecimal.ZERO);
				}else {
					c.setIncrease(currentPrice.subtract(startPrice).divide(startPrice,4,RoundingMode.HALF_UP));
				}
			}


		}
		return coins;
	}

	public LuckyNumberManagerServiceImpl getService() {
        return SpringContextUtil.getBean(LuckyNumberManagerServiceImpl.class);
    }

    private void capitalFlow(List<LuckyJoinInfo> joins , BigDecimal sendMoney , String unit , BigDecimal appendSendMoney , String title , BooleanEnum isWin ,BigDecimal payMoney) {
    	joins.stream().forEach(win->{

			try {
				if(isWin == BooleanEnum.IS_TRUE) {
					win.setWin(isWin);
					win.setAwardAmount(sendMoney);
					win.setAppendWx(BooleanEnum.IS_FALSE);
					win.setAddAwardAmount(appendSendMoney);
					win.setSettleStatus(SettleStatus.HAS_SEND);
					this.luckyJoinInfoMapper.updateById(win);
				}
				 //公号减去资金
				MessageRespResult<SilkDataDist> silk = silkDataDistApiService.findOne("LUCKY_CONFIG", "TOTAL_ACCOUNT_ID");
		        AssertUtil.isTrue(silk.isSuccess()&&silk.getData()!=null, LuckyErrorCode.RECEIVE_ACCOUNT_NOT_FOUND);
		        Long totalId=Long.valueOf(silk.getData().getDictVal());
		        WalletTradeEntity reveive=new WalletTradeEntity();
		        reveive.setType(isWin == BooleanEnum.IS_TRUE ? TransactionType.LUCKY_WIN_NUMBER : TransactionType.LUCKY_RETURN_NUMBER);
		        reveive.setRefId(String.valueOf(win.getId()));
		        reveive.setMemberId(totalId);
		        reveive.setCoinUnit(unit);
		        reveive.setTradeBalance(isWin == BooleanEnum.IS_TRUE ? sendMoney.negate() : payMoney.negate());
		        reveive.setComment(title);
		        //MessageRespResult<Boolean> reveiveResult = memberWalletApiService.trade(reveive);
		        MessageRespResult<WalletChangeRecord> baseResult = memberWalletApiService.tradeTccTry(reveive);
		        
		        
		        WalletTradeEntity entity=new WalletTradeEntity();
		        entity.setType(isWin == BooleanEnum.IS_TRUE ? TransactionType.LUCKY_WIN_NUMBER : TransactionType.LUCKY_RETURN_NUMBER);
		        entity.setRefId(String.valueOf(win.getId()));
		        entity.setMemberId(win.getMemberId());
		        entity.setCoinUnit(unit);
		        entity.setTradeBalance(isWin == BooleanEnum.IS_TRUE ? sendMoney : payMoney);
		        entity.setComment(title);
		        MessageRespResult<WalletChangeRecord> memberResult = memberWalletApiService.tradeTccTry(entity);
		        boolean success = false;
		        if(baseResult.isSuccess() && memberResult.isSuccess()) {
		        	MessageRespResult<Boolean> baseConfirm = memberWalletApiService.tradeTccConfirm(reveive.getMemberId(), baseResult.getData().getId());
		        	MessageRespResult<Boolean> memberConfirm = memberWalletApiService.tradeTccConfirm(entity.getMemberId(), memberResult.getData().getId());
		        	if(baseConfirm.isSuccess() && baseConfirm.getData() && memberConfirm.isSuccess() && memberConfirm.getData()) {
		        		success = true;
		        		luckyJoinInfoService.actChangeSend(win.getMemberId().toString(),win.getNumId().toString(), isWin == BooleanEnum.IS_TRUE);
		        	}
		        }
		        if(!success) {
		        	try {
	                    MessageRespResult<Boolean> resultCancel = memberWalletApiService.tradeTccCancel(reveive.getMemberId(), baseResult.getData().getId());
	                    MessageRespResult<Boolean> memberCancel = memberWalletApiService.tradeTccCancel(entity.getMemberId(), memberResult.getData().getId());
	                    ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultCancel);
	                    ExceptionUitl.throwsMessageCodeExceptionIfFailed(memberCancel);
	                    AssertUtil.isTrue(resultCancel.getData(), CommonMsgCode.ERROR);
	                    AssertUtil.isTrue(memberCancel.getData(), CommonMsgCode.ERROR);
	                } catch (Exception ex) {
	                	kafkaTemplate.send("acct-trade-tcc-cancel", "tcc-cancel",
                                JSON.toJSONString(new TradeTccCancelEntity(reveive.getMemberId(), baseResult.getData().getId())));
                        kafkaTemplate.send("acct-trade-tcc-cancel", "tcc-cancel",
                                JSON.toJSONString(new TradeTccCancelEntity(entity.getMemberId(), memberResult.getData().getId())));
                        win.setSettleStatus(SettleStatus.SEND_FAILED);
        				this.luckyJoinInfoMapper.updateById(win);
	                }
		        }
			} catch (Exception e) {
                win.setSettleStatus(SettleStatus.SEND_FAILED);
				this.luckyJoinInfoMapper.updateById(win);
			}
		});
    }


    @Autowired
    private ICoinExchange iCoinExchange;
	/**
	 * 从缓存中获取非小号行情
	 * @param coin
	 * @return
	 */
	public BigDecimal usdtRate(String coin){
		FxhApiDate fxhApiDate = fxhMap(coin);
		if(fxhApiDate!=null){
			return new BigDecimal(fxhApiDate.getPriceUsd());
		}

		MessageRespResult<BigDecimal> usdExchangeRate = iCoinExchange.getUsdExchangeRate(coin);
		return usdExchangeRate.getData();
	}


	/**
	 * 从缓存中获取非小号行情
	 * @param coin
	 * @return
	 */
	private FxhApiDate fxhMap(String coin){
		String val = luckyGameRedisUtil.getKey(String.format("entity:trade:plateMap:%s", coin));
		JSONArray array = JSONArray.parseArray(val);
		if(array!=null&&array.size()>1){
			FxhApiDate fxhApiDate = JSON.parseObject(JSON.toJSONString(array.get(1)), FxhApiDate.class);
			return fxhApiDate;
		}
		return null;
	}

	@Async
	public void sendSilkInfo(Long actId,Integer luckyType,String name){

		List<Long> jonMemberIds = luckyJoinInfoMapper.findJonMemberIds(actId);
		for (Long memberId:jonMemberIds){
			luckPushService.sendStationMessage(String.format("%s活动开奖完毕，快去看看！",name),
					String.format("%s活动开奖完毕，快去看看！",name),memberId,actId,luckyType);
		}
	}
}






















