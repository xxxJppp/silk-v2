package com.spark.bitrade.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.spark.bitrade.config.BizCfg;
import com.spark.bitrade.mapper.MemberInviteMapper;
import com.spark.bitrade.service.MemberInviteService;
import com.spark.bitrade.utils.KeyGenerator;

@Service
public class MemberInviteServiceImpl  implements MemberInviteService {
	
	@Autowired
	private MemberInviteMapper memberInviteMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private BizCfg bizCfg;
	
	@Override
	public List<Long> getMemberInviteChainIdList(long memberId) {
		String key = KeyGenerator.getMemberInviteChainKey(memberId);
		
		List<Long> idList = this.redisTemplate.opsForList().range(key, 0, -1);
		List<Long> result = Lists.newArrayList();
		if(null == idList || idList.isEmpty()) {
			
			result = this.createMemberInviteChain(memberId);
			return result;
		} 
		return idList;
	}
	
	@Override
	public List<Long> createMemberInviteChain(long memberId) {
		String key = KeyGenerator.getMemberInviteChainKey(memberId);
		Long inviterId = memberId;
		List<Long> result = Lists.newArrayList();
		for(int count = 0; count < this.bizCfg.getDistributeLevel(); count++) {
		
			inviterId = this.memberInviteMapper.getInviterById(inviterId);
			if(null == inviterId) break;
		
			result.add(inviterId);
		}
		for (int index = 0; index < result.size(); index++) {
			this.redisTemplate.opsForList().rightPush(key, result.get(index));
		}
		return result;
	}
}
