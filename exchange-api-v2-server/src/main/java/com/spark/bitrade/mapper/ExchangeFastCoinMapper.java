package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.ExchangeFastCoin;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 闪兑币种配置(ExchangeFastCoin)表数据库访问层
 *
 * @author yangch
 * @since 2019-06-24 17:06:44
 */
public interface ExchangeFastCoinMapper extends BaseMapper<ExchangeFastCoin> {
    /**
     * 根据币种和应用ID获取闪兑币种配置接口
     *
     * @param appId      必填，应用ID
     * @param coinSymbol 必填，闪兑币种，如BTC、LTC
     * @return
     */
    ExchangeFastCoin findByAppIdAndCoinSymbol(@Param("appId") String appId,
                                                @Param("coinSymbol") String coinSymbol,
                                                @Param("baseSymbol") String baseSymbol);

    /**
     * 闪兑支持币种的列表接口
     *
     * @param appId      必填，应用ID
     * @param baseSymbol 基币
     * @return
     */
    List<ExchangeFastCoin> list4CoinSymbol(@Param("appId") String appId, @Param("baseSymbol") String baseSymbol);

    /**
     * 闪兑基币币种的列表接口
     *
     * @param appId 必填，应用ID
     * @return
     */
    List<String> list4BaseSymbol(@Param("appId") String appId);
}