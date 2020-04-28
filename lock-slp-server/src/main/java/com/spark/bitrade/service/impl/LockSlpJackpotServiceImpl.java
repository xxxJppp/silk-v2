package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.LockSlpJackpot;
import com.spark.bitrade.mapper.LockSlpJackpotMapper;
import com.spark.bitrade.service.LockSlpJackpotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 大乐透总奖池表(LockSlpJackpot)表服务实现类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@Slf4j
@Service("lockSlpJackpotService")
public class LockSlpJackpotServiceImpl extends ServiceImpl<LockSlpJackpotMapper, LockSlpJackpot> implements LockSlpJackpotService {


    @Override
    public boolean add(String coinUnit, BigDecimal jackpotAmount) {
        QueryWrapper<LockSlpJackpot> query = new QueryWrapper<>();
        query.eq("coin_unit", coinUnit);

        LockSlpJackpot jackpot = getOne(query);

        if (jackpot == null) {
            log.error("未初始化币种奖池数据 [ coin_unit = '{}' ]", coinUnit);
            // throw new MessageCodeException()
            throw new RuntimeException(String.format("币种 ['%s'] 奖池数据未初始化", coinUnit));
        }

        return baseMapper.updateJackpotAmount(jackpot.getId(), coinUnit, jackpotAmount) > 0;
    }
}