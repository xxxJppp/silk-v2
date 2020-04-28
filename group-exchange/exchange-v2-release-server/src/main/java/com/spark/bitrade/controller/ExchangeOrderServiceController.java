package com.spark.bitrade.controller;


import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeTrade;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 币币交易订单服务控制层(内部服务，不对外)
 *
 * @author yangch
 * @since 2019-09-02 11:23:46
 */
@RestController
@RequestMapping("/service/v1/order")
@Api(description = "币币交易订单服务控制层")
@Slf4j
public class ExchangeOrderServiceController extends ApiController {
    @Autowired
    private ExchangePlaceOrderService exchangePlaceOrderService;
    @Autowired
    private SellService sellService;

    @Resource
    private ExchangeTradeService tradeService;
    @Resource
    private OrderFacadeService orderService;
    @Autowired
    private ExchangeRedoService redoService;



    /**
     * 委托订单
     *
     * @param memberId     会员ID
     * @param direction    交易方式：买币、卖币
     * @param symbol       交易对
     * @param price        委托价格
     * @param amount       委托数量
     * @param type         订单类型：市价、限价
     * @param tradeCaptcha 交易验证码
     * @return
     */
    @ApiOperation(value = "委托订单接口", notes = "委托订单接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int", required = true),
            @ApiImplicitParam(value = "交易方向", name = "direction", dataTypeClass = ExchangeOrderDirection.class),
            @ApiImplicitParam(value = "交易对", name = "symbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "委托价格", name = "price", dataTypeClass = BigDecimal.class, required = true),
            @ApiImplicitParam(value = "委托数量", name = "amount", dataTypeClass = BigDecimal.class, required = true),
            @ApiImplicitParam(value = "订单类型", name = "type", dataTypeClass = ExchangeOrderType.class),
            @ApiImplicitParam(value = "交易验证码", name = "tradeCaptcha", dataTypeClass = ExchangeOrderType.class)
    })
    @RequestMapping(value = "/place", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeOrder> placeOrder(@RequestParam("memberId") Long memberId,
                                                       @RequestParam("direction") ExchangeOrderDirection direction,
                                                       @RequestParam("symbol") String symbol,
                                                       @RequestParam("price") BigDecimal price,
                                                       @RequestParam("amount") BigDecimal amount,
                                                       @RequestParam("type") ExchangeOrderType type,
                                                       @RequestParam(value = "tradeCaptcha", required = false) String tradeCaptcha) {
        return exchangePlaceOrderService.place(memberId, direction, symbol, price, amount, type, tradeCaptcha);
    }

    /**
     * 处理交易明细的买单
     *
     * @param trade 交易明细
     * @return
     */
    @ApiOperation(value = "处理交易明细的买单接口", notes = "处理交易明细的买单接口")
    @ApiImplicitParam(value = "交易明细", name = "trade", dataTypeClass = ExchangeTrade.class)
    @RequestMapping(value = "/tradeBuy", method = {RequestMethod.POST})
    public MessageRespResult<ExchangeOrder> tradeBuy(@RequestBody ExchangeTrade trade) {
        return success(this.tradeService.processTrade(trade, ExchangeOrderDirection.BUY));
    }


    /**
     * 处理交易明细的卖单
     *
     * @param trade 交易明细
     * @return
     */
    @ApiOperation(value = "处理交易明细的卖单接口", notes = "处理交易明细的卖单接口")
    @ApiImplicitParam(value = "交易明细", name = "trade", dataTypeClass = ExchangeTrade.class)
    @RequestMapping(value = "/tradeSell", method = {RequestMethod.POST})
    public MessageRespResult<ExchangeOrder> tradeSell(@RequestBody ExchangeTrade trade) {
        return success(this.tradeService.processTrade(trade, ExchangeOrderDirection.SELL));
    }

    /**
     * 完成订单的处理
     *
     * @param memberId     用户ID
     * @param orderId      订单号
     * @param tradedAmount 成交数量
     * @param turnover     成交额
     * @return
     */
    @ApiOperation(value = "完成订单的处理接口", notes = "完成订单的处理接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int", required = true),
            @ApiImplicitParam(value = "订单号", name = "orderId", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(value = "成交数量", name = "tradedAmount", dataTypeClass = BigDecimal.class, required = true),
            @ApiImplicitParam(value = "成交额", name = "turnover", dataTypeClass = BigDecimal.class, required = true)
    })
    @RequestMapping(value = "/completedOrder", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeOrder> completedOrder(@RequestParam("memberId") Long memberId,
                                                           @RequestParam("orderId") String orderId,
                                                           @RequestParam("tradedAmount") BigDecimal tradedAmount,
                                                           @RequestParam("turnover") BigDecimal turnover) {
        return success(this.orderService.completedOrder(memberId, orderId, tradedAmount, turnover));
    }

    /**
     * 撤销订单的处理
     *
     * @param memberId     用户ID
     * @param orderId      订单号
     * @param tradedAmount 成交数量
     * @param turnover     成交额
     * @return
     */
    @ApiOperation(value = "撤销订单的处理接口", notes = "撤销订单的处理接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int", required = true),
            @ApiImplicitParam(value = "订单号", name = "orderId", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(value = "成交数量", name = "tradedAmount", dataTypeClass = BigDecimal.class, required = true),
            @ApiImplicitParam(value = "成交额", name = "turnover", dataTypeClass = BigDecimal.class, required = true)
    })
    @RequestMapping(value = "/canceledOrder", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeOrder> canceledOrder(@RequestParam("memberId") Long memberId,
                                                          @RequestParam("orderId") String orderId,
                                                          @RequestParam("tradedAmount") BigDecimal tradedAmount,
                                                          @RequestParam("turnover") BigDecimal turnover) {
        return success(this.orderService.canceledOrder(memberId, orderId, tradedAmount, turnover));
    }

    /**
     * 撤销订单的处理（无法提供成交额和成交量）
     *
     * @param memberId 用户ID
     * @param orderId  订单号
     * @return
     */
    @ApiOperation(value = "撤销订单的处理接口", notes = "撤销订单的处理接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int", required = true),
            @ApiImplicitParam(value = "订单号", name = "orderId", dataTypeClass = String.class, required = true)
    })
    @RequestMapping(value = "/canceledOrder2", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeOrder> canceledOrder(@RequestParam("memberId") Long memberId,
                                                          @RequestParam("orderId") String orderId) {
        return success(this.orderService.canceledOrder(memberId, orderId));
    }

    /**
     * 重做
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "重做接口", notes = "重做接口")
    @ApiImplicitParam(value = "错误业务记录ID", name = "id", dataType = "int", required = true)
    @RequestMapping(value = "/redo", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<Boolean> redo(@RequestParam("id") Long id) {
        return success(this.redoService.redo(id));
    }

}