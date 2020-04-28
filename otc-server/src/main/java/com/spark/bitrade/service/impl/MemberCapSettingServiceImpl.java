package com.spark.bitrade.service.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.common.RedisUtil;
import com.spark.bitrade.config.AliyunConfig;
import com.spark.bitrade.constant.CertifiedBusinessStatus;
import com.spark.bitrade.constant.RealNameStatus;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberCapSetting;
import com.spark.bitrade.entity.PaySetting;
import com.spark.bitrade.enums.OtcExceptionMsg;
import com.spark.bitrade.mapper.MemberCapSettingMapper;
import com.spark.bitrade.service.AdvertiseService;
import com.spark.bitrade.service.IMemberApiService;
import com.spark.bitrade.service.MemberCapSettingService;
import com.spark.bitrade.util.AliyunUtil;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;

/**
 * <p>
 * 用户支付方式对应关系（配置内容与原有结构应保持一致） 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2020-03-18
 */
@Service
public class MemberCapSettingServiceImpl extends ServiceImpl<MemberCapSettingMapper, MemberCapSetting> implements MemberCapSettingService {

	@Resource
	private RedisUtil redusUtil;
	@Resource
	private AliyunConfig aliyunConfig;
	@Resource
	private IMemberApiService iMemberApiService;
	@Resource
	private AdvertiseService advertiseService;
	
	private Log logger= LogFactory.getLog(getClass());
	
	@Override
	public MessageRespResult<JSONObject> queryCapSettingByMember(Member member) {
		//获取全部可用配置
		String setings = this.redusUtil.getVal("otc:paysetting:all");
		if(StringUtils.isBlank(setings)) {
			return MessageRespResult.success("", new JSONObject());
		}
		List<MemberCapSetting> memberSettings = this.baseMapper.selectList(new QueryWrapper<MemberCapSetting>().eq("member_id",member.getId()));
		List<PaySetting> allSetting = JSONArray.parseArray(setings, PaySetting.class);

		JSONObject result = new JSONObject();
		allSetting.stream().forEach(each -> {
			JSONObject cacheResult = new JSONObject();
			if(each.getPayState().equals("1")) { //是否已启用
				if(CollectionUtils.isNotEmpty(memberSettings) && memberSettings.stream().filter(cap -> cap.getPayKey().equals(each.getPayKey())).count() > 0) {
					JSONObject set = JSON.parseObject(memberSettings.stream().filter(cap -> cap.getPayKey().equals(each.getPayKey())).collect(Collectors.toList()).get(0).getPayCode());
					set.put("payKey", each.getPayKey());
					set.put("verified", 1);
					if(each.getAppendFile().equals("1")  && StringUtils.isNotBlank(set.getString(each.getFileJoinField())) ) { //是否包含外部文件
						try {
							set.put(each.getFileJoinField(), AliyunUtil.getPrivateUrl(aliyunConfig, set.getString(each.getFileJoinField())));
						} catch (Exception e) {
							logger.error("请求地址异常---->" + set.getString(each.getFileJoinField()) , e);
						}
					}
					cacheResult.put(each.getPayKey(), set);
				}
				else {
					JSONObject set = new JSONObject();
					set.put("payKey", each.getPayKey());
					set.put("verified", 0);
					cacheResult.put(each.getPayKey(), set);
				}
				result.putAll(cacheResult);
			}
		});
		//基础信息
		result.put("realName", member.getRealName());
		result.put("transactions", member.getTransactions() == null ? 0 : member.getTransactions());
		return MessageRespResult.success("", result);
	}

	
	
	@Override
	public MessageRespResult<JSONObject> queryMemberVerified(Member member) {
		member = this.iMemberApiService.getMember(member.getId()).getData();
		JSONObject setting = queryCapSettingByMember(member).getData();
		setting.put("username", member.getUsername());
		setting.put("createTime", member.getRegistrationTime());
		setting.put("id", member.getId());
		setting.put("emailVerified", StringUtils.isNotBlank(member.getEmail()));
		setting.put("email", member.getEmail());
		setting.put("mobilePhone", member.getMobilePhone());
		setting.put("fundsVerified", StringUtils.isNotBlank(member.getJyPassword()));
		setting.put("loginVerified", true);
		setting.put("realVerified", StringUtils.isNotBlank(member.getRealName()));
		setting.put("realAuditing", member.getRealNameStatus().equals(RealNameStatus.AUDITING));
		setting.put("avatar", member.getAvatar());
		setting.put("marginVerified", !"0".equals(member.getMargin()));
		
		setting.put("businessVerified", member.getCertifiedBusinessStatus().equals(CertifiedBusinessStatus.VERIFIED) ? true : false);
		return MessageRespResult.success("", setting);
	}



	@Override
	public MessageRespResult<Boolean> lockCapSetting(String payCode, String moneyPassword, String payKey,
			Member member) {
		MemberCapSetting mcs = this.getOne(new QueryWrapper<MemberCapSetting>().eq("member_id", member.getId()).eq("pay_key", payKey));
		if(mcs == null) {
			mcs = new MemberCapSetting();
			mcs.setCreateTime(new Date());
			mcs.setMemberId(member.getId());
			mcs.setPayKey(payKey);
		}
		else {
			// 判断是否还有未完成订单与已上架的广告
			this.advertiseService.checkOrderAndPutOn(member.getId());
		}
		mcs.setPayCode(payCode);
		this.saveOrUpdate(mcs);
		return MessageRespResult.success("", Boolean.TRUE);
	}

	@Override
	public MessageRespResult<Boolean> unLockCapSetting(String moneyPassword, String payKey, Member member) {
		MemberCapSetting mcs = this.getOne(new QueryWrapper<MemberCapSetting>().eq("member_id", member.getId()).eq("pay_key", payKey));
		AssertUtil.notNull(mcs, OtcExceptionMsg.UNLOCK_CAP_SETTING_NO);
		
		this.removeById(mcs.getId());
		return MessageRespResult.success("", Boolean.TRUE);
	}

}
