package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constants.ExchangeMsgCode;
import com.spark.bitrade.entity.ExchangeFastCoin;
import com.spark.bitrade.mapper.ExchangeFastCoinMapper;
import com.spark.bitrade.service.ExchangeFastCoinService;
import com.spark.bitrade.service.ICoinExchange;
import com.spark.bitrade.trans.ExchangeFastCoinRateInfo;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 闪兑币种配置(ExchangeFastCoin)表服务实现类
 *
 * @author yangch
 * @since 2019-06-24 17:06:44
 */
@Slf4j
@Service("exchangeFastCoinService")
public class ExchangeFastCoinServiceImpl extends ServiceImpl<ExchangeFastCoinMapper, ExchangeFastCoin> implements ExchangeFastCoinService {
    @Autowired
    private ICoinExchange coinExchange;

    @Override
    public boolean save(ExchangeFastCoin fastCoinDO) {
        fastCoinDO.setId(fastCoinDO.getBaseSymbol() + fastCoinDO.getCoinSymbol() + fastCoinDO.getAppId() + "");
        return SqlHelper.retBool(this.baseMapper.insert(fastCoinDO));
    }

    @Override
    @Cacheable(cacheNames = "exchangeFastCoin", key = "'entity:exchangeFastCoin:'+#appId+'-'+#coinSymbol")
    public ExchangeFastCoin findByAppIdAndCoinSymbol(String appId, String coinSymbol) {
        return this.baseMapper.findByAppIdAndCoinSymbol(appId, coinSymbol, null);
    }

    @Override
    @Cacheable(cacheNames = "exchangeFastCoin", key = "'entity:exchangeFastCoin:'+#appId+'-'" +
            "+#coinSymbol.toUpperCase()+'-'+#baseSymbol.toUpperCase()")
    public ExchangeFastCoin findByAppIdAndCoinSymbol(String appId, String coinSymbol, String baseSymbol) {
        //币种大小写兼容
        if (StringUtils.hasText(coinSymbol)) {
            coinSymbol = coinSymbol.toUpperCase();
        }
        if (StringUtils.hasText(baseSymbol)) {
            baseSymbol = baseSymbol.toUpperCase();
        }

        return this.baseMapper.findByAppIdAndCoinSymbol(appId, coinSymbol, baseSymbol);
    }

    @Override
    @Cacheable(cacheNames = "exchangeFastCoin", key = "'entity:exchangeFastCoin:list4Coin-'+#appId+'-'+#baseSymbol")
    public List<ExchangeFastCoin> list4CoinSymbol(String appId, String baseSymbol) {
        return this.baseMapper.list4CoinSymbol(appId, baseSymbol);
    }

    @Override
    @Cacheable(cacheNames = "exchangeFastCoin", key = "'entity:exchangeFastCoin:list4Base-'+#appId")
    public List<String> list4BaseSymbol(String appId) {
        return this.baseMapper.list4BaseSymbol(appId);
    }

    @Override
    public String getRateValidBaseSymbol(ExchangeFastCoin exchangeFastCoin) {
        return StringUtils.isEmpty(exchangeFastCoin.getRateReferenceBaseSymbol())
                ? exchangeFastCoin.getBaseSymbol() : exchangeFastCoin.getRateReferenceBaseSymbol();
    }

    @Override
    public String getRateValidCoinSymbol(ExchangeFastCoin exchangeFastCoin) {
        return StringUtils.isEmpty(exchangeFastCoin.getRateReferenceCoinSymbol())
                ? exchangeFastCoin.getCoinSymbol() : exchangeFastCoin.getRateReferenceCoinSymbol();
    }

