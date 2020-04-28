package com.spark.bitrade.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.alibaba.fastjson.JSONArray;
import com.spark.bitrade.constants.LSMsgCode;
import com.spark.bitrade.dto.Kline;
import com.spark.bitrade.service.ICoinExchange;
import com.spark.bitrade.service.SlpExchangeRateService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *  
 *
 * @author young
 * @time 2019.07.09 16:41
 */
@Slf4j
@Service
public class SlpExchangeRateServiceImpl implements SlpExchangeRateService {
    @Autowired
    private ICoinExchange coinExchange;

    /**
     * 创建缓存，默认60毫秒过期
     */
    TimedCache<String, Kline> timedCache = CacheUtil.newTimedCache(60 * 1000);

    @Override
    public Kline yesterdayKline(String symbol) {
        long time = this.yesterdayTime();
        String key = symbol.concat(String.valueOf(time));
        Kline kline = timedCache.get(key);

        if (kline == null) {
            JSONArray array = coinExchange.findKHistory(symbol, time, time, "1D");
            if (array.size() == 1) {
                ArrayList arrayList = (ArrayList) array.get(0);

                kline = new Kline();
                kline.setTime(Long.parseLong(arrayList.get(0).toString()));
                kline.setOpenPrice(new BigDecimal(arrayList.get(1).toString()));
                kline.setHighestPrice(new BigDecimal(arrayList.get(2).toString()));
                kline.setLowestPrice(new BigDecimal(arrayList.get(3).toString()));
                kline.setClosePrice(new BigDecimal(arrayList.get(4).toString()));

                timedCache.put(key, kline);
            }
        }

        return kline;
    }

    @Override
    public BigDecimal exchangeRate4Yesterday(String symbol) {
        Kline kline = this.yesterdayKline(symbol);
        if (kline == null) {
            return null;
        }

        return kline.getHighestPrice();
    }

    @Override
    public BigDecimal exchangeUsdtRate(String coinUit) {
        MessageRespResult<BigDecimal> respResult = coinExchange.getUsdExchangeRate(coinUit);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(respResult);
        AssertUtil.notNull(respResult.getData(), LSMsgCode.INVALID_EXCHANGE_RATE);
        AssertUtil.isTrue(BigDecimalUtil.gt0(respResult.getData()), LSMsgCode.INVALID_EXCHANGE_RATE);

        return respResult.getData();
    }

    /**
     * 获取昨天的时间戳
     *
     * @return
     */
    private Long yesterdayTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, -24);

        return calendar.getTimeInMillis();
    }
}
