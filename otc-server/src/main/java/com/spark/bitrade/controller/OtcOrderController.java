package com.spark.bitrade.controller;

import com.spark.bitrade.common.RedisUtil;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.MemberLevelEnum;
import com.spark.bitrade.constant.OrderStatus;
import com.spark.bitrade.constant.SysConstant;
import com.spark.bitrade.entity.Advertise;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.OtcCoin;
import com.spark.bitrade.entity.OtcOrder;
import com.spark.bitrade.enums.AdvertiseControlStatus;
import com.spark.bitrade.enums.AdvertiseType;
import com.spark.bitrade.enums.OtcExceptionMsg;
import com.spark.bitrade.enums.PriceType;
import com.spark.bitrade.exception.UnexpectedException;
import com.spark.bitrade.service.AdvertiseService;
import com.spark.bitrade.service.IMemberApiService;
import com.spark.bitrade.service.OtcCoinService;
import com.spark.bitrade.service.OtcOrderService;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.GeneratorUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.MessageResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Arrays;

import static com.spark.bitrade.constant.PayMode.*;
import static com.spark.bitrade.constant.PayMode.EPAY;
import static com.spark.bitrade.util.BigDecimalUtils.*;
import static com.spark.bitrade.util.MessageResult.error;

/**
 * (OtcOrder)表控制层
 *
 * @author ss
 * @date 2020-03-19 10:23:51
 */
@RestController
@RequestMapping("otcOrder")
public class OtcOrderController  extends ApiController{
    /**
     * 服务对象
     */
    @Resource
    private OtcOrderService otcOrderService;
    @Resource
    private AdvertiseService advertiseService;
    @Resource
    private OtcCoinService otcCoinService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private IMemberApiService memberService;

