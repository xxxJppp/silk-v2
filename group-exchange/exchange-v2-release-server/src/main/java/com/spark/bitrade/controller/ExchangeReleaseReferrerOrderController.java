package com.spark.bitrade.controller;

import com.spark.bitrade.service.ExchangeReleaseReferrerOrderService;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 币币交易-推荐人闪兑订单表(ExchangeReleaseReferrerOrder)控制层
 *
 * @author yangch
 * @since 2020-01-17 17:18:13
 */
@RestController
@RequestMapping("/service/v2/referrer")
@Api(description = "币币交易-推荐人闪兑订单表控制层")
public class ExchangeReleaseReferrerOrderController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private ExchangeReleaseReferrerOrderService exchangeReleaseReferrerOrderService;


    @ApiOperation(value = "完成闪兑", notes = "推荐人闪兑")
    @ApiImplicitParam(value = "关联订单ID", name = "orderId", dataTypeClass = String.class, required = true)
    @RequestMapping(value = "/exchange", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult exchange(@RequestParam("orderId") String orderId) {
        this.exchangeReleaseReferrerOrderService.exchange(orderId);
        return success();
    }
}