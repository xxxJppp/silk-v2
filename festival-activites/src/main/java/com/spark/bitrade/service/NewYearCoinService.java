package com.spark.bitrade.service;

import com.spark.bitrade.entity.MemberTransaction;
import com.spark.bitrade.entity.NewYearCoin;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.trans.WalletTradeEntity;

/**
 * <p>
 * 奖励币种 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
public interface NewYearCoinService extends IService<NewYearCoin> {

	void addSendCost(Long id , BigDecimal cost);

	/**
	 * 新增流水
	 * @param entity
	 * @return
	 */
	int addTransaction(MemberTransaction entity);

	/**
	 * 变动余额
	 * @param walletId
	 * @param tradeBalance
	 * @param tradeFrozenBalance
	 * @param tradeLockBalance
	 * @return
	 */
	Integer trade( Long walletId,
				   BigDecimal tradeBalance,
				   BigDecimal tradeFrozenBalance,
				   BigDecimal tradeLockBalance);
}
