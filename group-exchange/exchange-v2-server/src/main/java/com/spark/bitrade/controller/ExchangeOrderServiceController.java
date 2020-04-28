package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.config.ExchangeForwardStrategyConfiguration;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.dto.ExchangeOrderDto;
import com.spark.bitrade.dto.ExchangeOrderStats;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BigDecimalUtil;
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
import java.util.List;
import java.util.Optional;

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
    @Resource
    private OrderFacadeService orderService;
    @Resource
    private ExchangeTradeService tradeService;
    @Autowired
    private ExchangeCancelOrderService cancelOrderService;
    @Autowired
    private ExchangeRedoService redoService;
    @Autowired
    private ExchangeCoinService exchangeCoinService;
    @Autowired
    private ExchangePlaceOrderService exchangePlaceOrderService;
    @Autowired
    private ExchangeOrderService exchangeOrderService;

    @Autowired
    private ExchangeReleaseFreezeRuleService exchangeReleaseFreezeRuleService;
    @Autowired
    private IExchange2ReleaseService exchange2ReleaseService;

    @Autowired
    private ForwardService forwardService;

    /**
     * 创建订单
     *
     * @param exchangeOrder 订单
     * @return 新增结果
     */
    @ApiOperation(value = "创建订单接口", notes = "创建订单接口")
    @ApiImplicitParam(value = "订单数据", name = "order", dataTypeClass = ExchangeOrder.class)
//    @RequestMapping(value = "/createOrder", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeOrder> createOrder(@RequestBody ExchangeOrder exchangeOrder) {
        //验证必填信息
        AssertUtil.notNull(exchangeOrder, CommonMsgCode.INVALID_PARAMETER);

        //订单号，E开头的订单为普通用户的订单
//        this.checkOrderIdFormat(exchangeOrder.getOrderId());

        //会员ID：必填
        AssertUtil.notNull(exchangeOrder.getMemberId(), CommonMsgCode.INVALID_PARAMETER);
        //交易数量:必填，且大于0
        AssertUtil.isTrue(BigDecimalUtil.gt0(exchangeOrder.getAmount()), CommonMsgCode.INVALID_PARAMETER);
        //订单方向:必填
        AssertUtil.notNull(exchangeOrder.getDirection(), CommonMsgCode.INVALID_PARAMETER);
        //挂单价格:必填，且大于0
        AssertUtil.isTrue(BigDecimalUtil.gt0(exchangeOrder.getPrice()), CommonMsgCode.INVALID_PARAMETER);
        //交易对:必填
        AssertUtil.notNull(exchangeOrder.getSymbol(), CommonMsgCode.INVALID_PARAMETER);
