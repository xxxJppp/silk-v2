package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.ExchangeCoin;
import com.spark.bitrade.mapper.ExchangeCoinMapper;
import com.spark.bitrade.service.ExchangeCoinService.ExchangeCoinService;
import com.spark.bitrade.util.SpringContextUtil;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 交易币种配置表服务实现类
 *
 * @author yangch
 * @since 2019-09-03 13:44:40
 */
@Service
public class ExchangeCoinServiceImpl extends ServiceImpl<ExchangeCoinMapper, ExchangeCoin> implements ExchangeCoinService {

    @Override
    public List<ExchangeCoin> getAllSymbol() {
        return this.baseMapper.getAllSymbol();
    }
}
