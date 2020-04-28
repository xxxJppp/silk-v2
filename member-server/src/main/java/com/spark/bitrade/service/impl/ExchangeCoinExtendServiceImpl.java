package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.entity.ExchangeCoinExtend;
import com.spark.bitrade.mapper.ExchangeCoinExtendMapper;
import com.spark.bitrade.service.ExchangeCoinExtendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 币币交易-交易对扩展表 服务实现类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
@Service("exchangeCoinExtendService")
public class ExchangeCoinExtendServiceImpl extends ServiceImpl<ExchangeCoinExtendMapper, ExchangeCoinExtend> implements ExchangeCoinExtendService {

    @Override
    public ExchangeCoinExtend getOneExchangeCoinExtend(String symbol) {
        QueryWrapper<ExchangeCoinExtend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ExchangeCoinExtend.SYMBOL, symbol);
        return this.baseMapper.selectOne(queryWrapper);
    }
}
