package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constant.ExchangeOrderStatus;
import com.spark.bitrade.constant.ExchangeOrderType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.dto.ExchangeOrderDto;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeOrderDetail;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.service.ExchangeCancelOrderService;
import com.spark.bitrade.service.ExchangePlaceOrderService;
import com.spark.bitrade.service.OrderFacadeService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import com.spark.bitrade.web.resubmit.annotation.ForbidResubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 币币订单控制层
 *
 * @author yangch
 * @since 2019-09-02 11:23:46
 */
@RestController
@RequestMapping("api/v2/order")
@Api(description = " 币币订单控制层")
public class ExchangeOrderController extends ApiController {

    @Resource
    private OrderFacadeService orderService;
    @Autowired
    private ExchangeCancelOrderService cancelOrderService;
    @Autowired
    private ExchangePlaceOrderService exchangePlaceOrderService;

    /**
     * 委托订单
     *
     * @param direction    交易方式：买币、卖币
     * @param symbol       交易对
     * @param price        委托价格
     * @param amount       委托数量
     * @param type         订单类型：市价、限价
     * @param tradeCaptcha 交易验证码
     * @return
     */
    @ForbidResubmit(interdictTime = 1)
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
    public MessageRespResult<ExchangeOrder> placeOrder(@MemberAccount Member member,
                                                       @RequestParam("direction") ExchangeOrderDirection direction,
                                                       @RequestParam("symbol") String symbol,
                                                       @RequestParam("price") BigDecimal price,
                                                       @RequestParam("amount") BigDecimal amount,
                                                       @RequestParam("type") ExchangeOrderType type,
                                                       @RequestParam(value = "tradeCaptcha", required = false) String tradeCaptcha) {
        return exchangePlaceOrderService.place(member.getId(), direction, symbol, price, amount, type, tradeCaptcha);
    }


    /**
     * 查询订单
     *
     * @param orderId 订单号
     * @return
     */
    @ApiOperation(value = "查询订单接口", notes = "查询订单接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户ID", name = "memberId", dataType = "int", required = true),
            @ApiImplicitParam(value = "订单号", name = "orderId", dataTypeClass = String.class, required = true)
    })
    @RequestMapping(value = "/queryOrder", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeOrder> queryOrder(@MemberAccount Member member, @RequestParam("orderId") String orderId) {
        return success(this.orderService.queryOrder(member.getId(), orderId));
    }

    /**
     * 查询订单
     *
     * @param orderId 订单号
     * @return
     */
    @ApiOperation(value = "查询订单接口", notes = "查询订单接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "订单号", name = "orderId", dataTypeClass = String.class, required = true)
    })
    @RequestMapping(value = "/queryOrderDetail", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeOrder> queryOrderDetail(@MemberAccount Member member, @RequestParam("orderId") String orderId) {
        return this.success(this.orderService.queryOrderDetail(member.getId(), orderId));
    }

    /**
     * 申请撤销申请
     *
     * @param orderId 订单号
     * @return
     */
    @ForbidResubmit(interdictTime = 1)
    @ApiOperation(value = "申请撤销申请接口", notes = "申请撤销申请接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "订单号", name = "orderId", dataTypeClass = String.class, required = true)
    })
    @RequestMapping(value = "/cancelOrder", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeOrder> claimCancelOrder(@MemberAccount Member member, @RequestParam("orderId") String orderId) {
        //限制重复提交
        if (this.cancelOrderService.isCancelOrderRequestLimit(orderId)) {
            return failed(CommonMsgCode.FORBID_RESUBMIT);
        } else {
            return success(this.cancelOrderService.claimCancelOrder(member.getId(), orderId));
        }
    }

    /**
     * 分页查询交易中的订单
     *
     * @param size       分页.每页数量，不超过100
     * @param current    分页.当前页码
     * @param symbol     交易对
     * @param coinSymbol
     * @param baseSymbol
     * @param direction
     * @return
     */
    @ForbidResubmit(interdictTime = 1)
    @ApiOperation(value = "分页查询交易中的订单数据接口", notes = "分页查询交易中的订单数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "交易对", name = "symbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "交易币", name = "coinSymbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "基币", name = "baseSymbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "交易方向", name = "direction", dataTypeClass = ExchangeOrderDirection.class)
    })
    @RequestMapping(value = "/openOrders", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<ExchangeOrderDto>> openOrders(@MemberAccount Member member, @RequestParam("size") Integer size,
                                                                 @RequestParam("current") Integer current,
                                                                 @RequestParam(value = "symbol", required = false) String symbol,
                                                                 @RequestParam(value = "coinSymbol", required = false) String coinSymbol,
                                                                 @RequestParam(value = "baseSymbol", required = false) String baseSymbol,
                                                                 @RequestParam(value = "direction", required = false) ExchangeOrderDirection direction) {
//        size = size > 100 ? 100 : size;
        return success(this.orderService.openOrders(member.getId(), symbol, current, size, coinSymbol, baseSymbol, direction));
    }


    /**
     * 分页查询历史订单数据
     *
     * @param size       分页.每页数量，不超过100
     * @param current    分页.当前页码
     * @param symbol     交易对
     * @param coinSymbol
     * @param baseSymbol
     * @param direction
     * @param status
     * @return
     */
    @ForbidResubmit(interdictTime = 1)
    @ApiOperation(value = "分页查询历史订单数据接口", notes = "分页查询历史订单数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "交易对", name = "symbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "交易币", name = "coinSymbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "基币", name = "baseSymbol", dataTypeClass = String.class),
            @ApiImplicitParam(value = "交易方向", name = "direction", dataTypeClass = ExchangeOrderDirection.class),
            @ApiImplicitParam(value = "交易状态", name = "status", dataTypeClass = ExchangeOrderStatus.class)
    })
    @RequestMapping(value = "/historyOrders", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<ExchangeOrderDto>> historyOrders(@MemberAccount Member member, @RequestParam("size") Integer size,
                                                                    @RequestParam("current") Integer current,
                                                                    @RequestParam(value = "symbol", required = false) String symbol,
                                                                    @RequestParam(value = "coinSymbol", required = false) String coinSymbol,
                                                                    @RequestParam(value = "baseSymbol", required = false) String baseSymbol,
                                                                    @RequestParam(value = "direction", required = false) ExchangeOrderDirection direction,
                                                                    @RequestParam(value = "status", required = false) ExchangeOrderStatus status) {
//        size = size > 100 ? 100 : size;
        return success(this.orderService.historyOrders(member.getId(), symbol, current, size, coinSymbol, baseSymbol, direction, status, null, null));
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
    public MessageRespResult<List<ExchangeOrderDetail>> listTradeDetail(@MemberAccount Member member, @RequestParam("orderId") String orderId) {
        return success(this.orderService.listTradeDetail(orderId));
    }

}