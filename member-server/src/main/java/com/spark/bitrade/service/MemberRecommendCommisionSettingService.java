package com.spark.bitrade.service;

import com.spark.bitrade.entity.MemberRecommendCommisionSetting;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 推荐层次折扣配置 服务类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
public interface MemberRecommendCommisionSettingService extends IService<MemberRecommendCommisionSetting> {

	public List<MemberRecommendCommisionSetting> initRecommendCommisionBy();
	
	public List<MemberRecommendCommisionSetting> getRecommentCommisionByMemberLevel(int levelId);
}
