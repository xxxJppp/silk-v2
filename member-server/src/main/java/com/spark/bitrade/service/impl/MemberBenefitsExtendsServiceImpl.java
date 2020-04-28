package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.MemberExtend;
import com.spark.bitrade.entity.MemberBenefitsExtends;
import com.spark.bitrade.mapper.MemberBenefitsExtendsMapper;
import com.spark.bitrade.service.MemberBenefitsExtendsService;
import com.spark.bitrade.utils.KeyGenerator;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 会员扩展表，与原member表一对一 服务实现类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Service
@Slf4j
public class MemberBenefitsExtendsServiceImpl extends ServiceImpl<MemberBenefitsExtendsMapper, MemberBenefitsExtends> implements MemberBenefitsExtendsService {
	
	@Autowired
	private MemberBenefitsExtendsMapper memberBenefitsExtendsMapper;

	@Autowired
	private RedisTemplate redisTemplate;
	
	public MemberExtend getMemberExtendByMemberId(long memberId) {
//		String k = KeyGenerator.getMemberKey(memberId);
//		MemberExtend m = (MemberExtend) this.redisTemplate.opsForValue().get(k);
//		if(null == m) {
		MemberExtend m = this.memberBenefitsExtendsMapper.getMemberBenefitsExtends(memberId);
//			this.redisTemplate.opsForValue().set(k, m);
//		}
		return m;
	}

    @Override
    public MemberExtend initMemberLevel(long memberId) {
        return null;
    }

    @Override
	public MemberBenefitsExtends getMemberrBenefitsByMemberId(Long memberId ) {
//		MemberBenefitsExtends benefitsExtends = null;
//		String k = KeyGenerator.getmemberBenefitsExtend(memberId);
//		benefitsExtends = (MemberBenefitsExtends) this.redisTemplate.opsForValue().get(k);
//		if (null == benefitsExtends) {
    	MemberBenefitsExtends benefitsExtends = getOne(new QueryWrapper<MemberBenefitsExtends>().lambda().eq(MemberBenefitsExtends::getMemberId, memberId));
//			if (benefitsExtends != null) {
//				this.redisTemplate.opsForValue().set(k, benefitsExtends, 24, TimeUnit.HOURS);
//			}
//		}
		return benefitsExtends;
	}

	@Override
	public void updatetMemberrBenefits() {
		List<MemberExtend> memberExtends = memberBenefitsExtendsMapper.getBenefitsExtendsLessThanEndTime();
		log.info("=========== 符合条件 ============ {}", memberExtends);
		if (memberExtends != null && memberExtends.size() > 0) {
			for (MemberExtend extend : memberExtends) {
				memberBenefitsExtendsMapper.cheakBenefitsExtendsById(extend.getId());
				String key1 = KeyGenerator.getmemberBenefitsExtend(extend.getMemberId());
				String key2 = KeyGenerator.getMemberExtendKey(extend.getId());
				String key3 = KeyGenerator.getMemberOpenVipAoumt(extend.getMemberId());
				redisTemplate.delete(key1);
				redisTemplate.delete(key2);
				redisTemplate.delete(key3);
			}
		}

	}

	@Override
	public void updateMemberrBenefitsCache(MemberBenefitsExtends benefits) {
		String k = KeyGenerator.getmemberBenefitsExtend(benefits.getMemberId());
		redisTemplate.delete(k);
		this.redisTemplate.opsForValue().set(k, benefits);
	}

	@Override
	public MemberExtend getMemberExtendById(long memberExtendId) {
//		String k = KeyGenerator.getMemberExtendKey(memberExtendId);
//		MemberExtend m = (MemberExtend) this.redisTemplate.opsForValue().get(k);
//		if (null == m) {
		MemberExtend	m = this.memberBenefitsExtendsMapper.getMemberExtend(memberExtendId);
//			this.redisTemplate.opsForValue().set(k, m);
//		}
		return m;
	}

	@Override
	public MemberBenefitsExtends getSuperiorAccountLevelId(Long memberId) {
		return this.memberBenefitsExtendsMapper.getSuperiorAccountLevelId(memberId);
	}

}
