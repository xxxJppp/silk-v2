package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.common.LuckyGameRedisUtil;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.controller.param.ListLuckyNumberParam;
import com.spark.bitrade.controller.vo.LuckyRunBullListVo;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.enums.LuckyErrorCode;
import com.spark.bitrade.mapper.LuckyJoinInfoMapper;
import com.spark.bitrade.mapper.LuckyNumberManagerMapper;
import com.spark.bitrade.service.IChatService;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.service.ISilkDataDistApiService;
import com.spark.bitrade.service.LuckyJoinInfoService;
import com.spark.bitrade.trans.TradeTccCancelEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 幸运宝参与明细 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-16
 */
@Slf4j
@Service
public class LuckyJoinInfoServiceImpl extends ServiceImpl<LuckyJoinInfoMapper, LuckyJoinInfo> implements LuckyJoinInfoService {

	@Resource
	private LuckyGameRedisUtil luckyGameRedisUtil;
	@Resource
	private LuckyNumberManagerMapper luckyNumberManagerMapper;
	@Autowired
    private IMemberWalletApiService memberWalletApiService;
	@Autowired
	private ISilkDataDistApiService silkDataDistApiService;
	@Resource
    private KafkaTemplate<String, String> kafkaTemplate;
	@Autowired
	private IChatService chatService;
	private final int LUCKY_NUM_TYPE = 0;
	
	@Override
	public Map<String, Object> activityRecord(Long member) {
		return luckyGameRedisUtil.getActivityRecord(member.toString());
	}


