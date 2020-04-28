package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.ExchangeFastCoin;
import com.spark.bitrade.entity.ExchangeFastOrder;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.service.ExchangeFastCoinService;
import com.spark.bitrade.service.ExchangeFastOrderService;
import com.spark.bitrade.trans.ExchangeFastCoinRateInfo;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 闪兑订单(ExchangeFastOrder)控制层
 *
 * @author yangch
 * @since 2019-06-24 17:06:54
 */
@RestController
@RequestMapping("api/v2/fast")
@Api(description = "闪兑订单控制层")
@Slf4j
public class ExchangeFastOrderController extends ApiController {
    @Autowired
    private ExchangeFastCoinService fastCoinService;
    @Autowired
    private ExchangeFastOrderService fastOrderService;

    /**
     * 分页查询所有数据
     *
     * @param size              分页.每页数量
     * @param current           分页.当前页码
     * @param exchangeFastOrder 查询实体
     * @return 所有数据
     */
    @ApiOperation(value = "分页查询所有数据接口", notes = "分页查询所有数据接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "实体对象", name = "exchangeFastOrder", dataTypeClass = ExchangeFastOrder.class)
    })
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<IPage<ExchangeFastOrder>> list(Integer size, Integer current, ExchangeFastOrder exchangeFastOrder) {
        return success(this.fastOrderService.page(new Page<>(current, size), new QueryWrapper<>(exchangeFastOrder)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @ApiOperation(value = "通过主键查询单条数据接口", notes = "通过主键查询单条数据接口")
    @ApiImplicitParam(value = "主键", name = "id", dataTypeClass = Serializable.class, required = true)
    @RequestMapping(value = "/get", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeFastOrder> get(@RequestParam("id") Serializable id) {
        return success(this.fastOrderService.getById(id));
    }


    /**
     * 闪兑支持币种的列表接口
     *
     * @param appId
     * @return
     */
    @ApiOperation(value = "闪兑支持币种的列表接口", notes = "闪兑支持币种的列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "app端的ID", name = "appId", required = true),
            @ApiImplicitParam(value = "闪兑基币币种名称，如CNYT、BT", name = "baseSymbol")
    })
    @RequestMapping(value = "/support/coins",
            method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<List<ExchangeFastCoin>> supportCoins(String appId, String baseSymbol) {
        if (StringUtils.hasText(baseSymbol)) {
            baseSymbol = baseSymbol.toUpperCase();
        }
        return MessageRespResult.success4Data(fastCoinService.list4CoinSymbol(appId, baseSymbol));
    }

    /**
     * 闪兑基币币种的列表接口
     *
     * @param appId
     * @return
     */
    @ApiOperation(value = "闪兑基币币种的列表接口", notes = "闪兑基币币种的列表接口")
    @ApiImplicitParam(value = "app端的ID", name = "appId", required = true)
    @RequestMapping(value = "/support/baseCoins",
            method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<List<String>> supportBaseCoins(String appId) {
        return MessageRespResult.success4Data(fastCoinService.list4BaseSymbol(appId));
    }

    /**
     * 闪兑汇率接口
     *
     * @param coinSymbol 闪兑币种
     * @param baseSymbol 闪兑基币
     * @param direction  兑换方向
     * @return
     */
    @ApiOperation(value = "闪兑汇率接口", notes = "闪兑汇率接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "应用ID", name = "appId"),
            @ApiImplicitParam(value = "闪兑币种名称，如BTC、LTC", name = "coinSymbol", required = true),
            @ApiImplicitParam(value = "闪兑基币币种名称，如CNYT、BT", name = "baseSymbol", required = true),
            @ApiImplicitParam(value = "订单方向:0=买入(闪兑基币->闪兑币)/1=卖出(闪兑币->闪兑基币)", name = "direction", required = true)
    })
    @RequestMapping(value = "/exchangeRate",
            method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeFastCoinRateInfo> exchangeFastRate(@RequestParam("appId") String appId,
                                                                        @RequestParam("coinSymbol") String coinSymbol,
                                                                        @RequestParam("baseSymbol") String baseSymbol,
                                                                        @RequestParam("direction") ExchangeOrderDirection direction) {
        // 验证币种是否支持闪兑
        ExchangeFastCoin exchangeFastCoin = fastCoinService.findByAppIdAndCoinSymbol(appId, coinSymbol, baseSymbol);

        return success(fastCoinService.calculateExchangeFastCoinRate(exchangeFastCoin, direction));

        /*AssertUtil.notNull(exchangeFastCoin, ExchangeMsgCode.NONSUPPORT_FAST_EXCHANGE_COIN);

        //获取汇率
        BigDecimal coinRate = exchangeFastCoin.getCoinSymbolFixedRate();
        BigDecimal baseRate = exchangeFastCoin.getBaseSymbolFixedRate();

        if (!this.isValidFixedRate(coinRate)) {
            log.info("未配置coinSymbolFixedRate，从市场行情获取汇率");
            coinRate = coinExchange.getCnytExchangeRate(
                    fastCoinService.getRateValidCoinSymbol(exchangeFastCoin)).getData();
        }
        if (!this.isValidFixedRate(baseRate)) {
            log.info("未配置baseSymbolFixedRate，从市场行情获取汇率");
            baseRate = coinExchange.getCnytExchangeRate(
                    fastCoinService.getRateValidBaseSymbol(exchangeFastCoin)).getData();
        }


        if (direction == ExchangeOrderDirection.BUY) {
            //买入场景： 兑换基币币种 -> 接收币种
            //      基币(BT)=1 -> 兑换币(BTC)=4， 汇率计算 = currentPrice= BT/BTC=1/4=0.25
            if (coinRate.compareTo(BigDecimal.ZERO) == 0) {
                return MessageRespResult.success4Data(BigDecimal.ZERO);
            } else {
                return MessageRespResult.success4Data(baseRate.multiply(BigDecimal.ONE.subtract(exchangeFastCoin.getBuyAdjustRate()))
                        .divide(coinRate, 16, BigDecimal.ROUND_DOWN));
            }
        } else {
            //卖出场景：兑换币 ->基币
            //      兑换币(BTC)=4 -> 基币(BT)=1， 汇率计算 = currentPrice= BTC/BT=4/1=4
            if (baseRate.compareTo(BigDecimal.ZERO) == 0) {
                return MessageRespResult.success4Data(BigDecimal.ZERO);
            } else {
                return MessageRespResult.success4Data(coinRate.multiply(BigDecimal.ONE.subtract(exchangeFastCoin.getSellAdjustRate()))
                        .divide(baseRate, 16, BigDecimal.ROUND_DOWN));
            }
        }*/
    }


    /**
     * 闪兑接口
     *
     * @param coinSymbol 闪兑币种名称
     * @param baseSymbol 基币名称
     * @param amount     闪兑数量
     * @param direction  兑换方向
     * @return
     */
    @ApiOperation(value = "闪兑下单接口(依赖会话)", notes = "闪兑下单接口(依赖会话)")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "闪兑币种名称，如BTC、LTC", name = "coinSymbol", required = true),
            @ApiImplicitParam(value = "闪兑基币币种名称，如CNYT、BT", name = "baseSymbol", required = true),
            @ApiImplicitParam(value = "闪兑数量", name = "amount", required = true),
            @ApiImplicitParam(value = "订单方向:0=买入(闪兑基币->闪兑币)/1=卖出(闪兑币->闪兑基币)", name = "direction", required = true)
    })
    @RequestMapping(value = "/exchange",
            method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeFastOrder> exchange(@MemberAccount Member member, String coinSymbol, String baseSymbol,
                                                         BigDecimal amount, ExchangeOrderDirection direction) {
        String appId = getAppId();

        // 验证币种是否支持闪兑
        ExchangeFastCoin exchangeFastCoin = fastCoinService.findByAppIdAndCoinSymbol(appId, coinSymbol, baseSymbol);

        //获取兑换汇率
        ExchangeFastCoinRateInfo rateInfo = fastCoinService.calculateExchangeFastCoinRate(exchangeFastCoin, direction);

        //闪兑
        ExchangeFastOrder order = fastOrderService.exchangeInitiator(member.getId(), appId, coinSymbol,
                baseSymbol, amount, direction, rateInfo, false);

        return MessageRespResult.success4Data(order);
    }

    /**
     * 闪兑下单接口
     * 备注：兑换为指定币种的数量
     *
     * @param memberId    会员ID
     * @param appId       应用ID
     * @param coinSymbol  闪兑币种名称
     * @param baseSymbol  基币名称
     * @param targetAmout 闪兑目标数量
     * @param direction   兑换方向
     * @return
     */
    @ApiOperation(value = "闪兑下单接口", notes = "闪兑下单接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "会员ID", name = "memberId", required = true),
            @ApiImplicitParam(value = "应用ID", name = "appId", required = true),
            @ApiImplicitParam(value = "闪兑币种名称，如BTC、LTC", name = "coinSymbol", required = true),
            @ApiImplicitParam(value = "闪兑基币币种名称，如CNYT、BT", name = "baseSymbol", required = true),
            @ApiImplicitParam(value = "闪兑数量", name = "targetAmout", required = true),
            @ApiImplicitParam(value = "订单方向:0=买入(闪兑基币->闪兑币)/1=卖出(闪兑币->闪兑基币)", name = "direction", required = true)
    })
    @RequestMapping(value = "/exchangeTargetAmout",
            method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeFastOrder> exchangeTargetAmout(Long memberId, String appId,
                                                                    String coinSymbol, String baseSymbol,
                                                                    BigDecimal targetAmout, ExchangeOrderDirection direction) {
        // 验证币种是否支持闪兑
        ExchangeFastCoin exchangeFastCoin = fastCoinService.findByAppIdAndCoinSymbol(appId, coinSymbol, baseSymbol);

        //获取兑换汇率
        ExchangeFastCoinRateInfo rateInfo = fastCoinService.calculateExchangeFastCoinRate(exchangeFastCoin, direction);

        ExchangeFastOrder order = fastOrderService.exchangeInitiator(memberId, appId, coinSymbol,
                baseSymbol, targetAmout, direction, rateInfo, true);

        return MessageRespResult.success4Data(order);
    }

    /**
     * 闪兑订单接收方重做接口
     *
     * @param orderId 订单ID
     * @return
     */
    @ApiOperation(value = "闪兑订单接收方重做接口", notes = "闪兑订单接收方重做接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "闪兑订单ID", name = "orderId", required = true)
    })
    @RequestMapping(value = "/redoExchangeReceiver",
            method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ExchangeFastOrder> redoExchangeReceiver(Long orderId) {
        AssertUtil.notNull(orderId, CommonMsgCode.INVALID_PARAMETER);

        fastOrderService.exchangeReceiver(orderId);

        return MessageRespResult.success();
    }

}