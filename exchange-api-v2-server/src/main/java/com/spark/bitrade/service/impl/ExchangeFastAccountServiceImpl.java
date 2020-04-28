package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.mapper.ExchangeFastAccountMapper;
import com.spark.bitrade.entity.ExchangeFastAccount;
import com.spark.bitrade.service.ExchangeFastAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * exchange_fast_account(ExchangeFastAccount)表服务实现类
 *
 * @author yangch
 * @since 2019-06-24 17:06:30
 */
@Service("exchangeFastAccountService")
@Slf4j
public class ExchangeFastAccountServiceImpl extends ServiceImpl<ExchangeFastAccountMapper, ExchangeFastAccount> implements ExchangeFastAccountService {
    /**
     * 根据币种和应用ID获取闪兑总账户接口
     *
     * @param appId      必填，应用ID
     * @param coinSymbol 必填，闪兑币种，如BTC、LTC
     * @param baseSymbol 闪兑基币
     * @return
     */
    @Override
    @Cacheable(cacheNames = "exchangeFastAccount", key = "'entity:exchangeFastAccount:'+#appId+'-'+#coinSymbol+'-'+#baseSymbol")
    public ExchangeFastAccount findByAppIdAndCoinSymbol(String appId, String coinSymbol, String baseSymbol) {
        List<ExchangeFastAccount> lst = this.baseMapper.findByAppIdAndCoinSymbol(appId, coinSymbol, baseSymbol);

        if (lst.size() > 1) {
            //随机获取一个账户
            Random random = new Random();
            int randomInt = random.nextInt(lst.size());
            if (randomInt < 0 || randomInt > lst.size()) {
                log.warn("随机获取一个账户异常：randomInt={},size={}", randomInt, lst.size());
                randomInt = 0;
            }

            return lst.get(randomInt);
        } else if (lst.size() == 1) {
            return lst.get(0);
        }

        return null;
    }
}