	/**
	 * 加入幸运号活动
		 * 
		 * @param member
		 * @param payCount
		 * @param gameId
		 * @return
		 * @author zhaopeng
		 * @since 2019年12月17日
	 */
	@Transactional(rollbackFor = RuntimeException.class)
	@Override
	public MessageRespResult<List<String>> joinLuckyNumberGame( Member member, int payCount, Long gameId ,String coinUnit) {
		/*if(payCount < 1) {
			return MessageRespResult.error("购买数量不能小于1");
		}*/
		AssertUtil.isTrue(payCount > 0, LuckyErrorCode.BUY_COUNT_MUST_GT_ZERO);
		/*if(gameId < 1) {
			return  MessageRespResult.error("指定活动不存在");
		}*/
		AssertUtil.isTrue(gameId > 0, LuckyErrorCode.ACT_NOT_FIND);
		LuckyNumberManager numberGame = this.luckyNumberManagerMapper.selectNumberManagerByIdAndType(gameId, LUCKY_NUM_TYPE);
		/*if(numberGame == null) {
			return  MessageRespResult.error("指定活动不存在");
		}*/
		AssertUtil.notNull(numberGame, LuckyErrorCode.ACT_NOT_FIND);
		Date create = new Date();
		AssertUtil.isTrue(create.getTime() >= numberGame.getStartTime().getTime() && create.getTime() < numberGame.getEndTime().getTime(),LuckyErrorCode.JOIN_TIME_ERROR);
			
		//已购买数量检查
		QueryWrapper<LuckyJoinInfo> queryWrapper = new QueryWrapper<LuckyJoinInfo>();
		queryWrapper.eq("num_id", numberGame.getId());
		queryWrapper.eq("member_id", member.getId());
		int hasPayCount = this.baseMapper.selectCount(queryWrapper);
		/*if((hasPayCount + payCount) > numberGame.getSingleMaxNum()) {
			
			return  MessageRespResult.error("超过购买数量，每人限购 " + numberGame.getSingleMaxNum() + " 张 , 当前已购 " + hasPayCount + " 张");
		}*/
		AssertUtil.isTrue((hasPayCount + payCount) <= numberGame.getSingleMaxNum(), LuckyErrorCode.OVER_MAX_TICKET);
		List<LuckyJoinInfo> joinInfos = new ArrayList<LuckyJoinInfo>();
		//创建活动参与明细
		for(int i = 0 ; i < payCount ; i++) {
			String tickNum = luckyGameRedisUtil.joinGame(gameId.toString(), member.getId().toString());
			LuckyJoinInfo join = new LuckyJoinInfo();
			join.setAddAwardAmount(new BigDecimal(0));
			join.setAppendWx(BooleanEnum.IS_FALSE);
			join.setAwardAmount(new BigDecimal(0));
			//join.setCreateId(member.getId());
			join.setCreateTime(create);
			join.setDeleteState(BooleanEnum.IS_FALSE);
			join.setJoinInfo(tickNum);
			join.setMemberId(member.getId());
			join.setNumId(numberGame.getId());
			join.setWin(BooleanEnum.IS_FALSE);
			joinInfos.add(join);
		}
		boolean batchSave = this.saveBatch(joinInfos);
		if(batchSave) {

			// 账户支付
			BigDecimal payAmount=numberGame.getAmount().multiply(new BigDecimal(payCount));
			
	        WalletTradeEntity reveive=new WalletTradeEntity();
	        reveive.setType(TransactionType.BUY_LUCKY_NUMBER);
	        reveive.setRefId(String.valueOf(joinInfos.get(0).getId()));
	        reveive.setMemberId(member.getId());
	        reveive.setCoinUnit(numberGame.getUnit());
	        reveive.setTradeBalance(payAmount.negate());
	        reveive.setComment("购买幸运号票");
	        MessageRespResult<WalletChangeRecord> result = memberWalletApiService.tradeTccTry(reveive);
	        
	        MessageRespResult<SilkDataDist> silk = silkDataDistApiService.findOne("LUCKY_CONFIG", "TOTAL_ACCOUNT_ID");
	        AssertUtil.isTrue(silk.isSuccess()&&silk.getData()!=null,LuckyErrorCode.RECEIVE_ACCOUNT_NOT_FOUND);
	        Long totalId=Long.valueOf(silk.getData().getDictVal());
	        WalletTradeEntity entity=new WalletTradeEntity();
	        entity.setType(TransactionType.BUY_LUCKY_NUMBER);
	        entity.setRefId(String.valueOf(joinInfos.get(0).getId()));
	        entity.setMemberId(totalId);
	        entity.setCoinUnit(numberGame.getUnit());
	        entity.setTradeBalance(payAmount);
	        entity.setComment("购买幸运号票");
	        MessageRespResult<WalletChangeRecord> reveiveResult = memberWalletApiService.tradeTccTry(entity);
	        
	        boolean success = false;
	        if(result.isSuccess() && reveiveResult.isSuccess()) {
	        	MessageRespResult<Boolean> baseConfirm = memberWalletApiService.tradeTccConfirm(reveive.getMemberId(), result.getData().getId());
	        	MessageRespResult<Boolean> memberConfirm = memberWalletApiService.tradeTccConfirm(entity.getMemberId(), reveiveResult.getData().getId());
	        	if(baseConfirm.isSuccess() && baseConfirm.getData() && memberConfirm.isSuccess() && memberConfirm.getData()) {
	        		success = true;
	        	}
	        }
	        if(!success) {
	        	try {
                    MessageRespResult<Boolean> resultCancel = memberWalletApiService.tradeTccCancel(reveive.getMemberId(), result.getData().getId());
                    MessageRespResult<Boolean> memberCancel = memberWalletApiService.tradeTccCancel(entity.getMemberId(), reveiveResult.getData().getId());
                    ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultCancel);
                    ExceptionUitl.throwsMessageCodeExceptionIfFailed(memberCancel);
                    AssertUtil.isTrue(resultCancel.getData(), CommonMsgCode.ERROR);
                    AssertUtil.isTrue(memberCancel.getData(), CommonMsgCode.ERROR);
                    
                } catch (Exception ex) {
                	try {
                		kafkaTemplate.send("acct-trade-tcc-cancel", "tcc-cancel",
                                JSON.toJSONString(new TradeTccCancelEntity(reveive.getMemberId(), result.getData().getId())));
                        kafkaTemplate.send("acct-trade-tcc-cancel", "tcc-cancel",
                                JSON.toJSONString(new TradeTccCancelEntity(entity.getMemberId(), reveiveResult.getData().getId())));
					} catch (Exception e) {
						log.error("发送kafka取消支付消息失败");
					}
                }
                //异常情况下还原缓存
	        	joinInfos.stream().forEach(jl -> {
    				this.luckyGameRedisUtil.joinExecption(gameId.toString(), member.getId().toString(), jl.getJoinInfo());
    			});
	        	AssertUtil.isTrue(false,LuckyErrorCode.JOIN_FAILED);
    			//throw new RuntimeException("购买票号失败");
	        }
		}
		
