package com.spark.bitrade.controller;


import com.spark.bitrade.service.CywWalletOperations;
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
 * 机器人钱包(CywWallet)控制层
 *
 * @author yangch
 * @since 2019-09-02 14:42:41
 */
@RestController
@RequestMapping("/service/v1/cywWallet")
@Api(description = "机器人钱包控制层")
public class CywWalletServiceController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private CywWalletOperations cywWalletOperations;


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
    public MessageRespResult<BigDecimal> balance(@RequestParam("memberId") Long memberId, @RequestParam("coinUnit") String coinUnit) {
        Optional<BigDecimal> optional = this.cywWalletOperations.balance(memberId, coinUnit);
        if (optional.isPresent()) {
            return success(optional.get());
        }
        return failed();
    }

}