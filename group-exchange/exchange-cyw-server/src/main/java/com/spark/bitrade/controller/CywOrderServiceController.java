package com.spark.bitrade.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.ExchangeCywMsgCode;
import com.spark.bitrade.entity.ExchangeCywOrder;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeOrderDetail;
import com.spark.bitrade.entity.ExchangeTrade;
import com.spark.bitrade.service.CywCancelOrderService;
import com.spark.bitrade.service.CywOrderService;
import com.spark.bitrade.service.CywRedoService;
import com.spark.bitrade.service.CywTradeService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * 机器人订单服务控制层(内部服务，不对外)
 *
 * @author yangch
 * @since 2019-09-02 11:23:46
 */
@RestController
@RequestMapping("/service/v1/cywOrder")
@Api(description = "订单服务控制层")
public class CywOrderServiceController extends ApiController {
    @Resource
    private CywOrderService orderService;
    @Resource
    private CywTradeService tradeService;
    @Autowired
    private CywCancelOrderService cancelOrderService;
    @Autowired
    private CywRedoService redoService;

    /**
     * 创建订单
     *
     * @param order 订单
     * @return 新增结果
     */
    @ApiOperation(value = "创建订单接口", notes = "创建订单接口")
    @ApiImplicitParam(value = "订单数据", name = "order", dataTypeClass = ExchangeOrder.class)
    @RequestMapping(value = "/createOrder", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeOrder> createOrder(@RequestBody ExchangeOrder order) {
        //验证必填信息
        AssertUtil.notNull(order, CommonMsgCode.INVALID_PARAMETER);
        ExchangeCywOrder cywOrder = new ExchangeCywOrder();
        //ExchangeCywOrder cywOrder = (ExchangeCywOrder) order; //ExchangeOrder 服务转换为 ExchangeCywOrder
        BeanUtils.copyProperties(order, cywOrder);

        //订单号，S开头的订单为星客机器人订单
        this.checkOrderIdFormat(cywOrder.getOrderId());

        //会员ID：必填
        AssertUtil.notNull(cywOrder.getMemberId(), CommonMsgCode.INVALID_PARAMETER);
        //交易数量:必填，且大于0
        AssertUtil.isTrue(BigDecimalUtil.gt0(cywOrder.getAmount()), CommonMsgCode.INVALID_PARAMETER);
        //订单方向:必填
        AssertUtil.notNull(cywOrder.getDirection(), CommonMsgCode.INVALID_PARAMETER);
        //挂单价格:必填，且大于0
        AssertUtil.isTrue(BigDecimalUtil.gt0(cywOrder.getPrice()), CommonMsgCode.INVALID_PARAMETER);
        //交易对:必填
        AssertUtil.notNull(cywOrder.getSymbol(), CommonMsgCode.INVALID_PARAMETER);
        //挂单类型，0市价，1限价，只能是限价
        AssertUtil.isTrue(cywOrder.getType() == ExchangeOrderType.LIMIT_PRICE, CommonMsgCode.INVALID_PARAMETER);
        //冻结币数量:必填，且大于0
        AssertUtil.isTrue(BigDecimalUtil.gt0(cywOrder.getFreezeAmount()), CommonMsgCode.INVALID_PARAMETER);

        //交易币:必填
        AssertUtil.notNull(cywOrder.getCoinSymbol(), CommonMsgCode.INVALID_PARAMETER);
        //结算币:必填
        AssertUtil.notNull(cywOrder.getBaseSymbol(), CommonMsgCode.INVALID_PARAMETER);

        //下单时间
        if (cywOrder.getTime() == null) {
            cywOrder.setTime(System.currentTimeMillis());
        }
        cywOrder.setCompletedTime(null);
        cywOrder.setCanceledTime(null);
        cywOrder.setTurnover(BigDecimal.ZERO);
        cywOrder.setTradedAmount(BigDecimal.ZERO);
        cywOrder.setStatus(ExchangeOrderStatus.TRADING);

        return success(this.orderService.createOrder(cywOrder));
    }

    /**
     * 创建机器人订单
     *
     * @param jsonExchangeOrder 订单
     * @return 新增结果
     */

    @ApiOperation(value = "创建机器人订单接口", notes = "创建机器人订单接口")
    @ApiImplicitParam(value = "订单数据", name = "jsonExchangeOrder", dataTypeClass = String.class)
    @RequestMapping(value = "/createOrder2", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult createOrder(@RequestParam("jsonData") String jsonExchangeOrder) {
        return null;
        /*ExchangeCywOrder exchangeCywOrder = JSON.parseObject(jsonExchangeOrder, ExchangeCywOrder.class);

        //验证必填信息
        AssertUtil.notNull(jsonExchangeOrder, CommonMsgCode.INVALID_PARAMETER);

        return success(this.orderService.createOrder(exchangeCywOrder));*/
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
        this.checkOrderIdFormat(orderId);
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
        this.checkOrderIdFormat(orderId);
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
        this.checkOrderIdFormat(orderId);
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
        this.checkOrderIdFormat(orderId);
        ExchangeOrder order = this.orderService.queryOrder(memberId, orderId);
        if (order != null) {
            return success(order);
        } else {
            return failed();
        }
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
     * 指定交易区正在交易的用户
     *
     * @param symbol 交易对，eg：SLU/USDT
     * @return
     */
    @ApiOperation(value = "查询正在交易的用户接口", notes = "查询正在交易的用户接口")
    @ApiImplicitParam(value = "交易对", name = "symbol", dataTypeClass = String.class, required = true)
    @RequestMapping(value = "/openMembers", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<Set<Long>> openMembers(@RequestParam("symbol") String symbol) {
        return success(this.orderService.openMembers(symbol));
    }

    /**
     * 查询正在交易的订单
     *
     * @param memberId 用户ID
     * @param symbol   交易对，eg：SLU/USDT
     * @return
     */
    @ApiOperation(value = "查询正在交易的订单接口", notes = "查询正在交易的订单接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int", required = true),
            @ApiImplicitParam(value = "交易对", name = "symbol", dataTypeClass = String.class, required = true)
    })
    @RequestMapping(value = "/openOrders", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<List<ExchangeOrder>> openOrders(@RequestParam("memberId") Long memberId, @RequestParam("symbol") String symbol) {
        return success(this.orderService.openOrders(symbol, memberId));
    }

    /**
     * 分页查询历史订单数据
     *
     * @param size     分页.每页数量，不超过100
     * @param current  分页.当前页码
     * @param symbol   交易对
     * @param memberId 用户ID
     * @return
     */
    @ApiOperation(value = "分页查询历史订单数据接口", notes = "分页查询历史订单数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "交易对", name = "symbol", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int", required = true)
    })
    @RequestMapping(value = "/historyOrders", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<ExchangeOrder>> historyOrders(Integer size, Integer current, String symbol, Long memberId) {
        if (size == null) {
            size = 10;
        } else if (size > 100) {
            size = 100;
        }

        return success(this.orderService.historyOrders(symbol, memberId, size, current));
    }

    /**
     * 分页查询历史订单数据
     *
     * @param size     分页.每页数量，不超过100
     * @param current  分页.当前页码
     * @param symbol   交易对
     * @param memberId 用户ID
     * @return
     */
    @ApiOperation(value = "分页查询历史订单数据接口", notes = "分页查询历史订单数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "交易对", name = "symbol", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int", required = true)
    })
    @RequestMapping(value = "/historyOrders2", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<PageData<ExchangeOrder>> historyOrders2(Integer size, Integer current, String symbol, Long memberId) {
        if (size == null) {
            size = 10;
        } else if (size > 100) {
            size = 100;
        }
        return success(new PageData(this.orderService.historyOrders(symbol, memberId, size, current)));
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

    /**
     * 校验订单格式
     *
     * @param orderId
     */
    private void checkOrderIdFormat(String orderId) {
        //订单格式：S1168423154092716041_SLUUSDT
        AssertUtil.notNull(orderId, CommonMsgCode.INVALID_PARAMETER);

        if (!orderId.startsWith("S")) {
            ExceptionUitl.throwsMessageCodeException(ExchangeCywMsgCode.BAD_CYW_ORDER);
        }
        if (!orderId.contains("_")) {
            ExceptionUitl.throwsMessageCodeException(ExchangeCywMsgCode.BAD_CYW_ORDER);
        }
    }

}