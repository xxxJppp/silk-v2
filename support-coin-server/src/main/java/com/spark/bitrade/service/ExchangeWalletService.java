package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.ExchangeWallet;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.service.impl.ExchangeWalletServiceImpl;
import com.spark.bitrade.vo.MembertVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 用户币币账户 服务类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-26
 */
public interface ExchangeWalletService extends IService<ExchangeWallet> {

    BigDecimal countExchangeWalletByCoinUnit(Long memberId,String coinUnit,BigDecimal start,BigDecimal end);

    IPage<MembertVo> findExchangeWalletChicangMembers(Long memberId,String coinUnit, BigDecimal start, BigDecimal end, IPage page);
}
