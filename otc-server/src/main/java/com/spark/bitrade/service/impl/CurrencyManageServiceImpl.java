package com.spark.bitrade.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.CurrencyManage;
import com.spark.bitrade.entity.CurrencyRuleSetting;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.SilkDataDist;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.enums.OtcExceptionMsg;
import com.spark.bitrade.mapper.CurrencyManageMapper;
import com.spark.bitrade.mapper.MemberMapper;
import com.spark.bitrade.service.AdvertiseService;
import com.spark.bitrade.service.CurrencyManageService;
import com.spark.bitrade.service.CurrencyRuleSettingService;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.service.ISilkDataDistApiService;
import com.spark.bitrade.service.PaySettingService;
import com.spark.bitrade.trans.TradeTccCancelEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;

/**
 * <p>
 * 法币管理 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2020-03-18
 */
@Service
public class CurrencyManageServiceImpl extends ServiceImpl<CurrencyManageMapper, CurrencyManage> implements CurrencyManageService {

	@Resource
	private MemberMapper memberMapper;
	@Resource
	private CurrencyRuleSettingService currencyRuleSettingService;
	@Autowired
    private IMemberWalletApiService memberWalletApiService;
	@Resource
    private KafkaTemplate<String, String> kafkaTemplate;
	@Resource
	private AdvertiseService advertiseService;
	@Resource
	private PaySettingService paySettingService;
	@Autowired
	private ISilkDataDistApiService silkDataDistApiService;
	
	@Override
	public MessageRespResult<CurrencyManage> getMemberPaySetting(Member member) {
		Long baseId = this.memberMapper.selectMemberCurrency(member.getId());

		AssertUtil.notNull(baseId, OtcExceptionMsg.MEMBER_HAS_NO_BASE_CURRENCY);

		CurrencyManage cm = this.getById(baseId);
		AssertUtil.notNull(cm, OtcExceptionMsg.INVALID_CURRENCY);
		cm.setPaySetting(paySettingService.getPaySettingByCurrency(cm));
		cm.setCreateId(null);
		cm.setCreateTime(null);
		cm.setCurrencyOrder(null);
		cm.setUpdateId(null);
		cm.setUpdateTime(null);
		return MessageRespResult.success("", cm);
	}

	@Override
	public MessageRespResult<String> setMemberPaySetting(Member member, Long baseId) {
		//失效其他法币的广告
		this.advertiseService.invalidAdvertise(member,baseId);
		AssertUtil.notNull(baseId, OtcExceptionMsg.INVALID_CURRENCY);
		CurrencyManage cm = this.getById(baseId);
		AssertUtil.notNull(cm, OtcExceptionMsg.INVALID_CURRENCY);

		cm.setCreateId(null);
		cm.setCreateTime(null);
		cm.setCurrencyOrder(null);
		cm.setUpdateId(null);
		cm.setUpdateTime(null);
		this.memberMapper.updateMmeberCurrency(member.getId(), baseId);
		return MessageRespResult.success("", "设置成功");
	}