    @Value("${trade.orderNum}")
    private Integer orderNum;
    @Value("${trade.orderCancleNum}")
    private Integer orderCancleNum;
//
//    /**
//     * 买币
//     * TODO 用户默认法币验证
//     * @param id
//     * @param coinId
//     * @param price
//     * @param money
//     * @param amount
//     * @param remark
//     * @param member
//     * @return
//     * @throws UnexpectedException
//     */
//    @RequestMapping(value = "buy", method = RequestMethod.POST)
//    public MessageRespResult buy(long id, long coinId, BigDecimal price, BigDecimal money,
//                                 BigDecimal amount, String remark,
//                                 @RequestParam(value = "mode", defaultValue = "0") Integer mode,
//                                 @MemberAccount Member member, HttpServletRequest request) throws UnexpectedException {
//        //add by tansitao 时间： 2018/5/14 原因：用户买币增加限制
//        AssertUtil.isTrue(otcOrderService.isAllowTrade(member.getId(), orderNum), OtcExceptionMsg.NO_ALLOW_TRADE);
//        AssertUtil.isTrue(StringUtils.isEmpty(member.getTransactionStatus()) || member.getTransactionStatus() == BooleanEnum.IS_TRUE, OtcExceptionMsg.NO_ALLOW_TRANSACT);
//        advertiseService.validateOpenExPitTransaction(member.getId(), OtcExceptionMsg.NO_ALLOW_TRANSACT_BUY, AdvertiseType.BUY.getCode());
//
//        //edit by tansitao 时间： 2018/7/17 原因：修改为只读数据库操作
//        Advertise advertise = advertiseService.getById(id);
//        OtcCoin otcCoin = otcCoinService.getById(advertise.getCoinId());
//
//
//        //del by tansitao 时间： 2018/10/31 原因：取消精度为币种配置的精度
//        AssertUtil.isTrue(otcOrderService.isEqualIgnoreTailPrecision(mulRound(amount, price, 2), money),OtcExceptionMsg.NUMBER_ERROR);//edit by tansitao 时间： 2018/8/31 原因：修改精度
//        //add by tansitao 时间： 2018/10/31 原因：取消对数量和金额、价格的关系判断，直接计算出价格对应的数量
////        amount = money.divide(price, otcCoin.getCoinScale(), BigDecimal.ROUND_DOWN);
//        //add by tansitao 时间： 2018/10/26 原因：增加广告的身份证、手机绑定、交易次数等限制判断
//        if (advertise.getNeedBindPhone() == BooleanEnum.IS_TRUE.getOrdinal()) {
//            AssertUtil.isTrue(!StringUtils.isEmpty(member.getMobilePhone()), OtcExceptionMsg.NOT_BIND_PHONE);
//        }
//        if (advertise.getNeedRealname() == BooleanEnum.IS_TRUE.getOrdinal()) {
//            AssertUtil.isTrue(!StringUtils.isEmpty(member.getRealName()),OtcExceptionMsg.NO_REAL_NAME);
//        }
//        if (advertise.getNeedTradeTimes() > 0) {
//            AssertUtil.isTrue(member.getTransactions() >= advertise.getNeedTradeTimes(), OtcExceptionMsg.TRANSACTIONS_NOT_ENOUGH);
//        }
//        if (advertise.getMaxTradingOrders() > 0) {
//            //add by tansitao 时间： 2018/11/19 原因：判断交易中的订单数是否超过配置中的最大订单数
//            Integer onlineNum = (Integer) redisUtil.getHash(SysConstant.C2C_MONITOR_ORDER + advertise.getMemberId() + "-" + advertise.getId(), SysConstant.C2C_ONLINE_NUM);
//            AssertUtil.isTrue(onlineNum == null || onlineNum < advertise.getMaxTradingOrders(), OtcExceptionMsg.MAX_TRADING_ORDERS);
//        }
//
//
//        AssertUtil.isTrue(advertise.getStatus().equals(AdvertiseControlStatus.PUT_ON_SHELVES), OtcExceptionMsg.ALREADY_PUT_OFF);
//        AssertUtil.isTrue(compare(money, advertise.getMinLimit()), OtcExceptionMsg.MONEY_MIN);
//        AssertUtil.isTrue(compare(advertise.getMaxLimit(), money),OtcExceptionMsg.MONEY_MAX);
//
//        Member bMember = memberService.getMember(advertise.getMemberId()).getData();
//        // by wsy, date: 2019-1-23 14:48:52，reason: 限制商家与商家交易， 卖家和买家角色都需要判断
//        AssertUtil.isTrue(bMember.getMemberLevel() != MemberLevelEnum.IDENTIFICATION || member.getMemberLevel() != MemberLevelEnum.IDENTIFICATION, OtcExceptionMsg.SELLER_ALLOW_TRADE);
//
//        AssertUtil.isTrue(!member.getUsername().equals(bMember.getUsername()), OtcExceptionMsg.NOT_ALLOW_BUY_BY_SELF);
//
//
//        if (advertise == null || !AdvertiseType.SELL.getCode().equals(advertise.getAdvertiseType())) {
//            return failed(OtcExceptionMsg.PARAMETER_ERROR);
//        }
//        if (otcCoin.getId() != coinId) {
//            return failed(OtcExceptionMsg.PARAMETER_ERROR);
//        }
//        if (PriceType.REGULAR.getCode().equals(advertise.getPriceType())) {
//            AssertUtil.isTrue(isEqual(price, advertise.getPrice()), OtcExceptionMsg.PRICE_EXPIRED);
//        } else {
//            //TODO 获取法币价格
//            BigDecimal marketPrice = BigDecimal.TEN;
//            BigDecimal premiseRate = advertise.getPremiseRate().divide(new BigDecimal(100), otcCoin.getCoinScale(), BigDecimal.ROUND_HALF_UP);
//            if (AdvertiseType.SELL.getCode().equals(advertise.getAdvertiseType())) {
//                premiseRate = BigDecimal.ONE.add(premiseRate);
//            } else {
//                premiseRate = BigDecimal.ONE.subtract(premiseRate);
//            }
//            BigDecimal _price = mulRound(premiseRate, marketPrice, otcCoin.getCoinScale());
//            //edit by tansitao 时间： 2018/10/26 原因：修改精度为配置的精度
//            AssertUtil.isTrue(isEqual(price, _price), OtcExceptionMsg.PRICE_EXPIRED);
//        }
//
//        //计算手续费
//        BigDecimal commission = mulRound(amount, getRate(otcCoin.getJyRate()));
//        //手续费折扣率
//        BigDecimal feeDiscount = BigDecimal.ZERO;
//        if (otcCoin.getFeeSellDiscount().compareTo(BigDecimal.ONE) >= 0) {
//            feeDiscount = commission;
//        } else {
//            feeDiscount = commission.multiply(otcCoin.getFeeSellDiscount());
//            BigDecimal remainingFee = commission.subtract(feeDiscount); //优惠后的当前手续费
//            //计算 当前会员可优惠手续费数量
//            BigDecimal memberFeeDiscount = remainingFee.multiply(
//                    businessDiscountRuleService.getDiscountRule(bMember.getId(),
//                            otcCoin.getUnit()).getSellDiscount());
//
//            feeDiscount = feeDiscount.add(memberFeeDiscount).setScale(8, BigDecimal.ROUND_DOWN);
//        }
//        commission = commission.subtract(feeDiscount);
//        //edit by yangch 时间： 2018.07.31 原因：优化事务
//        BigDecimal buyAmount = add(commission, amount);
//        //AssertUtil.isTrue(compare(advertise.getRemainAmount(), add(commission, amount)), msService.getMessage("AMOUNT_NOT_ENOUGH"));
//        AssertUtil.isTrue(compare(advertise.getRemainAmount(), buyAmount), msService.getMessage("AMOUNT_NOT_ENOUGH"));
//        /*if (!advertiseService.updateAdvertiseAmountForBuy(advertise.getId(), add(commission, amount))) {
//            throw new UnexpectedException(String.format("OTCGM001：%s", msService.getMessage("CREATE_ORDER_FAILED")));
//        }*/
//
//
//        //add by zyj 2018.12.27 : 接入风控
//        TradeCashInfo tradeCashInfo = new TradeCashInfo();
//        tradeCashInfo.setDirection(0);
//        tradeCashInfo.setCoin(otcCoin.getUnit());
//        tradeCashInfo.setAmount(amount);
//        tradeCashInfo.setTargetUser(memberService.findOne(advertise.getBMemberId()));
//
//        MessageResult res = risk(request, null, member, tradeCashInfo);
//        if (res.getCode() != 0) {
//            return error(res.getMessage());
//        }
//
//        //add by tansitao 时间： 2018/11/9 原因：增加卖家和买家的账户姓名处理，有账户的用账户姓名，没有的用真实姓名
//        MemberPaymentAccount cmemberPaymentAccount = memberPaymentAccountService.findPaymentAccountByMemberId(member.getId());
//        MemberPaymentAccount bmemberPaymentAccount = memberPaymentAccountService.findPaymentAccountByMemberId(bMember.getId());
//        String caccountName = cmemberPaymentAccount == null || StringUtils.isEmpty(cmemberPaymentAccount.getAccountName())
//                ? member.getRealName() : cmemberPaymentAccount.getAccountName();
//        String baccountName = bmemberPaymentAccount == null || StringUtils.isEmpty(bmemberPaymentAccount.getAccountName())
//                ? bMember.getRealName() : bmemberPaymentAccount.getAccountName();
//
//        String randomCode = String.valueOf(GeneratorUtil.getRandomNumber(1000, 9999));
//        Order order = new Order();
//        order.setStatus(OrderStatus.NONPAYMENT);
//        order.setAdvertiseId(advertise.getId());
//        order.setAdvertiseType(advertise.getAdvertiseType());
//        order.setCoin(otcCoin);
//        order.setCommission(commission);
//        order.setCountry(advertise.getCountryName());
//        order.setCustomerId(user.getId());
//        order.setCustomerName(user.getName());
//        order.setCustomerRealName(caccountName);
//        order.setMemberId(bMember.getId());
//        order.setMemberName(bMember.getUsername());
//        order.setMemberRealName(baccountName);
//        order.setMaxLimit(advertise.getMaxLimit());
//        order.setMinLimit(advertise.getMinLimit());
//        order.setMoney(money);
//        order.setNumber(amount);
//        order.setPayMode(advertise.getPayMode());
//        order.setPrice(price);
//        order.setRemark(remark);
//        order.setTimeLimit(advertise.getTimeLimit());
//        order.setPayCode(randomCode);
//
//        // 买币服务费默认填充
//        order.setOrderMoney(order.getMoney());
//        order.setServiceRate(new BigDecimal(0));
//        order.setServiceMoney(new BigDecimal(0));
//
//        String[] pay = advertise.getPayMode().split(",");
//        Arrays.stream(pay).forEach(x -> {
//            if (ALI.getCnName().equals(x)) {
//                order.setAlipay(bMember.getAlipay());
//            } else if (WECHAT.getCnName().equals(x)) {
//                order.setWechatPay(bMember.getWechatPay());
//            } else if (BANK.getCnName().equals(x)) {
//                order.setBankInfo(bMember.getBankInfo());
//            } else if (EPAY.getCnName().equals(x)) {
//                MemberPaymentAccount memberPaymentAccount = memberPaymentAccountService.findPaymentAccountByMemberId(bMember.getId());
//                if (memberPaymentAccount != null) {
//                    Epay epay = new Epay();
//                    epay.setEpayNo(memberPaymentAccount.getEpayNo());
//                    order.setEpay(epay);
//                }
//            }
//        });
//
//        //edit by yangch 时间： 2018.07.31 原因：优化事务
//        //Order order1 = orderService.saveOrder(order);
//        Order order1 = orderService.buyOrder(order, advertise, buyAmount);
//        if (order1 != null) {
//            /**
//             * 下单后，将自动回复记录添加到mongodb
//             */
//            if (advertise.getAuto() == BooleanEnum.IS_TRUE && !StringUtils.isEmpty(advertise.getAutoword())) {
//                //edit by yangch 时间： 2018.07.12 原因：修改为异步推送自动回复内容
//                pushOrderMessageService.pushAutoResponseMessage2Mongodb(advertise, order1);
//            }
//
//            //edit by zhongxj 统一在kafka事件消费的时候，推送不同渠道的消息 20190929
////            pushOrderMessageService.pushCreateOrderMessage4SMS(advertise, order1, user);
//
//            //add by tansitao 时间： 2018/11/19 原因：下单成功，redis中的订单数加一
//            redisCountorService.addOrSubtractHash(SysConstant.C2C_MONITOR_ORDER + advertise.getBMemberId() + "-" + advertise.getId(), SysConstant.C2C_ONLINE_NUM, 1L);
//
//            getService().creatOrderEvent(order1, user);
//            MessageResult result = MessageResult.success(msService.getMessage("CREATE_ORDER_SUCCESS"));
//            result.setData(order1.getOrderSn().toString());
//            return result;
//        } else {
//            throw new UnexpectedException(String.format("OTCGM002：%s", msService.getMessage("CREATE_ORDER_FAILED")));
//        }
//    }



}
