package com.spark.bitrade.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.constant.RealNameStatus;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.PSMsgCode;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.entity.vo.SilkPayOrderListVo;
import com.spark.bitrade.entity.vo.SilkPayOrderVo;
import com.spark.bitrade.service.*;
import com.spark.bitrade.uitl.PayTypeUtil;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import com.spark.bitrade.web.resubmit.annotation.ForbidResubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 支付订单(SilkPayOrder)控制层
 *
 * @author wsy
 * @since 2019-07-18 10:39:01
 */
@RestController
@RequestMapping("api/v2/silkPayOrder")
@Api(description = "支付订单控制层")
public class SilkPayOrderController extends ApiController {

    @Autowired
    private ApplicationContext context;
    @Resource
    private SilkPayGlobalConfigService globalConfigService;
    @Resource
    private SilkPayUserConfigService userConfigService;
    /**
     * 服务对象
     */
    @Resource
    private SilkPayOrderService silkPayOrderService;

    /*private static final List<Long> IDS = Arrays.asList(new Long[]{70237L,
            71639L,
            100036L,
            100718L,
            103228L,
            105408L,
            300169L,
            333585L,
            340507L,
            389686L});*/


    private boolean notProdEnvironment() {
        return !"prod".equals(context.getEnvironment().getActiveProfiles()[0]);
    }


    @Autowired
    private SilkPayCoinService silkPayCoinService;

    @Autowired
    private SilkPayMatchRecordService silkPayMatchRecordService;

    @Autowired
    private IMemberApiService iMemberApiService;

    /**
     * 订单list查询
     *
     * @return true
     * @author shenzucai
     * @time 2019.07.26 11:17
     */
    @ApiOperation(value = "订单list查询", notes = "订单list查询")
    @RequestMapping(value = "/listOrder", method = {RequestMethod.GET, RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(value = "分页.每页数量。eg：10", defaultValue = "10", name = "size", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "分页.当前页码.eg：从1开始", name = "current", defaultValue = "1", dataTypeClass = Integer.class, required = true)
    })
    public MessageRespResult<IPage<SilkPayOrderListVo>> listOrder(@MemberAccount Member member, Integer size, Integer current) {
        // 0, 必要的参数校验
        AssertUtil.notNull(member, CommonMsgCode.UNKNOWN_ACCOUNT);
        if (Objects.isNull(size) || size < 0) {
            size = 10;
        }
        if (Objects.isNull(current) || current < 0) {
            current = 1;
        }
        IPage<SilkPayOrder> silkPayOrderIPage = silkPayOrderService.page(new Page<>(current, size), new QueryWrapper<SilkPayOrder>()
                .eq("member_id", member.getId()).orderByDesc("create_time"));

        AssertUtil.isTrue(!Objects.isNull(silkPayOrderIPage)
                && !Objects.isNull(silkPayOrderIPage.getRecords())
                && silkPayOrderIPage.getRecords().size() > 0, PSMsgCode.RECORD_NOT_FOUND);

        List<SilkPayOrder> silkPayOrders = silkPayOrderIPage.getRecords();
        List<SilkPayOrderListVo> silkPayOrderListVos = new ArrayList<>();
        silkPayOrders.forEach(item -> {
            SilkPayOrderListVo silkPayOrderListVo = new SilkPayOrderListVo();
            BeanUtils.copyProperties(item, silkPayOrderListVo);
            silkPayOrderListVos.add(silkPayOrderListVo);
        });

        IPage<SilkPayOrderListVo> silkPayOrderListVoIPage = new Page<SilkPayOrderListVo>();
        silkPayOrderListVoIPage.setCurrent(silkPayOrderIPage.getCurrent());
        silkPayOrderListVoIPage.setPages(silkPayOrderIPage.getPages());
        silkPayOrderListVoIPage.setSize(silkPayOrderIPage.getSize());
        silkPayOrderListVoIPage.setTotal(silkPayOrderIPage.getTotal());
        silkPayOrderListVoIPage.setRecords(silkPayOrderListVos);

        return success(silkPayOrderListVoIPage);
    }

