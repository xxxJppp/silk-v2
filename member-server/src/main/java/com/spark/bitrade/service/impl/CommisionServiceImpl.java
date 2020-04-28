package com.spark.bitrade.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.google.common.base.Strings;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.spark.bitrade.constant.DistributeTypeEnum;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constant.WalletChangeType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.MemberRecommendCommision;
import com.spark.bitrade.entity.SilkDataDist;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.CommisionService;
import com.spark.bitrade.service.GlobalConfService;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.service.ISilkDataDistApiService;
import com.spark.bitrade.service.MemberRecommendCommisionService;
import com.spark.bitrade.sms.KafkaSMSProvider;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.utils.MixUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommisionServiceImpl implements CommisionService {
	

	@Autowired
	private IMemberWalletApiService memberWalletApiService;
	
	@Autowired
	private MemberRecommendCommisionService memberRecommendCommisionService;
	
	@Autowired
	private CommisionService commisionService;
	
	@Autowired
	private GlobalConfService confService;
	
	@Autowired
	private ISilkDataDistApiService silkDataDistApiService;

	@Autowired
	 private KafkaSMSProvider smsProvider;

	public List<MemberRecommendCommision> transferCommision(List<MemberRecommendCommision> commisionList) {
		MessageRespResult<SilkDataDist> silk = silkDataDistApiService.findOne("MEMBER_SYSTEM_CONFIG", "TOTAL_ACCOUNT_ID");
		if(!silk.isSuccess() || silk.getData() == null) {
			log.error("返佣操作停止，未找到会员费归集/返佣账号");
			return commisionList;
		}
        Long totalId=Long.valueOf(silk.getData().getDictVal()); //归集账号
		
        WalletTradeEntity reveive=new WalletTradeEntity();
        reveive.setType(TransactionType.PROMOTION_AWARD);
        reveive.setMemberId(totalId);
        reveive.setComment("会员体系返佣扣除");
        boolean endTransfer = false; //是否停止后续划账，当主账余额不足时，后续处理放弃
        
		for (MemberRecommendCommision mrc : commisionList) {
			reveive.setType(TransactionType.PROMOTION_AWARD);
			WalletTradeEntity tradeEntity = new WalletTradeEntity();
	        tradeEntity.setType(TransactionType.PROMOTION_AWARD);
			reveive.setComment("会员体系返佣扣除");
			if(endTransfer) {
				break;
			}
			String comment = "会员体系返佣";
			if(mrc.getBizType().intValue() == 40) {
				reveive.setComment(mrc.getDeliverToMemberId().longValue() + "法币交易USDC商家返佣");
				comment = "法币交易USDC商家返佣";
				tradeEntity.setType(TransactionType.CURRENCY_GET);
				reveive.setType(TransactionType.CURRENCY_GET);
			}
			if(mrc.getBizType().intValue() == 50 || mrc.getBizType().intValue() == 60) {
				comment = "会员体系法币交易USDC返佣";
				reveive.setComment(mrc.getOrderMemberId().longValue() + "法币交易USDC返佣给"+ mrc.getDeliverToMemberId().longValue());
			}
		
			MessageRespResult<String> coinIdResult = memberWalletApiService.getCoinNameByUnit(mrc.getCommisionUnit());
	        tradeEntity.setRefId(mrc.getRefId());
	        tradeEntity.setChangeType(WalletChangeType.TRADE);
	        tradeEntity.setMemberId(mrc.getDeliverToMemberId());
	        tradeEntity.setComment(comment);
	        tradeEntity.setTradeBalance(mrc.getAccumulativeQuantity());
	        tradeEntity.setCoinUnit(mrc.getCommisionUnit());
	        tradeEntity.setCoinId(coinIdResult.getData());

	        reveive.setRefId(mrc.getRefId());
	        reveive.setCoinUnit(mrc.getCommisionUnit());
	        reveive.setTradeBalance(mrc.getAccumulativeQuantity().negate());
	        
			Long walletChangeRecordId = 0l;
			Long baseChangeRecordId = 0l;
	        try {
	            // try
	            MessageRespResult<WalletChangeRecord> tradeResult = memberWalletApiService.tradeTccTry(tradeEntity);

	            ExceptionUitl.throwsMessageCodeExceptionIfFailed(tradeResult);
	            AssertUtil.notNull(tradeResult.getData(), CommonMsgCode.ERROR);
	            
	            //归集账号划账
	            MessageRespResult<WalletChangeRecord> baseResult = memberWalletApiService.tradeTccTry(reveive);
	            
	            if(!baseResult.isSuccess() && baseResult.getCode() == 6010) { //交易失败原因为归集账号余额不足
	            	endTransfer = true;
	            	//发送短信到财务手机
	            	MessageRespResult<SilkDataDist> phone = silkDataDistApiService.findOne("MEMBER_SYSTEM_CONFIG", "ACCOUNT_PHONE_NUMBER");
	            	String content = "会员费归集和返佣账号 " + totalId + " 可用余额不足，部分佣金没有发放成功，请及时充值。";
	                smsProvider.sendSms("", "86", phone.getData().getDictVal(), content);
	            }
	            ExceptionUitl.throwsMessageCodeExceptionIfFailed(baseResult);
	            AssertUtil.notNull(baseResult.getData(), CommonMsgCode.ERROR);
	            
	            // 流水记录ID
	            walletChangeRecordId = tradeResult.getData().getId();
	            
	            baseChangeRecordId = baseResult.getData().getId();
	            log.debug("*****************************" + tradeResult.getData().toString() + "\n"
	            		+      "*****************************" + baseResult.getData().toString() );

	           

	            // confirm
	            MessageRespResult<Boolean> resultConfirm = memberWalletApiService.tradeTccConfirm(mrc.getDeliverToMemberId(), walletChangeRecordId);
	            ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultConfirm);
	            AssertUtil.isTrue(resultConfirm.getData(), CommonMsgCode.ERROR);
	            //归集账号确认
	            MessageRespResult<Boolean> baseConfirm = memberWalletApiService.tradeTccConfirm(totalId, baseChangeRecordId);
	            ExceptionUitl.throwsMessageCodeExceptionIfFailed(baseConfirm);
	            AssertUtil.isTrue(baseConfirm.getData(), CommonMsgCode.ERROR);
	           
	            
	            mrc.setDistributeStatus(DistributeTypeEnum.DISTRIBUTED.getCode());
	            mrc.setTransferId(walletChangeRecordId);

	           
	        } catch (Exception ex) {
	            log.error("处理失败 [   err = '{}' ]", ex.getMessage());
	            log.error("操作失败", ex);
	            throw new MessageCodeException(CommonMsgCode.FAILURE);
	        } finally {
	            if (walletChangeRecordId != null) {
	                // cancel
	                try {
	                    MessageRespResult<Boolean> resultCancel = memberWalletApiService.tradeTccCancel(mrc.getDeliverToMemberId(), walletChangeRecordId);
	                    // throw
	                    ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultCancel);
	                    AssertUtil.isTrue(resultCancel.getData(), CommonMsgCode.ERROR);
	                } catch (Exception ex) {
	                    log.error("账户变动业务取消失败", ex);
	                }
	            }
	            if (baseChangeRecordId != null) {
	                // 归集账号操作还原
	                try {
	                    MessageRespResult<Boolean> resultCancel = memberWalletApiService.tradeTccCancel(totalId, baseChangeRecordId);
	                    ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultCancel);
	                    AssertUtil.isTrue(resultCancel.getData(), CommonMsgCode.ERROR);
	                } catch (Exception ex) {
	                    log.error("账户变动业务取消失败", ex);
	                }
	            }
	        }
		}
		return commisionList;
		 
	}
	
	@Override
	public void distribute() {
		String status = this.confService.getMemberCommisionDistributeStatus();
		if(Strings.isNullOrEmpty(status)) {
			log.info("============member commision distribute status is NOT SET=========please check your config!");
			return;
		}
		if(status.toLowerCase().equals("on")) {
			log.info(MixUtil.getCurrentDate() + " start to transfer commision fee");
			
			List<MemberRecommendCommision> rcList = this.memberRecommendCommisionService.getMemberRecommendCommisionByStatus(10);
			List<MemberRecommendCommision> oldList = this.memberRecommendCommisionService.getMemberRecommendCommisionByStatus(11);
			List<MemberRecommendCommision> distributeList = Lists.newArrayList();
			if(rcList != null &&!rcList.isEmpty()) {

				//分组
				Map<Object, List<MemberRecommendCommision>> map = rcList.stream().collect(Collectors.groupingBy(r -> r.getDeliverToMemberId()));
				for (Object key : map.keySet()) {
					
					System.out.println(key + "====" + map.get(key).size());
					
					
					 BigDecimal sum = map.get(key).stream().map(MemberRecommendCommision::getCommisionUsdtQty) .reduce(BigDecimal.ZERO,BigDecimal::add);
					 //滤出累计大于1的部分
					 //if(sum.compareTo(BigDecimal.ONE) > 0) {
				     if(sum.compareTo(new BigDecimal(0.1)) > 0) {
						List<Long> distributeIds = map.get(key).stream().map(MemberRecommendCommision::getId).collect(Collectors.toList());
						boolean flag = this.memberRecommendCommisionService.updateDistributingStatus(distributeIds);
						log.info(key + "============distributing =======" +Joiner.on(",").join(distributeIds) +"=========result:" + flag);
						
						distributeList.addAll(map.get(key));
					 }
					 
				}
			}
			
			if(CollectionUtils.isNotEmpty(oldList)) {//如果上一次的发放因为其他原因导致发放失败，标记为11，新任务执行时需要处理除了当前新任务之外，上一次任务遗留的11状态
				distributeList.addAll(oldList);
			}
			
			
			log.info("==========plan to distribute ids:" + Joiner.on(",").join(distributeList.stream().map(MemberRecommendCommision::getId).collect(Collectors.toList())));
			List<MemberRecommendCommision> distributeResult = this.commisionService.transferCommision(distributeList);
			
			List<MemberRecommendCommision> successDistributeList  = distributeResult.stream().filter(rc -> rc.getDistributeStatus() == DistributeTypeEnum.DISTRIBUTED.getCode()).collect(Collectors.toList());
			
			this.memberRecommendCommisionService.updateDistributeStatus(successDistributeList);
		}
		if(status.toLowerCase().equals("off")) {
			log.info("============member commision distribute status is OFF=========do nothing!");
		}
		
		
	}
	
}