    @Override
    public ExchangeFastCoinRateInfo calculateExchangeFastCoinRate(
            ExchangeFastCoin exchangeFastCoin, ExchangeOrderDirection direction) {

        AssertUtil.notNull(exchangeFastCoin, ExchangeMsgCode.NONSUPPORT_FAST_EXCHANGE_COIN);
        ExchangeFastCoinRateInfo exchangeFastCoinRate = new ExchangeFastCoinRateInfo();
        exchangeFastCoinRate.setDirection(direction);

        //获取固定汇率
        exchangeFastCoinRate.setBaseRate(exchangeFastCoin.getBaseSymbolFixedRate());
        exchangeFastCoinRate.setCoinRate(exchangeFastCoin.getCoinSymbolFixedRate());
        exchangeFastCoinRate.setBuyAdjustRate(exchangeFastCoin.getBuyAdjustRate());
        exchangeFastCoinRate.setSellAdjustRate(exchangeFastCoin.getSellAdjustRate());

        //如果固定汇率不存在，则从市场行情获取汇率
        if (!this.isValidFixedRate(exchangeFastCoinRate.getCoinRate())) {
            log.info("未配置coinSymbolFixedRate，从市场行情获取汇率");
            MessageRespResult<BigDecimal> result = coinExchange.getCnytExchangeRate(
                    this.getRateValidCoinSymbol(exchangeFastCoin));
            ExceptionUitl.throwsMessageCodeExceptionIfFailed(result);

            exchangeFastCoinRate.setCoinRate(result.getData());
            exchangeFastCoinRate.setFixedRate(false);
        }

        if (!this.isValidFixedRate(exchangeFastCoinRate.getBaseRate())) {
            log.info("未配置baseSymbolFixedRate，从市场行情获取汇率");
            MessageRespResult<BigDecimal> result = coinExchange.getCnytExchangeRate(
                    this.getRateValidBaseSymbol(exchangeFastCoin));

            ExceptionUitl.throwsMessageCodeExceptionIfFailed(result);

            exchangeFastCoinRate.setBaseRate(result.getData());
            exchangeFastCoinRate.setFixedRate(false);
        }


        /*if (direction == ExchangeOrderDirection.BUY) {
            //买入场景： 兑换基币币种 -> 接收币种
            //      基币(BT)=1 -> 兑换币(BTC)=4， 汇率计算 = currentPrice= BT/BTC=1/4=0.25
            if (BigDecimalUtil.gt0(exchangeFastCoinRate.getCoinRate())) {
                exchangeFastCoinRate.setRealtimeRate(BigDecimalUtil.div2down(
                        exchangeFastCoinRate.getBaseRate(),
                        exchangeFastCoinRate.getCoinRate(), 16));
                exchangeFastCoinRate.setTradeRate(
                        BigDecimalUtil.div2down(
                                exchangeFastCoinRate.getBaseRate().multiply(BigDecimal.ONE.subtract(exchangeFastCoin.getBuyAdjustRate())),
                                exchangeFastCoinRate.getCoinRate(), 16));
            }
        } else {
            //卖出场景：兑换币 ->基币
            //      兑换币(BTC)=4 -> 基币(BT)=1， 汇率计算 = currentPrice= BTC/BT=4/1=4
            if (BigDecimalUtil.gt0(exchangeFastCoinRate.getBaseRate())) {
                exchangeFastCoinRate.setRealtimeRate(BigDecimalUtil.div2down(
                        exchangeFastCoinRate.getCoinRate(),
                        exchangeFastCoinRate.getBaseRate(), 16));
                exchangeFastCoinRate.setTradeRate(
                        BigDecimalUtil.div2down(
                                exchangeFastCoinRate.getCoinRate().multiply(BigDecimal.ONE.subtract(exchangeFastCoin.getSellAdjustRate())),
                                exchangeFastCoinRate.getBaseRate(), 16));
            }
        }*/

        return exchangeFastCoinRate;
    }


    /**
     * 判断是否为有效的的固定汇率
     *
     * @param fixedRate 汇率
     * @return
     */
    private boolean isValidFixedRate(BigDecimal fixedRate) {
        if (StringUtils.isEmpty(fixedRate)) {
            return false;
        }
        if (fixedRate.compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        return false;
    }
}