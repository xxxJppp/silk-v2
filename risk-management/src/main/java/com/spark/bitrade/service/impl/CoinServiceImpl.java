package com.spark.bitrade.service.impl;

import com.spark.bitrade.entity.Coin;
import com.spark.bitrade.mapper.CoinMapper;
import com.spark.bitrade.service.CoinService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2020-03-11
 */
@Service
public class CoinServiceImpl extends ServiceImpl<CoinMapper, Coin> implements CoinService {

}
