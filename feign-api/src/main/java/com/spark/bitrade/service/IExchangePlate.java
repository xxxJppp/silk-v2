package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.entity.TradePlateItem;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 币种盘口接口
 * 备注：从market获取汇率
 *
 * @author yangch
 * @time 2019-06-18 09:24:28
 */
@FeignClient(FeignServiceConstant.MARKET_SERVER)
public interface IExchangePlate {

    String URL_PREFIX = "/market/api/v1/";


    /**
     * 买1盘口数据
     *
     * @param symbol 交易对，可忽略大小写。eg：BTC/USDT
     * @return
     */
    @RequestMapping(URL_PREFIX + "exchange-plate/buy1")
    MessageRespResult<TradePlateItem> tradePlateBuy1(@RequestParam("symbol") String symbol);

    /**
     * 卖1盘口数据
     *
     * @param symbol 交易对，可忽略大小写。eg：BTC/USDT
     * @return
     */
    @RequestMapping(URL_PREFIX + "exchange-plate/sell1")
    MessageRespResult<TradePlateItem> tradePlateSell1(@RequestParam("symbol") String symbol);

}
