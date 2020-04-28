package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.SilkPayCoin;
import com.spark.bitrade.mapper.SilkPayCoinMapper;
import com.spark.bitrade.service.SilkPayCoinService;
import com.spark.bitrade.vo.MemberWalletVo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 币种配置(SilkPayCoin)表服务实现类
 *
 * @author wsy
 * @since 2019-07-29 14:22:25
 */
@Service("silkPayCoinService")
public class SilkPayCoinServiceImpl extends ServiceImpl<SilkPayCoinMapper, SilkPayCoin> implements SilkPayCoinService {


    @Override
    @Cacheable(cacheNames = "silkPayCoin", key = "'entity:SilkPayCoin:allAbleUnits'")
    public List<MemberWalletVo> findAllAbleUnits(){
        return this.baseMapper.findAllAbleUnits();
    }

    @Override
    public boolean checkCoinDailyMax(String unit, BigDecimal amount) {
        return this.baseMapper.checkCoinDailyMax(unit, amount) == 1;
    }
}