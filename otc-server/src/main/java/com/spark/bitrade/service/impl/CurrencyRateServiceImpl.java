
package com.spark.bitrade.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.spark.bitrade.entity.CoinRateData;
import com.spark.bitrade.entity.CurrencyManage;
import com.spark.bitrade.entity.CurrencyRateData;
import com.spark.bitrade.entity.OtcCoin;
import com.spark.bitrade.enums.OtcExceptionMsg;
import com.spark.bitrade.mapper.CurrencyManageMapper;
import com.spark.bitrade.mapper.OtcCoinMapper;
import com.spark.bitrade.service.CurrencyManageService;
import com.spark.bitrade.service.CurrencyRateService;
import com.spark.bitrade.service.ICoinExchange;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 货币汇率换算
 *
 * @author lc
 * @date 2020/3/24 09:57
 */
@Service
@Slf4j
public class CurrencyRateServiceImpl implements CurrencyRateService {


    @Autowired
    private ICoinExchange coinExchange;

    @Resource
    private CurrencyManageService currencyManageService;

    @Resource
    private OtcCoinMapper otcCoinMapper;

    @Resource
    private CurrencyManageMapper currencyManageMapper;

    /**
     * 法币与交易币种汇率的小数位(结果向下舍)
     */
    private final int rateScale = 2;

    /**
     * 定义汇率换算的初始计价单位
     */
    private final String usdSymbol = "USD";

    /**
     * 缓存默认5分钟过期 timedCache
     */
    private TimedCache<String,Object> CurrencyRateListCache = CacheUtil.newTimedCache(5 * 60 * 1000);

    /**
     * 缓存默认5分钟过期 timedCache
     */
    private TimedCache<String,Object> CurrencyRateListCacheForUsd = CacheUtil.newTimedCache(5 * 60 * 1000);

    /**
     * 法币与交易币汇率存储
     */
    String currencyRateListCache = "rate:currencyRateListCache";
    /**
     * 平台支持法币与美元的汇率列表
     */
    String currencyRateListCacheForUSD = "rate:currencyRateListCacheForUsd";