//        //挂单类型，0市价，1限价，只能是限价
//        AssertUtil.isTrue(exchangeOrder.getType() == ExchangeOrderType.LIMIT_PRICE, CommonMsgCode.INVALID_PARAMETER);
        //冻结币数量:必填，且大于0
        AssertUtil.isTrue(BigDecimalUtil.gt0(exchangeOrder.getFreezeAmount()), CommonMsgCode.INVALID_PARAMETER);

        //交易币:必填
        AssertUtil.notNull(exchangeOrder.getCoinSymbol(), CommonMsgCode.INVALID_PARAMETER);
        //结算币:必填
        AssertUtil.notNull(exchangeOrder.getBaseSymbol(), CommonMsgCode.INVALID_PARAMETER);

        //下单时间
        if (exchangeOrder.getTime() == null) {
            exchangeOrder.setTime(System.currentTimeMillis());
        }
        exchangeOrder.setCompletedTime(null);
        exchangeOrder.setCanceledTime(null);
        exchangeOrder.setTurnover(BigDecimal.ZERO);
        exchangeOrder.setTradedAmount(BigDecimal.ZERO);
        exchangeOrder.setStatus(ExchangeOrderStatus.TRADING);

        return success(this.orderService.createOrder(exchangeOrder));
    }

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
        // 特殊下单
        Optional<ExchangeReleaseFreezeRule> ruleOptional = exchangeReleaseFreezeRuleService.findBySymbol(symbol);
        if (ruleOptional.isPresent()) {
            if (ExchangeOrderDirection.BUY.equals(direction) && ruleOptional.get().getEnableBuy().isIs()) {
                return exchange2ReleaseService.placeOrder(memberId, direction, symbol, price, amount, type, tradeCaptcha);
            }

            if (ExchangeOrderDirection.SELL.equals(direction) && ruleOptional.get().getEnableSell().isIs()) {
                return exchange2ReleaseService.placeOrder(memberId, direction, symbol, price, amount, type, tradeCaptcha);
            }
        }

        // 转发请求
        if (forwardService.getStrategy(symbol).isPresent()) {
            Optional<ExchangeForwardStrategyConfiguration> optional = forwardService.getStrategy(symbol);
            if (ExchangeOrderDirection.BUY.equals(direction) && optional.get().getEnablePlaceBuy()) {
                return forwardService.placeOrder(memberId, direction, symbol, price, amount, type, tradeCaptcha);
            }

            if (ExchangeOrderDirection.SELL.equals(direction) && optional.get().getEnablePlaceSell()) {
                return forwardService.placeOrder(memberId, direction, symbol, price, amount, type, tradeCaptcha);
            }
        }

        // 正常下单
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
        // 转发请求
        if (forwardService.getStrategy(trade.getSymbol()).isPresent()) {
            Optional<ExchangeForwardStrategyConfiguration> optional = forwardService.getStrategy(trade.getSymbol());
            if (optional.get().getEnableTradeBuy()) {
                return forwardService.tradeBuy(trade);
            }

            if (optional.get().getEnableTradeSell()) {
                return forwardService.tradeSell(trade);
            }
        }

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
        // 转发请求
        if (forwardService.getStrategy(trade.getSymbol()).isPresent()) {
            Optional<ExchangeForwardStrategyConfiguration> optional = forwardService.getStrategy(trade.getSymbol());
            if (optional.get().getEnableTradeBuy()) {
                return forwardService.tradeSell(trade);
            }

            if (optional.get().getEnableTradeSell()) {
                return forwardService.tradeSell(trade);
            }
        }

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
     * 查询订单
     *
     * @param memberId 用户ID
     * @param orderId  订单号
     * @return
     */
    @ApiOperation(value = "查询订单接口", notes = "查询订单接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int", required = true),
            @ApiImplicitParam(value = "订单号", name = "orderId", dataTypeClass = String.class, required = true)
    })
    @RequestMapping(value = "/queryOrder", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeOrder> queryOrder(@RequestParam("memberId") Long memberId, @RequestParam("orderId") String orderId) {
        ExchangeOrder order = this.orderService.queryOrder(memberId, orderId);
        if (order != null) {
            return success(order);
        } else {
            return failed();
        }
    }

    /**
     * 查询订单及交易明细
     *
     * @param memberId 用户ID
     * @param orderId  订单号
     * @return
     */
    @ApiOperation(value = "查询订单接口", notes = "查询订单接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int", required = true),
            @ApiImplicitParam(value = "订单号", name = "orderId", dataTypeClass = String.class, required = true)
    })
    @RequestMapping(value = "/queryOrderDetail", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeOrderDto> queryOrderDetail(@RequestParam("memberId") Long memberId, @RequestParam("orderId") String orderId) {
        return this.success(this.orderService.queryOrderDetail(memberId, orderId));
//
//        ExchangeOrder order = this.orderService.queryOrder(memberId, orderId);
//        if (order != null) {
//            ExchangeOrderDto orderDto = new ExchangeOrderDto();
//            BeanUtils.copyProperties(order, orderDto);
//            if (order.getStatus().equals(ExchangeOrderStatus.TRADING)) {
//                orderDto.setDetail(orderService.listTradeDetail(orderId));
//            } else {
//                orderDto.setDetail(orderService.listHistoryByOrderId(orderId));
//            }
//
//            return success(orderDto);
//        } else {
//            return failed();
//        }
    }

    /**
     * 申请撤销申请
     *
     * @param memberId 用户ID
     * @param orderId  订单号
     * @return
     */
    @ApiOperation(value = "申请撤销申请接口", notes = "申请撤销申请接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int", required = true),
            @ApiImplicitParam(value = "订单号", name = "orderId", dataTypeClass = String.class, required = true)
    })
    @RequestMapping(value = "/claimCancelOrder", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeOrder> claimCancelOrder(@RequestParam("memberId") Long memberId, @RequestParam("orderId") String orderId) {
        //限制重复提交
        if (this.cancelOrderService.isCancelOrderRequestLimit(orderId)) {
            return failed(CommonMsgCode.FORBID_RESUBMIT);
        } else {
            return success(this.cancelOrderService.claimCancelOrder(memberId, orderId));
        }
    }

    /**
     * 限制重复提交
     *
     * @param orderId 订单号
     * @return
     */
    @ApiOperation(value = "限制重复提交接口", notes = "限制重复提交接口")
    @ApiImplicitParam(value = "订单号", name = "orderId", dataTypeClass = String.class, required = true)
    @RequestMapping(value = "/isRequestLimit", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<Boolean> isRequestLimit(@RequestParam("orderId") String orderId) {
        // 限制重复提交
        return this.of(CommonMsgCode.SUCCESS, this.cancelOrderService.isCancelOrderRequestLimit(orderId));
    }


    /**
     * 分页查询交易中的订单
     *
     * @param size       分页.每页数量
     * @param current    分页.当前页码
     * @param memberId   用户ID
     * @param symbol     交易对
     * @param coinSymbol
     * @param baseSymbol
     * @param direction
     * @return
     */
    @ApiOperation(value = "分页查询交易中的订单数据接口", notes = "分页查询交易中的订单数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int"),
            @ApiImplicitParam(value = "交易对", name = "symbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "交易币", name = "coinSymbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "基币", name = "baseSymbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "交易方向", name = "direction", dataTypeClass = ExchangeOrderDirection.class)
    })
    @RequestMapping(value = "/openOrders", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<ExchangeOrderDto>> openOrders(@RequestParam("size") Integer size,
                                                                 @RequestParam("current") Integer current,
                                                                 @RequestParam(value = "memberId", required = false) Long memberId,
                                                                 @RequestParam(value = "symbol", required = false) String symbol,
                                                                 @RequestParam(value = "coinSymbol", required = false) String coinSymbol,
                                                                 @RequestParam(value = "baseSymbol", required = false) String baseSymbol,
                                                                 @RequestParam(value = "direction", required = false) ExchangeOrderDirection direction) {
        return success(this.orderService.openOrders(memberId, symbol, current, size, coinSymbol, baseSymbol, direction));
    }

    /**
     * 分页查询交易中的订单(含交易明细记录)
     *
     * @param size       分页.每页数量，不超过100
     * @param current    分页.当前页码
     * @param memberId   用户ID
     * @param symbol     交易对
     * @param coinSymbol
     * @param baseSymbol
     * @param direction
     * @return
     */
    @ApiOperation(value = "分页查询交易中的订单数据(含交易明细记录)接口", notes = "分页查询交易中的订单数据(含交易明细记录)接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int"),
            @ApiImplicitParam(value = "交易对", name = "symbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "交易币", name = "coinSymbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "基币", name = "baseSymbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "交易方向", name = "direction", dataTypeClass = ExchangeOrderDirection.class)
    })
    @RequestMapping(value = "/openOrdersAndDetail", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<PageData<ExchangeOrder>> openOrdersAndDetail(@RequestParam("size") Integer size,
                                                                          @RequestParam("current") Integer current,
                                                                          @RequestParam(value = "memberId", required = false) Long memberId,
                                                                          @RequestParam(value = "symbol", required = false) String symbol,
                                                                          @RequestParam(value = "coinSymbol", required = false) String coinSymbol,
                                                                          @RequestParam(value = "baseSymbol", required = false) String baseSymbol,
                                                                          @RequestParam(value = "direction", required = false) ExchangeOrderDirection direction) {
        size = size > 100 ? 100 : size;
        IPage<ExchangeOrderDto> page = this.orderService.openOrders(memberId, symbol, current, size, coinSymbol, baseSymbol, direction);

        page.getRecords().forEach(exchangeOrder -> {
            this.processCurrentOrderDetailAndTraderAmout(exchangeOrder);
        });
        return success(new PageData(page));
    }

    /**
     * 对未成交订单 进行成交明细和成交额的加工处理
     *
     * @param exchangeOrder
     */
    public void processCurrentOrderDetailAndTraderAmout(ExchangeOrderDto exchangeOrder) {
        //获取交易成交详情
        BigDecimal tradedAmount = BigDecimal.ZERO;
        List<ExchangeOrderDetail> details = orderService.listTradeDetail(exchangeOrder.getOrderId());
        exchangeOrder.setDetail(details);
        for (ExchangeOrderDetail trade : details) {
            if (exchangeOrder.getType() == ExchangeOrderType.MARKET_PRICE
                    && exchangeOrder.getDirection() == ExchangeOrderDirection.BUY) {
                //使用已成交的交易额（已经换算为USDT了）
                if (trade.getTurnover() == null || trade.getTurnover().compareTo(BigDecimal.ZERO) <= 0) {
                    ExchangeCoin exchangeCoin = exchangeCoinService.findBySymbol(exchangeOrder.getSymbol());
                    tradedAmount = tradedAmount.add(trade.getAmount().multiply(trade.getPrice())).setScale(exchangeCoin.getCoinScale(), BigDecimal.ROUND_UP);
                } else {
                    tradedAmount = tradedAmount.add(trade.getTurnover());
                }
            } else {
                tradedAmount = tradedAmount.add(trade.getAmount());
            }
        }
        exchangeOrder.setTradedAmount(tradedAmount);
    }


    /**
     * 分页查询历史订单数据
     *
     * @param size       分页.每页数量，不超过100
     * @param current    分页.当前页码
     * @param memberId   用户ID
     * @param symbol     交易对
     * @param coinSymbol
     * @param baseSymbol
     * @param direction
     * @param status
     * @return
     */
    @ApiOperation(value = "分页查询历史订单数据接口", notes = "分页查询历史订单数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int"),
            @ApiImplicitParam(value = "交易对", name = "symbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "交易币", name = "coinSymbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "基币", name = "baseSymbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "交易方向", name = "direction", dataTypeClass = ExchangeOrderDirection.class),
            @ApiImplicitParam(value = "交易状态", name = "status", dataTypeClass = ExchangeOrderStatus.class),
            @ApiImplicitParam(value = "开始时间戳（包含）", name = "startTime", dataType = "int"),
            @ApiImplicitParam(value = "截止时间戳", name = "endTime", dataType = "int")
    })
    @RequestMapping(value = "/historyOrders", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<ExchangeOrderDto>> historyOrders(@RequestParam("size") Integer size,
                                                                    @RequestParam("current") Integer current,
                                                                    @RequestParam(value = "memberId", required = false) Long memberId,
                                                                    @RequestParam(value = "symbol", required = false) String symbol,
                                                                    @RequestParam(value = "coinSymbol", required = false) String coinSymbol,
                                                                    @RequestParam(value = "baseSymbol", required = false) String baseSymbol,
                                                                    @RequestParam(value = "direction", required = false) ExchangeOrderDirection direction,
                                                                    @RequestParam(value = "status", required = false) ExchangeOrderStatus status,
                                                                    @RequestParam(value = "startTime", required = false) Long startTime,
                                                                    @RequestParam(value = "endTime", required = false) Long endTime) {
        return success(this.orderService.historyOrders(memberId, symbol, current, size, coinSymbol, baseSymbol, direction, status, startTime, endTime));
    }

    /**
     * 分页查询历史订单数据(含交易明细记录)
     *
     * @param size       分页.每页数量，不超过100
     * @param current    分页.当前页码
     * @param memberId   用户ID
     * @param symbol     交易对
     * @param coinSymbol
     * @param baseSymbol
     * @param direction
     * @param status
     * @return
     */
    @ApiOperation(value = "分页查询历史订单数据(含交易明细记录)接口", notes = "分页查询历史订单数据(含交易明细记录)接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int"),
            @ApiImplicitParam(value = "交易对", name = "symbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "交易币", name = "coinSymbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "基币", name = "baseSymbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "交易方向", name = "direction", dataTypeClass = ExchangeOrderDirection.class),
            @ApiImplicitParam(value = "交易状态", name = "status", dataTypeClass = ExchangeOrderStatus.class),
            @ApiImplicitParam(value = "开始时间戳（包含）", name = "startTime", dataType = "int"),
            @ApiImplicitParam(value = "截止时间戳", name = "endTime", dataType = "int")
    })
    @RequestMapping(value = "/historyOrdersAndDetail", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<PageData<ExchangeOrder>> historyOrdersAndDetail(@RequestParam("size") Integer size,
                                                                             @RequestParam("current") Integer current,
                                                                             @RequestParam(value = "memberId", required = false) Long memberId,
                                                                             @RequestParam(value = "symbol", required = false) String symbol,
                                                                             @RequestParam(value = "coinSymbol", required = false) String coinSymbol,
                                                                             @RequestParam(value = "baseSymbol", required = false) String baseSymbol,
                                                                             @RequestParam(value = "direction", required = false) ExchangeOrderDirection direction,
                                                                             @RequestParam(value = "status", required = false) ExchangeOrderStatus status,
                                                                             @RequestParam(value = "startTime", required = false) Long startTime,
                                                                             @RequestParam(value = "endTime", required = false) Long endTime) {
        size = size > 100 ? 100 : size;
        IPage<ExchangeOrderDto> page = this.orderService.historyOrders(memberId, symbol, current, size, coinSymbol, baseSymbol, direction, status, startTime, endTime);
        // 添加成交记录
        page.getRecords().forEach(exchangeOrder ->
                exchangeOrder.setDetail(this.orderService.listHistoryByOrderId(exchangeOrder.getOrderId())));

        return success(new PageData(page));
    }

    /**
     * 查询订单的撮合明细记录
     *
     * @param orderId 订单号
     * @return
     */
    @ApiOperation(value = "查询订单的撮合明细记录接口", notes = "查询订单的撮合明细记录接口")
    @ApiImplicitParam(value = "订单号", name = "orderId", dataTypeClass = String.class, required = true)
    @RequestMapping(value = "/listTradeDetail", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<List<ExchangeOrderDetail>> listTradeDetail(@RequestParam("orderId") String orderId) {
        return success(this.orderService.listTradeDetail(orderId));
    }

    /**
     * 项目方中心数据统计
     *
     * @param coinSymbol 项目币种
     * @return
     */
    @ApiOperation(value = "项目方中心数据统计接口", notes = "项目方中心数据统计接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "项目币种", name = "coinSymbol", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(value = "订单类型，0市价，1限价", name = "type", dataTypeClass = ExchangeOrderType.class),
            @ApiImplicitParam(value = "订单状态，不传递为所有订单，0=交易中，1=已完成订单", name = "status", dataType = "int"),
            @ApiImplicitParam(value = "开始时间戳（包含）", name = "startTime", dataType = "int"),
            @ApiImplicitParam(value = "截止时间戳", name = "endTime", dataType = "int")
    })
    @RequestMapping(value = "/stats", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<List<ExchangeOrderStats>> stats(@RequestParam(value = "coinSymbol") String coinSymbol,
                                                             @RequestParam(value = "type", required = false) ExchangeOrderType type,
                                                             @RequestParam(value = "status", required = false) Integer status,
                                                             @RequestParam(value = "startTime", required = false) Long startTime,
                                                             @RequestParam(value = "endTime", required = false) Long endTime) {
        return success(this.exchangeOrderService.stats(type, coinSymbol, status, startTime, endTime));
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