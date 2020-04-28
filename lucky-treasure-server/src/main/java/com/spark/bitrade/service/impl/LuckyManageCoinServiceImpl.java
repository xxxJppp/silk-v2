package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.entity.LuckyManageCoin;
import com.spark.bitrade.mapper.LuckyManageCoinMapper;
import com.spark.bitrade.service.LuckyManageCoinService;
import com.spark.bitrade.service.LuckyNumberManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 幸运宝-对应币种表 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-16
 */
@Service
public class LuckyManageCoinServiceImpl extends ServiceImpl<LuckyManageCoinMapper, LuckyManageCoin> implements LuckyManageCoinService {

    @Autowired
    private LuckyNumberManagerService luckyNumberManagerService;

    @Override
    public List<LuckyManageCoin> bullsRank(Long actId) {
        QueryWrapper<LuckyManageCoin> qw=new QueryWrapper<>();
        qw.lambda().eq(LuckyManageCoin::getNumId,actId)
                .eq(LuckyManageCoin::getDeleteFlag, BooleanEnum.IS_FALSE)
                .orderByDesc(LuckyManageCoin::getIncrease);
        List<LuckyManageCoin> manageCoins = this.list(qw);
        luckyNumberManagerService.sortBulls(manageCoins);
        return manageCoins;
    }

    @Override
    public Optional<LuckyManageCoin> findByActIdAndCoin(Long actId, String coinUnit) {
        return baseMapper.findByActIdAndCoin(actId,coinUnit);
    }
}
