package com.spark.bitrade.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.MemberBenefitsSetting;
import com.spark.bitrade.mapper.MemberBenefitsSettingMapper;
import com.spark.bitrade.service.MemberBenefitsSettingService;
import com.spark.bitrade.utils.KeyGenerator;

/**
 * <p>
 * 会员权益表 服务实现类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Service
public class MemberBenefitsSettingServiceImpl extends ServiceImpl<MemberBenefitsSettingMapper, MemberBenefitsSetting> implements MemberBenefitsSettingService {


	@Autowired
	private MemberBenefitsSettingMapper memberBenefitsSettingMapper;
	
	@Autowired
	private RedisTemplate redisTemplate;
	
    @Override
    public List<MemberBenefitsSetting> getBenefitsSettingList() {
        QueryWrapper<MemberBenefitsSetting> queryWrapper = new QueryWrapper<>();
        return this.baseMapper.selectList(queryWrapper);
    }

	@Override
	public MemberBenefitsSetting getBenefitsSettingByMemberLevel(int levelId) {
		//String k = KeyGenerator.getBenefitsSettingKey(levelId);
		
//		MemberBenefitsSetting  setting = (MemberBenefitsSetting) this.redisTemplate.opsForValue().get(k);
//		if(null == setting) {
		
			MemberBenefitsSetting  setting = this.memberBenefitsSettingMapper.getBenefitsSettingByMemberLevel(levelId);
//			this.redisTemplate.opsForValue().set(k, setting);
//		}
		return setting;
	}
}
