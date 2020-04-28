package com.spark.bitrade.service;

import com.spark.bitrade.entity.NewYearStatics;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 领奖记录 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
public interface NewYearStaticsService extends IService<NewYearStatics> {

	void addSendAndLock(BigDecimal send , BigDecimal lock , String coin , String date);
	
	void addReleased(BigDecimal released,  String coin , String date);
}
