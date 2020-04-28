package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.ExchangeFastAccount;

/**
 * exchange_fast_account(ExchangeFastAccount)表服务接口
 *
 * @author yangch
 * @since 2019-06-24 17:06:30
 */
public interface ExchangeFastAccountService extends IService<ExchangeFastAccount> {
    /**
     * 根据币种和应用ID获取闪兑总账户接口
     *
     * @param appId      必填，应用ID
     * @param coinSymbol 必填，闪兑币种，如BTC、LTC
     * @param baseSymbol 闪兑基币
     * @return
     */
    ExchangeFastAccount findByAppIdAndCoinSymbol(String appId, String coinSymbol, String baseSymbol);
}