package com.spark.bitrade.controller;

import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.ExchangeUsdcOrder;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.entity.vo.ExchangeUsdcInfo;
import com.spark.bitrade.enums.OtcExceptionMsg;
import com.spark.bitrade.service.AmountOfDiscountRecordService;
import com.spark.bitrade.service.CurrencyRuleSettingService;
import com.spark.bitrade.service.ExchangeUsdcOrderService;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * USDC兑换记录(ExchangeUsdcOrder)表控制层
 *
 * @author ss
 * @date 2020-04-08 16:01:32
 */
@RestController
@RequestMapping("api/v2/exchangeUsdcOrder")
@Api(tags = "经纪人USDC优惠购币通道")
public class ExchangeUsdcOrderController extends ApiController{
    /**
     * 服务对象
     */
    @Resource
    private ExchangeUsdcOrderService exchangeUsdcOrderService;

    /**
     * USDC兑换前的准备查询接口
     * @param member
     * @return
     */
    @PostMapping("pre")
    @ApiOperation("USDC兑换前的准备查询接口")
    public MessageRespResult<ExchangeUsdcInfo> getPre(@MemberAccount Member member) {
        ExchangeUsdcInfo exchangeUsdcInfo = exchangeUsdcOrderService.getPre(member.getId());
        return success(exchangeUsdcInfo);
    }
    /**
     *  USDC兑换接口
     * @param member
     * @param usdcAmount 兑换USDC数量
     * @param exchangeUnitAmount 兑换币数量
     * @param jyPassword 交易密码
     * @param price USDC对兑换币的价格：如果price=2,则 一个兑换币等于两个USDC
     * @return
     */
    @PostMapping("exchangeUsdc")
    @ApiOperation("USDC兑换")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "兑换USDC数量", name = "usdcAmount", dataTypeClass = BigDecimal.class, required = true),
            @ApiImplicitParam(value = "兑换币数量", name = "exchangeUnitAmount", dataTypeClass = BigDecimal.class, required = true),
            @ApiImplicitParam(value = "交易密码", name = "jyPassword", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(value = "USDC对兑换币的价格：如果price=2,则 一个兑换币等于两个USDC", name = "price", dataTypeClass = BigDecimal.class, required = true)
    })
    public MessageRespResult exchangeUsdc(@MemberAccount Member member,BigDecimal usdcAmount,BigDecimal exchangeUnitAmount,String jyPassword,BigDecimal price) {
        Boolean result = exchangeUsdcOrderService.exchange(member,usdcAmount,exchangeUnitAmount,jyPassword,price);
        return result ? success("成功") : failed(CommonMsgCode.FAILURE);
    }
}