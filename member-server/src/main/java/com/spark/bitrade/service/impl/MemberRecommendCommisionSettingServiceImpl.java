package com.spark.bitrade.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.MemberRecommendCommisionSetting;
import com.spark.bitrade.mapper.MemberRecommendCommisionSettingMapper;
import com.spark.bitrade.service.MemberRecommendCommisionSettingService;
import com.spark.bitrade.utils.KeyGenerator;


@Service
public class MemberRecommendCommisionSettingServiceImpl extends ServiceImpl<MemberRecommendCommisionSettingMapper, MemberRecommendCommisionSetting> implements MemberRecommendCommisionSettingService {

	@Autowired
	private RedisTemplate redisTemplate;
	
	@Autowired
	private MemberRecommendCommisionSettingMapper memberRecommendCommisionSettingMapper;
	
	public List<MemberRecommendCommisionSetting> initRecommendCommisionBy() {
		List<MemberRecommendCommisionSetting> list = super.list();
		
		Map<Integer, List<MemberRecommendCommisionSetting>> groupBy = list.stream().collect(Collectors.groupingBy(MemberRecommendCommisionSetting::getLevelId));
		for (Integer k : groupBy.keySet()) {
			List<MemberRecommendCommisionSetting> memberRecommendCommisionSetting = groupBy.get(k);
			String key = KeyGenerator.getRecommendCommisionKey(k);
			this.redisTemplate.opsForValue().set(key, memberRecommendCommisionSetting);
		}
		return list;
	}
	
	@Override
	public List<MemberRecommendCommisionSetting> getRecommentCommisionByMemberLevel(int levelId) {
		
//		String key = KeyGenerator.getRecommendCommisionKey(levelId);
//		List<MemberRecommendCommisionSetting> list = (List<MemberRecommendCommisionSetting>) this.redisTemplate.opsForValue().get(key);
//		if(null == list) {
			List<MemberRecommendCommisionSetting> list = this.memberRecommendCommisionSettingMapper.getCommisionByLevel(levelId);
//			if(null != list) {
//				this.redisTemplate.opsForValue().set(key, list);
//			}
//		}
		return list;
	}
}
