package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.entity.ExchangeFastCoin;
import com.spark.bitrade.trans.ExchangeFastCoinRateInfo;

import java.util.List;

/**
 * 闪兑币种配置(ExchangeFastCoin)表服务接口
 *
 * @author yangch
 * @since 2019-06-24 17:06:44
 */
public interface ExchangeFastCoinService extends IService<ExchangeFastCoin> {
    boolean save(ExchangeFastCoin fastCoinDO);

    /**
     * 根据币种和应用ID获取闪兑币种配置接口
     *
     * @param appId      必填，应用ID
     * @param coinSymbol 必填，闪兑币种，如BTC、LTC
     * @return
     */
    ExchangeFastCoin findByAppIdAndCoinSymbol(String appId, String coinSymbol);

    /**
     * 根据币种和应用ID获取闪兑币种配置接口
     *
     * @param appId      必填，应用ID
     * @param coinSymbol 必填，闪兑币种，如BTC、LTC
     * @param baseSymbol 闪兑基币
     * @return
     */
    ExchangeFastCoin findByAppIdAndCoinSymbol(String appId, String coinSymbol, String baseSymbol);

    /**
     * 闪兑支持币种的列表接口
     *
     * @param appId      必填，应用ID
     * @param baseSymbol 闪兑基币
     * @return
     */
    List<ExchangeFastCoin> list4CoinSymbol(String appId, String baseSymbol);

    /**
     * 闪兑基币币种的列表接口
     *
     * @param appId 必填，应用ID
     * @return
     */
    List<String> list4BaseSymbol(String appId);

    /**
     * 从配置中获取有效的基币汇率币种
     *
     * @param exchangeFastCoin 币种配置
     * @return 有效汇率的币种
     */
    String getRateValidBaseSymbol(ExchangeFastCoin exchangeFastCoin);

    /**
     * 从配置中获取有效的闪兑汇率币种
     *
     * @param exchangeFastCoin 币种配置
     * @return 有效汇率的币种
     */
    String getRateValidCoinSymbol(ExchangeFastCoin exchangeFastCoin);

    /**
     * 计算闪兑汇率
     *
     * @param exchangeFastCoin 闪兑币种配置信息
     * @param direction        交易方向
     * @return
     */
    ExchangeFastCoinRateInfo calculateExchangeFastCoinRate(ExchangeFastCoin exchangeFastCoin, ExchangeOrderDirection direction);
}