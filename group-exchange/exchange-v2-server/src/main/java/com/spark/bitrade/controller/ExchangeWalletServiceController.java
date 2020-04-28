package com.spark.bitrade.controller;


import com.spark.bitrade.entity.ExchangeWallet;
import com.spark.bitrade.service.ExchangeWalletOperations;
import com.spark.bitrade.service.ExchangeWalletService;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * 币币交易钱包(ExchangeWallet)控制层
 *
 * @author yangch
 * @since 2019-09-02 14:42:41
 */
@RestController
@RequestMapping("/service/v1/exchangeWallet")
@Api(description = "币币交易钱包控制层")
public class ExchangeWalletServiceController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private ExchangeWalletOperations exchangeWalletOperations;
    @Resource
    private ExchangeWalletService exchangeWalletService;


    /**
     * 获取指定币种的可用余额
     *
     * @param memberId 用户ID
     * @param coinUnit 币种
     * @return 单条数据
     */
    @ApiOperation(value = "获取指定币种的可用余额数据接口", notes = "获取指定币种的可用余额数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int", required = true),
            @ApiImplicitParam(value = "币种", name = "coinUnit", dataTypeClass = String.class, required = true)
    })
    @RequestMapping(value = "/balance", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeWallet> balance(@RequestParam("memberId") Long memberId, @RequestParam("coinUnit") String coinUnit) {
        Optional<ExchangeWallet> optional = this.exchangeWalletOperations.balance(memberId, coinUnit);
        if (optional.isPresent()) {
            return success(optional.get());
        }
        return failed();
    }

    /**
     * 重置指定账户和币种的余额及签名数据
     * url: exchange2/service/v1/exchangeWallet/reset?memberId=&coinUnit=
     *
     * @param memberId 用户ID
     * @param coinUnit 币种
     * @return 单条数据
     */
    @ApiOperation(value = "重置指定账户和币种的余额及签名数据接口", notes = "重置指定账户和币种的余额及签名数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int", required = true),
            @ApiImplicitParam(value = "币种", name = "coinUnit", dataTypeClass = String.class, required = true)
    })
    @RequestMapping(value = "/reset", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<Boolean> reset(@RequestParam("memberId") Long memberId, @RequestParam("coinUnit") String coinUnit) {
        return success(this.exchangeWalletService.reset(memberId, coinUnit, BigDecimal.ZERO, BigDecimal.ZERO));
    }
}