package com.spark.bitrade.service;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.entity.ExchangeFastOrder;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * 提供 闪兑 API服务
 *
 * @author yangch
 * @time 2019-06-26 13:27:24
 */
@FeignClient(FeignServiceConstant.EXCHANGE_API2_SERVER)
public interface IExchangeFastApiService {

    /**
     * 闪兑接口
     *
     * @param appId      应用ID
     * @param coinSymbol 闪兑币种名称
     * @param baseSymbol 基币名称
     * @param amount     闪兑数量
     * @param direction  兑换方向
     * @return
     */
    @PostMapping(value = "/exchange-api2/internal/fast/exchange")
    MessageRespResult<ExchangeFastOrder> exchange(@RequestHeader("apiKey") String apiKey,
                                                  @RequestHeader("appId") String appId,
                                                  @RequestParam("coinSymbol") String coinSymbol,
                                                  @RequestParam("baseSymbol") String baseSymbol,
                                                  @RequestParam("amount") BigDecimal amount,
                                                  @RequestParam("direction") ExchangeOrderDirection direction);

    /**
     * 闪兑下单接口
     * <p>
     * 备注：兑换为指定币种的数量
     *
     * @param appId          应用ID
     * @param coinSymbol     闪兑币种名称
     * @param baseSymbol     基币名称
     * @param targetAmout    闪兑目标数量
     * @param direction      兑换方向
     * @param coinSymbolRate 兑换币汇率
     * @param baseSymbolRate 基币汇率
     * @return
     */
    @PostMapping(value = "/exchange-api2/internal/fast/exchangeTargetAmoutAndRate")
    MessageRespResult<ExchangeFastOrder> exchangeTargetAmoutAndRate(@RequestHeader("apiKey") String apiKey,
                                                                    @RequestParam("appId") String appId,
                                                                    @RequestParam("coinSymbol") String coinSymbol,
                                                                    @RequestParam("baseSymbol") String baseSymbol,
                                                                    @RequestParam("targetAmout") BigDecimal targetAmout,
                                                                    @RequestParam("direction") ExchangeOrderDirection direction,
                                                                    @RequestParam("coinSymbolRate") BigDecimal coinSymbolRate,
                                                                    @RequestParam("baseSymbolRate") BigDecimal baseSymbolRate);
}
