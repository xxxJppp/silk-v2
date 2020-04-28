package com.spark.bitrade.service;

import com.spark.bitrade.entity.MemberFeeDayStat;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员购买情况日统计 服务类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
public interface MemberFeeDayStatService extends IService<MemberFeeDayStat> {

	public boolean updateDailyStat(MemberFeeDayStat stat);
	
	public MemberFeeDayStat getMemberFeeDayStatByDay(String day);
}