	@Override
	public MessageRespResult<CurrencyManage> updateMmeberCurrenc(Member member, Long baseId) {
		AssertUtil.notNull(baseId, OtcExceptionMsg.INVALID_CURRENCY);
		CurrencyManage cm = this.getById(baseId);
		AssertUtil.notNull(cm, OtcExceptionMsg.INVALID_CURRENCY);

		// 判断是否还有未完成订单与已上架的广告
		this.advertiseService.checkOrderAndPutOn(member.getId());

		//查询配置信息中修改账号需要支付的usdt数目
		CurrencyRuleSetting setting = this.currencyRuleSettingService.getOne(new QueryWrapper<CurrencyRuleSetting>().eq("rule_key", "EDIT_BASE_UNIT"));
		AssertUtil.notNull(setting, OtcExceptionMsg.HAS_NO_BASE_CHANGE_SETTING);

		//判断是否设置的法币是原有设置
		Long oldBaseId = this.memberMapper.selectMemberCurrency(member.getId());
		if(oldBaseId != null && oldBaseId.longValue() == baseId.longValue()) { //重复设置
			AssertUtil.notNull(null, OtcExceptionMsg.HAS_SET_THIS_CURRENCY_BEFOR);
		}
		this.advertiseService.invalidAdvertise(member,baseId);
		//资金划转
		WalletTradeEntity reveive=new WalletTradeEntity();
        reveive.setType(TransactionType.CHANGE_CURRENCY);
        reveive.setRefId(String.valueOf(member.getId().toString() + "-" + baseId.toString() + "-"+System.currentTimeMillis()));
        reveive.setMemberId(member.getId());
        reveive.setCoinUnit("USDT");
        reveive.setTradeBalance(new BigDecimal(setting.getRuleValue()).negate());
        reveive.setComment("修改默认法币");
        MessageRespResult<WalletChangeRecord> result = memberWalletApiService.tradeTccTry(reveive);
        log.error("修改默认法币用户支付--->" +result.getCode() + "----" + result.getMessage());
        if(result.isSuccess()) {
        	//归集账号
        	MessageRespResult<SilkDataDist> silk = silkDataDistApiService.findOne("FEE_COLLECTION", "LEGAL_TENDER_FEE_COLLECTION");
	        AssertUtil.isTrue(silk.isSuccess()&&silk.getData()!=null, OtcExceptionMsg.RECEIVE_ACCOUNT_NOT_FOUND);
	        Long totalId=Long.valueOf(silk.getData().getDictVal());
        	
	        WalletTradeEntity baseReveive=new WalletTradeEntity();
	        baseReveive.setType(TransactionType.CHANGE_CURRENCY);
	        baseReveive.setRefId(reveive.getRefId());
	        baseReveive.setMemberId(totalId);
	        baseReveive.setCoinUnit("USDT");
	        baseReveive.setTradeBalance(new BigDecimal(setting.getRuleValue()));
	        baseReveive.setComment(member.getId() + "修改默认法币");
	        MessageRespResult<WalletChangeRecord> baseResult = memberWalletApiService.tradeTccTry(baseReveive);
	        log.error("修改默认法币账号归集--->" +result.getCode() + "----" + result.getMessage());
	        if(baseResult.isSuccess()) {
	        	MessageRespResult<Boolean> memberConfirm = memberWalletApiService.tradeTccConfirm(member.getId(), result.getData().getId());
	        	MessageRespResult<Boolean> baseMemberConfirm = memberWalletApiService.tradeTccConfirm(totalId, baseResult.getData().getId());
	        	if(memberConfirm.isSuccess() && memberConfirm.getData() && baseMemberConfirm.isSuccess() && baseMemberConfirm.getData()) {
	        		//修改法币
	        		this.memberMapper.updateMmeberCurrency(member.getId(), baseId);
	        		return MessageRespResult.success("", "修改成功");
	        	}
	        }
	        try {
	        	MessageRespResult<Boolean> baseMemberCancel = memberWalletApiService.tradeTccCancel(totalId, baseResult.getData().getId());
	            ExceptionUitl.throwsMessageCodeExceptionIfFailed(baseMemberCancel);
	            AssertUtil.isTrue(baseMemberCancel.getData(), CommonMsgCode.ERROR);
	            AssertUtil.notNull(null, OtcExceptionMsg.CHANGE_CURRENCY_ERROR);
			} catch (Exception e) {
				log.error("归集资金回滚异常--》" +result.getCode() + "----" + result.getMessage());
	            try {
	            	kafkaTemplate.send("acct-trade-tcc-cancel", "tcc-cancel",
	                        JSON.toJSONString(new TradeTccCancelEntity(member.getId(), result.getData().getId())));
	            }
	            catch (Exception ex) {
	            	
				}
			}
        	
        	
        }
        if(result.getData() == null && result.getCode() == 6010) { //交易没有执行成功，返回data为null
        	AssertUtil.notNull(null, OtcExceptionMsg.ACCOUNT_BALANCE_INSUFFICIENT);
    	}
        try {
        	MessageRespResult<Boolean> memberCancel = memberWalletApiService.tradeTccCancel(member.getId(), result.getData().getId());
            ExceptionUitl.throwsMessageCodeExceptionIfFailed(memberCancel);
            AssertUtil.isTrue(memberCancel.getData(), CommonMsgCode.ERROR);
            AssertUtil.notNull(null, OtcExceptionMsg.CHANGE_CURRENCY_ERROR);
		} catch (Exception e) {
			log.error("资金回滚异常--》" +result.getCode() + "----" + result.getMessage());
            try {
            	kafkaTemplate.send("acct-trade-tcc-cancel", "tcc-cancel",
                        JSON.toJSONString(new TradeTccCancelEntity(member.getId(), result.getData().getId())));
            }
            catch (Exception ex) {
            	AssertUtil.notNull(null, OtcExceptionMsg.CHANGE_CURRENCY_ERROR);
			}
		}

        AssertUtil.notNull(null, OtcExceptionMsg.CHANGE_CURRENCY_ERROR);
        return null;
	}


}
