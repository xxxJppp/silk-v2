package com.spark.bitrade.controller;

import com.spark.bitrade.service.CurrencyRateService;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;


/**
 * 获取法币与交易币种的汇率_swagger测试
 *
 * @author lc
 */
@RestController
@RequestMapping("api/v2/otcCoin")
@Api(tags = "OTC汇率")
public class OtcCurrencyRateController {

    @Resource
    private CurrencyRateService rateService;


    @RequestMapping(value = {"/getCurrencyRate","/no-auth/getCurrencyRate"}, method = {RequestMethod.GET, RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(value = "法币币种缩写", name = "fSymbol", required = true),
            @ApiImplicitParam(value = "交易币种缩写", name = "tSymbol", required = true)
    })
    @ApiOperation(value = "获取法币与交易币种的汇率", tags = "OTC汇率接口")
    public MessageRespResult getCurrencyRate(@RequestParam("fSymbol") String fSymbol,
                                             @RequestParam("tSymbol") String tSymbol) {
        BigDecimal currencyRate = rateService.getCurrencyRate(fSymbol, tSymbol);
        if (currencyRate.compareTo(BigDecimal.ZERO) <= 0) {
            return MessageRespResult.error("未获取到汇率");
        }
        return MessageRespResult.success4Data(rateService.getCurrencyRate(fSymbol, tSymbol));
    }

    @RequestMapping(value = "/getCurrencyPrice", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(value = "法币币种ID", name = "fSymbol", required = true),
            @ApiImplicitParam(value = "交易币种缩写", name = "tSymbol", required = true)
    })
    @ApiOperation(value = "获取法币与交易币种的价格(内部接口)")
    public MessageRespResult getCurrencyPrice(@RequestParam("fSymbol") Long fSymbol,
                                             @RequestParam("tSymbol") String tSymbol) {
//        BigDecimal currencyRate = rateService.getCurrencyPrice(fSymbol, tSymbol);
//        if (currencyRate.compareTo(BigDecimal.ZERO) <= 0) {
//            return MessageRespResult.error("未获取到汇率");
//        }
        return MessageRespResult.success4Data(rateService.getCurrencyPrice(fSymbol, tSymbol));
    }


    @RequestMapping(value = "/getCurrencyRateList", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "获取平台支持的法币与交易币种汇率列表", tags = "OTC汇率接口")
    public MessageRespResult getCurrencyRateList() {
        return MessageRespResult.success4Data(rateService.getCurrencyRateList());
    }

    @RequestMapping(value = "/no-auth/usd/queryPriceList", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiOperation(value = "获取平台支持法币与美元的汇率列表", tags = "OTC汇率接口")
    public MessageRespResult queryPriceList() {
        return MessageRespResult.success4Data(rateService.queryPriceList());
    }

}