		return MessageRespResult.success("游戏加入成功", joinInfos.stream().map(LuckyJoinInfo::getJoinInfo).collect(Collectors.toList())); //加入成功，返回购买票号
		
	}


	/**
	 * 处理分享成功的逻辑
		 *
		 * @param param
		 * @return
		 * @author zhaopeng
		 * @since 2019年12月17日
	 */
	@Transactional
	@Override
	public MessageRespResult<String> appendWx(Member member ,ListLuckyNumberParam param) {
		/*if(param.getGameId() == null || param.getGameId() == 0) {
			return MessageRespResult.error("活动不存在");
		}*/
		AssertUtil.isTrue((param.getGameId() != null && param.getGameId() != 0),LuckyErrorCode.ACT_NOT_FIND);
		//查询活动票信息
		QueryWrapper<LuckyJoinInfo> queryWrapper = new QueryWrapper<LuckyJoinInfo>();
		queryWrapper.eq("num_id", param.getGameId())
		.eq("member_id", member.getId())
		.eq("win",1) //中奖票
		.eq("append_wx", BooleanEnum.IS_FALSE); //未分享
		int count = this.baseMapper.selectCount(queryWrapper);
		/*if(count < 1) {
			return MessageRespResult.error("当前活动票不可分享");
		}*/
		AssertUtil.isTrue(count > 0,LuckyErrorCode.SHARE_FAILED);
		LuckyNumberManager numberGame = this.luckyNumberManagerMapper.selectNumberManagerByIdAndType(param.getGameId(), LUCKY_NUM_TYPE);
		List<LuckyJoinInfo> info = this.baseMapper.selectList(queryWrapper);
		//对操作锁检查，有效期60秒
		long lock = luckyGameRedisUtil.increment(String.format(LuckyGameRedisUtil.ACTIVITY_GAME_APPEND_WX_LOCK, param.getGameId().toString()+"_"+member.getId()), LuckyGameRedisUtil.DELTA_ADD , 60);
		if(lock > 1) {
			return MessageRespResult.error("请不要重复分享");
		}
		BigDecimal addAmount = new BigDecimal(0);
		for(LuckyJoinInfo in : info) {
			in.setAppendWx(BooleanEnum.IS_TRUE);
			addAmount = addAmount.add(in.getAddAwardAmount());
			int update = this.baseMapper.updateById(in);
		}
		// 账户支付
		MessageRespResult<SilkDataDist> silk = silkDataDistApiService.findOne("LUCKY_CONFIG", "TOTAL_ACCOUNT_ID");
        AssertUtil.isTrue(silk.isSuccess()&&silk.getData()!=null,LuckyErrorCode.RECEIVE_ACCOUNT_NOT_FOUND);
        Long totalId=Long.valueOf(silk.getData().getDictVal());
        WalletTradeEntity reveive=new WalletTradeEntity();
        reveive.setType(TransactionType.LUCKY_APPEND_WX_NUMBER);
        reveive.setRefId(String.valueOf(info.get(0).getId()));
        reveive.setMemberId(totalId);
        reveive.setCoinUnit(numberGame.getUnit());
        reveive.setTradeBalance(addAmount.negate());
        reveive.setComment("幸运号分享追加");
        MessageRespResult<WalletChangeRecord> reveiveResult = memberWalletApiService.tradeTccTry(reveive);
        
        WalletTradeEntity entity=new WalletTradeEntity();
        entity.setType(TransactionType.LUCKY_APPEND_WX_NUMBER);
        entity.setRefId(String.valueOf(info.get(0).getId()));
        entity.setMemberId(member.getId());
        entity.setCoinUnit(numberGame.getUnit());
        entity.setTradeBalance(addAmount);
        entity.setComment("购买幸运号票");
        MessageRespResult<WalletChangeRecord> result = memberWalletApiService.tradeTccTry(entity);
        boolean success = false;
        if(reveiveResult.isSuccess() && result.isSuccess()) {
        	MessageRespResult<Boolean> baseConfirm = memberWalletApiService.tradeTccConfirm(totalId, reveiveResult.getData().getId());
        	MessageRespResult<Boolean> memberConfirm = memberWalletApiService.tradeTccConfirm(member.getId(), result.getData().getId());
        	if(baseConfirm.isSuccess() && baseConfirm.getData() && memberConfirm.isSuccess() && memberConfirm.getData()) {
        		success = true;
        	}
        }
        if(!success) {
        	try {
                MessageRespResult<Boolean> resultCancel = memberWalletApiService.tradeTccCancel(totalId, reveiveResult.getData().getId());
                MessageRespResult<Boolean> memberCancel = memberWalletApiService.tradeTccCancel(member.getId(), result.getData().getId());
                ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultCancel);
                ExceptionUitl.throwsMessageCodeExceptionIfFailed(memberCancel);
                AssertUtil.isTrue(resultCancel.getData(), CommonMsgCode.ERROR);
                AssertUtil.isTrue(memberCancel.getData(), CommonMsgCode.ERROR);
        		
            } catch (Exception ex) {
            	kafkaTemplate.send("acct-trade-tcc-cancel", "tcc-cancel",
                        JSON.toJSONString(new TradeTccCancelEntity(totalId, reveiveResult.getData().getId())));
                kafkaTemplate.send("acct-trade-tcc-cancel", "tcc-cancel",
                        JSON.toJSONString(new TradeTccCancelEntity(member.getId(), result.getData().getId())));
            }
        } else {

        	readAck(member.getId(), param.getGameId().toString(), 2);
        	luckyGameRedisUtil.delKey(String.format(LuckyGameRedisUtil.ACTIVITY_GAME_APPEND_WX_LOCK, param.getGameId().toString()+"_"+member.getId()));
	        return MessageRespResult.success("奖金发放完成");
        }
        info.stream().forEach(in -> {
			in.setAppendWx(BooleanEnum.IS_FALSE);
			int update = this.baseMapper.updateById(in);
		});
    	luckyGameRedisUtil.delKey(String.format(LuckyGameRedisUtil.ACTIVITY_GAME_APPEND_WX_LOCK,param.getGameId().toString()+"_"+member.getId()));
    	AssertUtil.isTrue(false,LuckyErrorCode.MONEY_SEND_ERRO);
    	return MessageRespResult.error("奖金发放失败");
		
	}


	@Override
	public List<LuckyRunBullListVo.MyJoinBulls> findMyJoinBulls(Long memberId, Long actId) {
		return baseMapper.findMyJoinBulls(memberId,actId);
	}

	@Override
	public LuckyRunBullListVo findMyBullLucky(Long memberId, Long actId) {
		return baseMapper.findMyBullLucky(memberId,actId);
	}

	/**
	 * 用户参加的小牛快跑注数
	 * @param memberId
	 * @param actId
	 * @return
	 */
	@Override
	public Integer findMyJoinBullCount(Long memberId, Long actId) {
		Optional<Integer> myJoinBullCount = baseMapper.findMyJoinBullCount(memberId, actId);
		return myJoinBullCount.orElse(0);
	}

	@Override
	public int updateBullShareStatus(Long actId, Long memberId) {
		return baseMapper.updateBullShareStatus(actId,memberId);
	}

	/**
	 *
	 * @param memberId
	 * @param actId
	 */
	@Override
	@Async
	public void readAck(Long memberId, String actId,Integer type) {
		if (type==1){
			luckyGameRedisUtil.readChange(memberId.toString(),actId);
		}
		if (type==2){
			luckyGameRedisUtil.hasAppendWx(memberId.toString(),actId);
		}

		Map<String, Object> activityRecord = luckyGameRedisUtil.getActivityRecord(memberId.toString());
		Object my = activityRecord.get("my");
		//读消息之后推送给前端
		try {
			chatService.sendRedPoint(memberId.toString(),my.toString());
		}catch (Exception e){
			log.info("===========推送失败==========");
		}

	}

	/**
	 * 状态改变推送消息
	 * @param memberId
	 * @param actId
	 * @param isLucky 是否中奖
	 */
	@Override
	@Async
	public void actChangeSend(String memberId, String actId,boolean isLucky){
		if (isLucky){
			luckyGameRedisUtil.gameAppendWx(actId,memberId);
		}
		luckyGameRedisUtil.gameStateChange(actId,1);

		Map<String, Object> activityRecord = luckyGameRedisUtil.getActivityRecord(memberId);
		Object my = activityRecord.get("my");
		//读消息之后推送给前端
		try {
			chatService.sendRedPoint(memberId,my.toString());
		}catch (Exception e){
			log.info("===========推送失败==========");
		}

	}

}
