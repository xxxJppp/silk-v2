package com.spark.bitrade.controller.internal;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.entity.ExchangeFastCoin;
import com.spark.bitrade.entity.ExchangeFastOrder;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.service.ExchangeFastCoinService;
import com.spark.bitrade.service.ExchangeFastOrderService;
import com.spark.bitrade.trans.ExchangeFastCoinRateInfo;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 闪兑订单(内部接口)控制层
 *
 * @author yangch
 * @time 2019.06.26 13:31
 */
@RestController
@RequestMapping("/internal/fast")
@Api(description = "闪兑订单(内部接口)控制层")
@Slf4j
public class InternalExchangeFastOrderController {
    @Autowired
    private ExchangeFastCoinService fastCoinService;
    @Autowired
    private ExchangeFastOrderService fastOrderService;

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
     * @param coinSymbolRate 可选，兑换币汇率
     * @param baseSymbolRate 可选，基币汇率
     * @return
     */
    @ApiOperation(value = "闪兑下单接口", notes = "闪兑下单接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "会员ID", name = "memberId", required = true),
            @ApiImplicitParam(value = "应用ID", name = "appId", required = true),
            @ApiImplicitParam(value = "闪兑币种名称，如BTC、LTC", name = "coinSymbol", required = true),
            @ApiImplicitParam(value = "闪兑基币币种名称，如CNYT、BT", name = "baseSymbol", required = true),
            @ApiImplicitParam(value = "闪兑数量", name = "targetAmout", required = true),
            @ApiImplicitParam(value = "订单方向:0=买入(闪兑基币->闪兑币)/1=卖出(闪兑币->闪兑基币)", name = "direction", required = true),
            @ApiImplicitParam(value = "兑换币汇率", name = "coinSymbolRate", dataType = "double"),
            @ApiImplicitParam(value = "基币汇率", name = "baseSymbolRate", dataType = "double")
    })
    @RequestMapping(value = "/exchangeTargetAmoutAndRate",
            method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeFastOrder> exchangeTargetAmoutAndRate(@MemberAccount Member member,
                                                                           String appId,
                                                                           String coinSymbol,
                                                                           String baseSymbol,
                                                                           BigDecimal targetAmout,
                                                                           ExchangeOrderDirection direction,
                                                                           BigDecimal coinSymbolRate,
                                                                           BigDecimal baseSymbolRate) {
        // 验证币种是否支持闪兑
        ExchangeFastCoin exchangeFastCoin = fastCoinService.findByAppIdAndCoinSymbol(appId, coinSymbol, baseSymbol);

        //获取兑换汇率，并更新为指定的汇率
        ExchangeFastCoinRateInfo rateInfo = fastCoinService.calculateExchangeFastCoinRate(exchangeFastCoin, direction);
        if (BigDecimalUtil.gt0(coinSymbolRate)) {
            rateInfo.setCoinRate(coinSymbolRate);
        }
        if (BigDecimalUtil.gt0(baseSymbolRate)) {
            rateInfo.setBaseRate(baseSymbolRate);
        }

        ExchangeFastOrder order = fastOrderService.exchangeInitiator(member.getId(), appId, coinSymbol,
                baseSymbol, targetAmout, direction, rateInfo, true);

        return MessageRespResult.success4Data(order);
    }
}