    /**
     * 订单查询
     *
     * @param order_sn 订单编号
     * @return true
     * @author shenzucai
     * @time 2019.07.26 11:17
     */
    @ApiOperation(value = "订单查询", notes = "订单查询")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "订单编号", name = "order_sn", dataTypeClass = String.class, required = true),
    })
    @RequestMapping(value = "/findOrder", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<SilkPayOrderVo> findOrder(@MemberAccount Member member, String order_sn) {
        // 0, 必要的参数校验
        AssertUtil.notNull(member, CommonMsgCode.UNKNOWN_ACCOUNT);

        // 校验资金密码
        AssertUtil.hasText(order_sn, CommonMsgCode.REQUIRED_PARAMETER);

        SilkPayOrderVo silkPayOrder = silkPayOrderService.getByOrderSn(order_sn);
        silkPayOrder.setCurrentTime(new Date());
        AssertUtil.notNull(silkPayOrder, PSMsgCode.RECORD_NOT_FOUND);
        return success(silkPayOrder);
    }


    /**
     * 订单创建
     *
     * @param receiptContent 收款二维码支付内容
     * @param receiptName    收款人姓名
     * @param longitude      经度（百度坐标系）
     * @param latitude       纬度（百度坐标系）
     * @return true
     * @author shenzucai
     * @time 2019.07.26 11:17
     */
    @ApiOperation(value = "订单创建", notes = "订单创建")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "收款二维码支付内容", name = "receiptContent", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(value = "收款人姓名", name = "receiptName", dataTypeClass = String.class),
            @ApiImplicitParam(value = "法币金额（目前只支持cny）", name = "amount", dataTypeClass = BigDecimal.class, required = true),
            @ApiImplicitParam(value = "支付币种(数字货币或积分大写)", name = "unit", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(value = "经度", name = "longitude", dataTypeClass = BigDecimal.class),
            @ApiImplicitParam(value = "纬度", name = "latitude", dataTypeClass = BigDecimal.class),
            @ApiImplicitParam(value = "资金密码", name = "jyPassword", dataTypeClass = String.class, required = true),
    })
    @RequestMapping(value = "/createOrder", method = {RequestMethod.GET, RequestMethod.POST})
    @ForbidResubmit(value = true, interdictTime = 3)
    public MessageRespResult<SilkPayOrderVo> createOrder(@MemberAccount Member member, String receiptContent, String receiptName, BigDecimal amount, String unit, BigDecimal longitude, BigDecimal latitude, String jyPassword) {

//        if(!notProdEnvironment()) {
//            // 内测限定
//            AssertUtil.isTrue(IDS.contains(member.getId()), PSMsgCode.NOT_INNER_MEMBER);
//        }
        // 0, 必要的参数校验
        AssertUtil.hasLength(receiptContent, CommonMsgCode.REQUIRED_PARAMETER);
        AssertUtil.hasLength(unit, CommonMsgCode.REQUIRED_PARAMETER);
        AssertUtil.notNull(amount, CommonMsgCode.REQUIRED_PARAMETER);

        // 校验是否开启支付功能
        int type = PayTypeUtil.getPayType(receiptContent);
        SilkPayGlobalConfig globalConfig = globalConfigService.getById(1);
        AssertUtil.notNull(globalConfig, CommonMsgCode.FAILURE);
        AssertUtil.isTrue(globalConfig.getUserSingleMin().compareTo(amount) < 1, PSMsgCode.MIN_AMOUNT_LIMIT);
        if (type == 0) { // 微信
            AssertUtil.isTrue(globalConfig.getEnabledWxPay() == 1, PSMsgCode.PAY_CLOSE_WX_PAY);
        } else { // 支付宝
            AssertUtil.isTrue(globalConfig.getEnabledAliPay() == 1, PSMsgCode.PAY_CLOSE_AL_PAY);
        }

        // 必须有手机号
        AssertUtil.hasText(member.getMobilePhone(), PSMsgCode.NOT_BIND_PHONE);
        // 必须实名
        AssertUtil.isTrue(RealNameStatus.VERIFIED == member.getRealNameStatus(), PSMsgCode.NOT_REAL_NAME_MEMBER);
//        // 校验资金密码
        AssertUtil.hasText(jyPassword, CommonMsgCode.MISSING_JYPASSWORD);
        String mbPassword = member.getJyPassword();
        AssertUtil.hasText(mbPassword, CommonMsgCode.NO_SET_JYPASSWORD);
        MessageRespResult<Boolean> confirmResult = iMemberApiService.confirmPassword(mbPassword, jyPassword, member.getSalt());
        AssertUtil.isTrue(confirmResult.isSuccess(), CommonMsgCode.of(confirmResult.getCode(), confirmResult.getMessage()));
        AssertUtil.isTrue(confirmResult.getData(), CommonMsgCode.ERROR_JYPASSWORD);

        // 对币种是否是合法的进行校验
        QueryWrapper<SilkPayCoin> silkPayCoinQueryWrapper = new QueryWrapper<SilkPayCoin>()
                .eq("unit", unit).eq("state", 1);
        SilkPayCoin silkPayCoin = silkPayCoinService.getOne(silkPayCoinQueryWrapper);
        AssertUtil.notNull(silkPayCoin, PSMsgCode.INVALID_UNIT);
        AssertUtil.isTrue(silkPayCoin.getTradeMax().compareTo(amount) > -1, PSMsgCode.MAX_AMOUNT_LIMIT);
        AssertUtil.isTrue(silkPayCoin.getTradeMin().compareTo(amount) < 1, PSMsgCode.MIN_AMOUNT_LIMIT);

        // 解限用户限额
        userConfigService.resetSurplus(member.getId(), globalConfig);

        // 校验金额，次数限额
        SilkPayUserConfig userConfig = userConfigService.getById(member.getId());
        if (userConfig == null || userConfig.getDefaultConfig() == 1) {
            SilkPayUserConfig newUserConfig = new SilkPayUserConfig();
            newUserConfig.setMemberId(member.getId());
            newUserConfig.setEnablePay(userConfig == null ? 0 : userConfig.getEnablePay());
            newUserConfig.setDefaultConfig(userConfig == null ? 1 : userConfig.getDefaultConfig());
            newUserConfig.setQuotaTotal(userConfig == null || userConfig.getDefaultConfig() == 1 ? globalConfig.getUserTotalQuota() : userConfig.getQuotaTotal());
            newUserConfig.setQuotaDaily(userConfig == null || userConfig.getDefaultConfig() == 1 ? globalConfig.getUserSingleQuota() : userConfig.getQuotaDaily());
            newUserConfig.setLimitTotal(userConfig == null || userConfig.getDefaultConfig() == 1 ? globalConfig.getUserTotalDaily() : userConfig.getLimitTotal());
            newUserConfig.setLimitDaily(userConfig == null || userConfig.getDefaultConfig() == 1 ? globalConfig.getUserDailyMax() : userConfig.getLimitDaily());
            newUserConfig.setTotalNumber(userConfig == null ? 0 : userConfig.getTotalNumber());
            newUserConfig.setDailyNumber(userConfig == null ? 0 : userConfig.getDailyNumber());
            newUserConfig.setTotalAmount(userConfig == null ? BigDecimal.ZERO : userConfig.getTotalAmount());
            newUserConfig.setDailyAmount(userConfig == null ? BigDecimal.ZERO : userConfig.getDailyAmount());
            userConfig = newUserConfig;
        }

        // 校验币种每日最高交易，单个用户最高交易，用户每日最高交易
        AssertUtil.isTrue(userConfig.getEnablePay() == 1, PSMsgCode.PAY_CLOSE_SILK_PAY);
        // 校验币种交易限额
        AssertUtil.isTrue(!silkPayCoinService.checkCoinDailyMax(unit, amount), PSMsgCode.TRADE_UPPER_LIMIT);
        // 校验币种用户交易限额
        AssertUtil.isTrue(silkPayCoin.getUserTotalMax().compareTo(userConfig.getTotalAmount().add(amount)) > -1, PSMsgCode.TRADE_TOTAL_AMOUNT_LIMIT); // 总可用支付额度不足，无法支付
        AssertUtil.isTrue(silkPayCoin.getUserDailyMax().compareTo(userConfig.getDailyAmount().add(amount)) > -1, PSMsgCode.TRADE_DAILY_AMOUNT_LIMIT); // 今日可用支付额度不足，无法支付

        // 校验用户每日最高限额，用户总额最高限额
        AssertUtil.isTrue(userConfig.getQuotaTotal().compareTo(userConfig.getTotalAmount().add(amount)) > -1, PSMsgCode.TRADE_TOTAL_AMOUNT_LIMIT); // 总可用支付额度不足，无法支付
        AssertUtil.isTrue(userConfig.getQuotaDaily().compareTo(userConfig.getDailyAmount().add(amount)) > -1, PSMsgCode.TRADE_DAILY_AMOUNT_LIMIT); // 今日可用支付额度不足，无法支付

        // 校验用户每日最高限次，用户总次最高限次
        AssertUtil.isTrue(userConfig.getLimitDaily() > userConfig.getDailyNumber(), PSMsgCode.TRADE_TOTAL_NUMBER_LIMIT); // 可用支付次数不足，无法支付
        AssertUtil.isTrue(userConfig.getLimitTotal() > userConfig.getTotalNumber(), PSMsgCode.TRADE_TOTAL_NUMBER_LIMIT); // 可用支付次数不足，无法支付

        // 1, 创建初始订单
        GpsLocation gpsLocation = null;
        if (!Objects.isNull(longitude) && !Objects.isNull(latitude)) {
            gpsLocation = new GpsLocation();
            gpsLocation.setLongitude(longitude);
            gpsLocation.setLatitude(latitude);
        }
        SilkPayOrder silkPayOrder = silkPayOrderService.createOrder(gpsLocation, silkPayCoin, userConfig, member, receiptContent, receiptName, amount, unit);
        // 该步骤已迁移到 creatOrder逻辑中，便于事物管理
       /* // 2, 进行付款匹配（订单金额，地理位置，付款方式）及付款拆分,用户资金预变动
        Boolean aBoolean = silkPayMatchRecordService.payOrSplitOrderMatchRecord(silkPayOrder, gpsLocation);*/
        SilkPayOrderVo silkPayOrderVo = new SilkPayOrderVo();
        BeanUtils.copyProperties(silkPayOrder, silkPayOrderVo);
        // ---- mqtt监听处 3, -----mqtt去接收返回消息，改变相应的状态
        return !Objects.isNull(silkPayOrder) ? success(silkPayOrderVo) : failed();
    }
}
