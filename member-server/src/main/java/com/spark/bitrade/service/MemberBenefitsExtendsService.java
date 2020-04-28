package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.MemberExtend;
import com.spark.bitrade.entity.MemberBenefitsExtends;
import com.spark.bitrade.param.PageParam;

/**
 * <p>
 * 会员扩展表，与原member表一对一 服务类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
public interface MemberBenefitsExtendsService extends IService<MemberBenefitsExtends> {

	public MemberExtend initMemberLevel(long memberId);

	MemberExtend getMemberExtendByMemberId(long memberId);
	MemberExtend getMemberExtendById(long memberExtendId);

	/**
	 * 获取会员等级
	 * @param memberId
	 * @return
	 */
	MemberBenefitsExtends getMemberrBenefitsByMemberId(Long memberId);


	/**
	 * 更新会员到期的人员
	 *
	 */
	void updatetMemberrBenefits();




	void updateMemberrBenefitsCache(MemberBenefitsExtends benefits);
	
	/**
	 * 获取账号上级会员等级
	 * @param memberId
	 * @return
	 * @author zhaopeng
	 * @since 2020年4月7日
	 */
	MemberBenefitsExtends getSuperiorAccountLevelId(Long memberId);
}