    /**
     * 获取法币与交易币种的汇率(汇率接口统一计价单位为USD,如传入法币不是USD时需要额外做换算)
     * 如传入:CNY->BTC 则先获取到CNY与USD汇率，再获取USD与交易币种的汇率(USD计价),最后以USD与交易币种的汇率除以CNY与USD的汇率得到结果
     *
     * @param fSymbol 法币币种缩写
     * @param tSymbol 交易币缩写
     * @return 失败时返回汇率为0 成功时返回币种对应的法币价格
     */
    @Override
    public BigDecimal getCurrencyRate(String fSymbol, String tSymbol) {
        if("EURO".equals(fSymbol)){
            fSymbol = "EUR";
            if("EURT".equals(tSymbol)){
                return BigDecimal.ONE;
            }
        }
        log.info("获取汇率,法币币种:{},交易币种:{}", fSymbol, tSymbol);
        if("USDC".equals(tSymbol)){
            //如果交易币是USDC,直接返回USD汇率
            BigDecimal rate = getUsdcRate(fSymbol);
            return rate.compareTo(BigDecimal.ZERO) <= 0 ? rate : BigDecimalUtil.div2down(BigDecimal.ONE,rate,rateScale);
        }
        if("DCC".equals(tSymbol)){
            //DCC获取cny价格
            MessageRespResult<BigDecimal> dccPrice = coinExchange.getCnyExchangeRate("DCC");
            if(dccPrice.isSuccess()){
                return dccPrice.getData();
            }else {
                return BigDecimal.ZERO;
            }
        }
        BigDecimal currencyRate = BigDecimal.ZERO;
        try {
            //如果传入法币单位为USD则直接获取USD汇率
            if (fSymbol.equals(usdSymbol)) {
                //取得USD与交易币的汇率
                currencyRate = this.getRate(tSymbol);
                log.info("market——fUsdRate接口返回{}", currencyRate);
                return currencyRate.compareTo(BigDecimal.ZERO) <= 0 ? currencyRate : BigDecimalUtil.div2down(BigDecimal.ONE,currencyRate,rateScale);
            } else {
                //获取法币币种与USD汇率.
                BigDecimal baseToUsdrate = getUsdcRate(fSymbol);
                if (baseToUsdrate.compareTo(BigDecimal.ZERO) <= 0) {
                    log.error("法币币种与USD汇率汇率获取失败,请确认是否添加了新币种,未同步添加币种汇率或market接口无此汇率");
                }
                //获得USD和交易币的最新汇率
                BigDecimal fUsdRate = this.getRate(tSymbol);
                log.info("market——fUsdRate接口返回{}", fUsdRate);
                //USD与交易币汇率除以传入法币币种与USD的汇率
                currencyRate = BigDecimalUtil.div2down(baseToUsdrate, fUsdRate, rateScale);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return currencyRate.compareTo(BigDecimal.ZERO) <= 0 ? currencyRate : BigDecimalUtil.div2down(BigDecimal.ONE,currencyRate,rateScale);
    }

    @Override
    public BigDecimal getCurrencyPrice(Long fSymbol, String tSymbol) {
        CurrencyManage currencyManage = currencyManageService.getById(fSymbol);
        AssertUtil.notNull(currencyManage, OtcExceptionMsg.CURRENCY_NOT_EXIST);
        return getCurrencyRate(currencyManage.getUnit(),tSymbol);
    }


    /**
     *
     * 获取平台支持法币与交易币种的汇率
     * @return
     */
    @Override
    public Map<String, LinkedList<CurrencyRateData>> getCurrencyRateList() {
        Map<String, LinkedList<CurrencyRateData>> data = Maps.newHashMap();
        if(CurrencyRateListCache.isEmpty()){
            //交易币种列表
            List<OtcCoin> fSymbolList = otcCoinMapper.selectOpenOtcCoin();
            //法币币种列表
            QueryWrapper queryWrapper = new QueryWrapper<>().eq("currency_state","1");
            List<CurrencyManage>tSymbolList = currencyManageMapper.selectList(queryWrapper);

            CurrencyRateData currencyRateData;
            for (OtcCoin otcCoin : fSymbolList){
                LinkedList<CurrencyRateData> list = new LinkedList();
                for (CurrencyManage currencyManage : tSymbolList){
                    currencyRateData = CurrencyRateData.builder()
                            .symbol(currencyManage.getUnit()).price(this.getCurrencyRate(currencyManage.getUnit(),otcCoin.getUnit()))
                            .lastUpdated(System.currentTimeMillis()).build();
                    list.add(currencyRateData);
                }
                data.put(otcCoin.getUnit(),list);
            }
            CurrencyRateListCache.put(currencyRateListCache,data);
        }else{
            data = (Map<String, LinkedList<CurrencyRateData>>) CurrencyRateListCache.get(currencyRateListCache);
        }
        return data;
    }

    @Override
    public Map queryPriceList() {
        if(CurrencyRateListCacheForUsd.isEmpty()){
            //法币币种列表
            QueryWrapper queryWrapper = new QueryWrapper<>().eq("currency_state","1");
            List<CurrencyManage>tSymbolList = currencyManageMapper.selectList(queryWrapper);
            CoinRateData coinRateData;
            Map data = Maps.newHashMap();
            for (CurrencyManage currencyManage :tSymbolList){
                coinRateData = CoinRateData.builder().pair(currencyManage.getUnit()+"_USD").name(currencyManage.getName())
                        .symbol(currencyManage.getSymbol()).price_usd(BigDecimalUtil.div2down(new BigDecimal(1),getCurrencyRate(currencyManage.getUnit(),usdSymbol),8))
                        .lastUpdated(System.currentTimeMillis()).build();
                data.put(currencyManage.getUnit(),coinRateData);
            }
            CurrencyRateListCacheForUsd.put(currencyRateListCacheForUSD,data);
        }
        return (Map)CurrencyRateListCacheForUsd.get(currencyRateListCacheForUSD);
    }


    /**
     * 获取汇率
     *
     * @param coinUnit 币种
     * @return
     */
    protected BigDecimal getRate(String coinUnit) {
        return coinExchange.getUsdExchangeRate(coinUnit).getData();
    }

    /**
     * 获取法币币种与USD汇率.
     * @param fSymbol 法币缩写
     * @return
     */
    public BigDecimal getUsdcRate(String fSymbol){
        BigDecimal currencyRate = BigDecimal.ZERO;
        MessageRespResult result = coinExchange.toRate(fSymbol, usdSymbol, "");
        log.info("market接口返回{}", result);
        if (!result.isSuccess() || "0".equals(result.getData().toString())) {
            log.error("汇率接口调用失败,result{}", result);
            return currencyRate;
        }
        JSONObject jsonData = JSONObject.parseObject(JSONObject.toJSONString(result.getData()));
        BigDecimal baseToUsdrate = new BigDecimal(jsonData.getString("rate"));
        if (baseToUsdrate.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("法币币种与USD汇率汇率获取失败,请确认是否添加了新币种,未同步添加币种汇率或market接口无此汇率");
        }
        return baseToUsdrate;
    }


    /**
     * 获取传入币种与USDC的汇率
     *  平台market模块相关接口默认返回基于USDT的汇率，USDC与USD等值与USDT存在溢价 因此需要作转换才能使用
     * @param symbol BTC,USDT
     * @return
     */
    @Override
    public BigDecimal toUsdcRate(String symbol){
        String cnySymbol = "CNY";
        //与market模块达成约定 获取国际汇率时需传入G开头币种,否则将从平台币币交易区获取汇率
        String gCnySymbol = "GCNY";
        //获取CNY与USD的汇率,小数位不处理
        MessageRespResult result = coinExchange.toRate(cnySymbol, gCnySymbol, "");
        log.info("market汇率接口调用-获取CNY与USD价格,结果返回:{}", result);
        if (!result.isSuccess() || "0".equals(result.getData().toString())) {
            log.error("汇率接口调用失败,result{}", result);
            return BigDecimal.ZERO;
        }
        JSONObject jsonData = JSONObject.parseObject(JSONObject.toJSONString(result.getData()));
        BigDecimal cnyToUsdPrice = new BigDecimal(jsonData.getString("rate"));
        //获取平台中USD与传入币种的交易价格
        MessageRespResult<BigDecimal> cnyExchangeRate = coinExchange.getUsdExchangeRate(symbol);
        if(!cnyExchangeRate.isSuccess() && cnyExchangeRate.getData().compareTo(BigDecimal.ZERO) > 0){
            log.error("汇率接口调用失败,result{}", result);
            return BigDecimal.ZERO;
        }
        log.info("market汇率接口调用-获取CNY与USDT的价格,结果返回:{}", cnyExchangeRate);

        //除以溢价返回USD与指定币种的价格
        AssertUtil.notNull(cnyToUsdPrice,OtcExceptionMsg.USD_RATE_GET_FAILED);
        AssertUtil.isTrue(!BigDecimalUtil.eq0(cnyToUsdPrice),OtcExceptionMsg.USD_RATE_GET_FAILED);
        return  BigDecimalUtil.div2up(cnyExchangeRate.getData(),cnyToUsdPrice,8);

    }



}

