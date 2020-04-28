package com.spark.bitrade.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.google.common.base.Strings;
import com.spark.bitrade.mapper.GlobalConfMapper;
import com.spark.bitrade.service.GlobalConfService;
import com.spark.bitrade.utils.KeyGenerator;

@Service
public class GlobalConfServiceImpl implements GlobalConfService {

	
	@Autowired
	private RedisTemplate redisTemplate;
	
	
	@Autowired
	private GlobalConfMapper globalConfMapper;
	
//	@Value("${commision.token.exchange.unit}")
//	private String tokenExchangeCommisionUnit;
//	
//	@Value("${commision.member.recommend.unit}")
//	private String memberRecommendCommisionUnit;
	
	
//	public PlatformToken getPlatformToken() {
//		
//		
//		String k = KeyGenerator.getPlatformTokenKey();
//		
//		PlatformToken pt = (PlatformToken) this.redisTemplate.opsForValue().get(k);
//		if(null == pt || Strings.isNullOrEmpty(pt.getName())) {
//			String platformToken = this.globalConfMapper.getPlatformToken();
//		
//			PlatformToken token =  new PlatformToken();
//			token.setName(platformToken);
//			
//			this.redisTemplate.opsForValue().set(k, token);
//			return token;
//		}
//		return pt;
//	}
	
//	public String getTokenExchangeCommisionUnit() {
//		return tokenExchangeCommisionUnit;
//	}
//	public void setTokenExchangeCommisionUnit(String tokenExchangeCommisionUnit) {
//		this.tokenExchangeCommisionUnit = tokenExchangeCommisionUnit;
//	}
//	public void setMemberRecommendCommisionUnit(String memberRecommendCommisionUnit) {
//		this.memberRecommendCommisionUnit = memberRecommendCommisionUnit;
//	}
	@Override
	public String getMemberRecommendCommisionUnit() {
		String k = KeyGenerator.getMemberRecommendCommisionUnitKey();
		
		String token = (String) this.redisTemplate.opsForValue().get(k);
		if(Strings.isNullOrEmpty(token)) {
			String v = this.globalConfMapper.getMemberRecommendCommisionUnit();
			if(Strings.isNullOrEmpty(v)) {
				throw new RuntimeException("member recommend commision unit not found, check db config ....");
			}
			this.redisTemplate.opsForValue().set(k, v);
			return v;
		}
		return token;
	}
	@Override
	public String getTokenExchangeFeeCommisionUnit() {
		String k = KeyGenerator.getTokenExchangeFeeCommisionUnitKey();
		
		String token = (String) this.redisTemplate.opsForValue().get(k);
		if(Strings.isNullOrEmpty(token)) {
			String v = this.globalConfMapper.getTokenExchangeFeeCommisionUnit();
			if(Strings.isNullOrEmpty(v)) {
				throw new RuntimeException("token exchange commision unit not found, check db config....");
			}
			this.redisTemplate.opsForValue().set(k, v);
			return v;
		}
		return token;
	}
	
	@Override
	public String getMemberCommisionDistributeStatus() {
		return this.globalConfMapper.getMemberCommisionDistributeStatus();
	}
	
	@Override
	public BigDecimal getCommisionTuneRate() {
		BigDecimal rate =  this.globalConfMapper.getCommisionTuneRate();
		if(null == rate) rate = new BigDecimal(1.0001);
		return rate;
	}
}
