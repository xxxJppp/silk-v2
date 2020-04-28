package com.spark.bitrade.mapper;

import com.spark.bitrade.entity.MemberTransaction;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.entity.NewYearCoin;

import java.math.BigDecimal;

import com.spark.bitrade.trans.WalletTradeEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 奖励币种 Mapper 接口
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
public interface NewYearCoinMapper extends BaseMapper<NewYearCoin> {

	@Update("UPDATE new_year_coin SET cost_amount = cost_amount + #{cost} WHERE id = #{id}")
	void addSendCost(@Param("id")Long id , @Param("cost")BigDecimal cost);

	/**
	 * 新增资金流水
	 * @param entity
	 * @return
	 */
    int addTransaction(@Param("entity") MemberTransaction entity);

	/**
	 * 加减钱包余额
	 *
	 * @param walletId           钱包ID
	 * @param tradeBalance       交易余额，整数为加/负数为减
	 * @param tradeFrozenBalance 交易冻结余额，整数为加/负数为减
	 * @param tradeLockBalance   交易锁仓余额，整数为加/负数为减
	 * @return
	 */
	Integer trade(@Param("walletId") Long walletId,
				  @Param("tradeBalance") BigDecimal tradeBalance,
				  @Param("tradeFrozenBalance") BigDecimal tradeFrozenBalance,
				  @Param("tradeLockBalance") BigDecimal tradeLockBalance);


}
