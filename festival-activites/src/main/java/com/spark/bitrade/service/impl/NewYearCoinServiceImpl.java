package com.spark.bitrade.service.impl;

import com.spark.bitrade.entity.MemberTransaction;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.entity.NewYearCoin;
import com.spark.bitrade.mapper.NewYearCoinMapper;
import com.spark.bitrade.service.NewYearCoinService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.math.BigDecimal;

import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 奖励币种 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
@Service
public class NewYearCoinServiceImpl extends ServiceImpl<NewYearCoinMapper, NewYearCoin> implements NewYearCoinService {

    @Override
    public void addSendCost(Long id, BigDecimal cost) {
        this.baseMapper.addSendCost(id, cost);
    }

    @Override
    public int addTransaction(MemberTransaction entity) {
        return baseMapper.addTransaction(entity);
    }

    @Override
    public Integer trade(Long walletId,
                         BigDecimal tradeBalance,
                         BigDecimal tradeFrozenBalance,
                         BigDecimal tradeLockBalance) {
        return baseMapper.trade(walletId, tradeBalance, tradeFrozenBalance, tradeLockBalance);
    }


}
