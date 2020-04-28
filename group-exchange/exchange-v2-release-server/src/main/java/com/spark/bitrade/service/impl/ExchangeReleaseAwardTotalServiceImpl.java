package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.spark.bitrade.mapper.ExchangeReleaseAwardTotalMapper;
import com.spark.bitrade.entity.ExchangeReleaseAwardTotal;
import com.spark.bitrade.service.ExchangeReleaseAwardTotalService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 币币交易-推荐人累计买入奖励累计表(ExchangeReleaseAwardTotal)表服务实现类
 *
 * @author yangch
 * @since 2020-02-05 17:31:07
 */
@Service("exchangeReleaseAwardTotalService")
public class ExchangeReleaseAwardTotalServiceImpl extends ServiceImpl<ExchangeReleaseAwardTotalMapper, ExchangeReleaseAwardTotal> implements ExchangeReleaseAwardTotalService {

    @Override
    public boolean addTotalBuyAmount(long memberId, BigDecimal buyAmount) {
        return SqlHelper.retBool(this.baseMapper.addTotalBuyAmount(memberId, buyAmount));
    }

    @Override
    public boolean updateAward(long memberId, BigDecimal buyAmount, BigDecimal awardAmount, int awardTimes, BigDecimal minTotalBuyAmount) {
        return SqlHelper.retBool(this.baseMapper.updateAward(memberId, buyAmount, awardAmount, awardTimes, minTotalBuyAmount));
    }
}