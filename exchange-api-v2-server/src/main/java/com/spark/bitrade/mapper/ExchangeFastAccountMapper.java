package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.ExchangeFastAccount;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 闪兑总账户配置访问层
 *
 * @author yangch
 * @since 2019-06-24 17:06:30
 */
public interface ExchangeFastAccountMapper extends BaseMapper<ExchangeFastAccount> {
    /**
     * 根据币种和应用ID获取闪兑总账户接口
     *
     * @param appId      必填，应用ID
     * @param coinSymbol 必填，闪兑币种，如BTC、LTC
     * @param baseSymbol 闪兑基币
     * @return
     */
    List<ExchangeFastAccount> findByAppIdAndCoinSymbol(
            @Param("appId") String appId, @Param("coinSymbol") String coinSymbol, @Param("baseSymbol") String baseSymbol);
}