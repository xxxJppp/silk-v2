package com.spark.bitrade.service.impl;

import com.spark.bitrade.entity.NewYearStatics;
import com.spark.bitrade.mapper.NewYearStaticsMapper;
import com.spark.bitrade.service.NewYearStaticsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 领奖记录 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
@Service
public class NewYearStaticsServiceImpl extends ServiceImpl<NewYearStaticsMapper, NewYearStatics> implements NewYearStaticsService {

	@Override
	public void addSendAndLock(BigDecimal send, BigDecimal lock , String coin , String date) {
		this.baseMapper.addSendAndLock(send, lock, coin, date);
	}

	@Override
	public void addReleased(BigDecimal released,  String coin , String date) {
		this.baseMapper.addReleased(released, coin, date);
	}
}
