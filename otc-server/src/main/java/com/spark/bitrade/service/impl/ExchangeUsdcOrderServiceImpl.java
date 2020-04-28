package com.spark.bitrade.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.entity.vo.ExchangeUsdcInfo;
import com.spark.bitrade.enums.OtcExceptionMsg;
import com.spark.bitrade.mapper.ExchangeUsdcOrderMapper;
import com.spark.bitrade.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.trans.TradeTccCancelEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.MessageRespResult;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * USDC兑换记录(ExchangeUsdcOrder)表服务实现类
 *
 * @author ss
 * @date 2020-04-08 16:01:32
 */
@Service("exchangeUsdcOrderService")
public class ExchangeUsdcOrderServiceImpl extends ServiceImpl<ExchangeUsdcOrderMapper,ExchangeUsdcOrder> implements ExchangeUsdcOrderService {
    @Resource
    private ExchangeUsdcOrderMapper exchangeUsdcOrderMapper;

    @Resource
    private AmountOfDiscountRecordService amountOfDiscountRecordService;
    @Resource
    private CurrencyRuleSettingService currencyRuleSettingService;
    @Resource
    private IMemberWalletApiService memberWalletApiService;
    @Resource
    private CurrencyRateService currencyRateService;
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    private final String DISSCONT_COIN = "USDC";


    @Override
    public ExchangeUsdcInfo getPre(Long memberId) {
        ExchangeUsdcInfo exchangeUsdcInfo = new ExchangeUsdcInfo();
        //获取总额度
        BigDecimal totalDiscount = new BigDecimal(currencyRuleSettingService.getCurrencyRuleValueByKey("AGENT_PAY_USDC_MAX",OtcExceptionMsg.NOT_AGENT_PAY_USDC_MAX));
        BigDecimal remainingAmountOfDiscount = totalDiscount.subtract(amountOfDiscountRecordService.getByMemberId(memberId).getUsedAmountOfDiscount());
        if(BigDecimalUtil.lte0(remainingAmountOfDiscount)){
            remainingAmountOfDiscount = BigDecimal.ZERO;
        }
        exchangeUsdcInfo.setRemainingAmountOfDiscount(remainingAmountOfDiscount);
        exchangeUsdcInfo.setCoinUnit(currencyRuleSettingService.getCurrencyRuleValueByKey("USDC_UNIT",OtcExceptionMsg.NOT_USDC_UNIT).toUpperCase());

        MessageRespResult<MemberWallet> walletResult = memberWalletApiService.getWalletByUnit(memberId,exchangeUsdcInfo.getCoinUnit());
        if(walletResult.isSuccess()){
            exchangeUsdcInfo.setBalance(walletResult.getData().getBalance());
        }else {
            exchangeUsdcInfo.setBalance(BigDecimal.ZERO);
        }
        walletResult = memberWalletApiService.getWalletByUnit(Long.valueOf(currencyRuleSettingService.getCurrencyRuleValueByKey("AGENT_USDC_ACCOUNT",OtcExceptionMsg.NOT_AGENT_USDC_ACCOUNT)),"USDC");
        if(walletResult.isSuccess()){
            exchangeUsdcInfo.setUsdcBalance(walletResult.getData().getBalance());
        }else {
            exchangeUsdcInfo.setUsdcBalance(BigDecimal.ZERO);
        }
        exchangeUsdcInfo.setMaxLimit(new BigDecimal(currencyRuleSettingService.getCurrencyRuleValueByKey("USDC_MAX_PAY",OtcExceptionMsg.NOT_USDC_MAX_PAY)));
        exchangeUsdcInfo.setMinLimit(new BigDecimal(currencyRuleSettingService.getCurrencyRuleValueByKey("USDC_MIN_PAY",OtcExceptionMsg.NOT_USDC_MIN_PAY)));
        exchangeUsdcInfo.setRate(currencyRuleSettingService.getCurrencyRuleValueByKey("AGENT_PAY_USDC",OtcExceptionMsg.NOT_AGENT_PAY_USDC));
        exchangeUsdcInfo.setPrice(currencyRateService.toUsdcRate(exchangeUsdcInfo.getCoinUnit()));
        return exchangeUsdcInfo;
    }

    @Override
    @Transactional
    public Boolean exchange(Member member, BigDecimal usdcAmount, BigDecimal exchangeUnitAmount, String jyPassword,BigDecimal price) {
        //交易密码验证
        AssertUtil.hasText(jyPassword, CommonMsgCode.MISSING_JYPASSWORD);
        AssertUtil.hasText(jyPassword, CommonMsgCode.NO_SET_JYPASSWORD);
        String jyPass = new SimpleHash("md5", jyPassword, member.getSalt(), 2).toHex().toLowerCase();
        AssertUtil.isTrue(member.getJyPassword().equals(jyPass), CommonMsgCode.ERROR_JYPASSWORD);
        //获取兑换必要参数
        ExchangeUsdcInfo exchangeUsdcInfo = getPre(member.getId());
        //参数验证 最小，最大限制,价格,余额
        AssertUtil.isTrue(exchangeUsdcInfo.getMaxLimit().compareTo(usdcAmount) >= 0, OtcExceptionMsg.MAX_LIMIT);
        AssertUtil.isTrue(exchangeUsdcInfo.getMinLimit().compareTo(usdcAmount) <= 0, OtcExceptionMsg.MIN_LIMIT);
        AssertUtil.isTrue(exchangeUsdcInfo.getBalance().compareTo(exchangeUnitAmount) >= 0, OtcExceptionMsg.ACCOUNT_BALANCE_INSUFFICIENT);
        AssertUtil.isTrue(exchangeUsdcInfo.getUsdcBalance().compareTo(usdcAmount) >= 0, OtcExceptionMsg.ACCOUNT_BALANCE_INSUFFICIENT);
        AssertUtil.isTrue(exchangeUsdcInfo.getPrice().compareTo(price) == 0, OtcExceptionMsg.PRICE_EXPIRED);
        //校验数量：用usdc数量计算应用多少兑换币兑换
        BigDecimal discountPart;
        if(exchangeUsdcInfo.getRemainingAmountOfDiscount().compareTo(usdcAmount) >= 0){
            //全优惠
            discountPart = usdcAmount;
        }else if(exchangeUsdcInfo.getRemainingAmountOfDiscount().compareTo(BigDecimal.ZERO) == 0){
            //不优惠
            discountPart = BigDecimal.ZERO;
        }else{
            //部分优惠
            discountPart = exchangeUsdcInfo.getRemainingAmountOfDiscount();
        }
        BigDecimal discountAmount = discountPart.multiply(new BigDecimal(exchangeUsdcInfo.getRate()));
        //如果兑换币是BTC：若为BTC支付，显示≈符号和价格，按实时汇率优惠1%
//        if("BTC".equals(exchangeUsdcInfo.getCoinUnit())){
//            discountAmount = discountAmount.add(discountPart.multiply(new BigDecimal(0.01)));
//        }
        exchangeUnitAmount = exchangeUnitAmount.setScale(8,BigDecimal.ROUND_UP);
        AssertUtil.isTrue(BigDecimalUtil.sub(usdcAmount,discountAmount).divide(price, 8, BigDecimal.ROUND_UP).compareTo(exchangeUnitAmount) == 0,OtcExceptionMsg.NUMBER_ERROR);
        //写订单
        ExchangeUsdcOrder order = new ExchangeUsdcOrder();
        order.setOrderSn("A" + IdWorker.getId());
        order.setAmount(usdcAmount);
//        order.setCoinId();
        order.setCoinUnit(DISSCONT_COIN);
        order.setCreateTime(new Date());
        order.setExchangeAmount(exchangeUnitAmount);
        order.setExchangeCoinUnit(exchangeUsdcInfo.getCoinUnit());
        order.setDiscountUsdc(discountAmount);
        order.setExchangeTime(new Date());
//        order.setExchangeCoinId();
        order.setMemberId(member.getId());
        order.setPhone(member.getMobilePhone());
        order.setUpdateTime(new Date());
        int a = exchangeUsdcOrderMapper.insert(order);
        AssertUtil.isTrue(a > 0,OtcExceptionMsg.ORDER_FAIL);
        //修改用户额度
        int size = amountOfDiscountRecordService.updateMemberDiscount(member.getId(),discountPart);
        AssertUtil.isTrue(size > 0,OtcExceptionMsg.ORDER_FAIL);
        //获取总账户ID
        Long totalAccountId = Long.valueOf(currencyRuleSettingService.getCurrencyRuleValueByKey("AGENT_USDC_ACCOUNT",OtcExceptionMsg.NOT_AGENT_USDC_ACCOUNT));
        //校验通过 操作钱包
        WalletTradeEntity reveive=new WalletTradeEntity();
        //1、经纪人减少兑换币
        getReveive(reveive,order.getOrderSn(),member.getId(),exchangeUsdcInfo.getCoinUnit(),exchangeUnitAmount.multiply(new BigDecimal(-1)),"经纪人快捷购币");
        MessageRespResult<WalletChangeRecord> agentExchangeResult = memberWalletApiService.tradeTccTry(reveive);
        log.error("经纪人优惠购币兑换币扣除兑换币结果：" + JSON.toJSONString(agentExchangeResult));
        AssertUtil.isTrue(!(agentExchangeResult.getData() == null && agentExchangeResult.getCode() == 6010),OtcExceptionMsg.ACCOUNT_BALANCE_INSUFFICIENT);
        //2、扣除 经纪人优惠兑换USDC总账号的USDC
        getReveive(reveive,order.getOrderSn(),totalAccountId,DISSCONT_COIN,usdcAmount.multiply(new BigDecimal(-1)),"经纪人"+ member.getId() +"兑换USDC");
        MessageRespResult<WalletChangeRecord> exchangeUsdcResult = memberWalletApiService.tradeTccTry(reveive);
        log.error("经纪人优惠购币出售总账户扣除USDC结果：" + JSON.toJSONString(exchangeUsdcResult));
        //3、经纪人增加USDC
        getReveive(reveive,order.getOrderSn(),member.getId(),DISSCONT_COIN,usdcAmount,"经纪人快捷购币");
        MessageRespResult<WalletChangeRecord> agentUsdcResult = memberWalletApiService.tradeTccTry(reveive);
        log.error("经纪人优惠购币增加USDC结果：" + JSON.toJSONString(agentUsdcResult));
        //4、增加 经纪人优惠兑换USDC总账号的兑换币数量
        getReveive(reveive,order.getOrderSn(),totalAccountId,exchangeUsdcInfo.getCoinUnit(),exchangeUnitAmount,"经纪人"+ member.getId() +"兑换USDC");
        MessageRespResult<WalletChangeRecord> exchangeResult = memberWalletApiService.tradeTccTry(reveive);
        log.error("经纪人优惠购币兑换币总账户增加兑换币结果：" + JSON.toJSONString(exchangeResult));
        if(agentUsdcResult.isSuccess() && agentUsdcResult.getData() != null &&
                agentExchangeResult.isSuccess() && agentExchangeResult.getData() != null &&
                exchangeUsdcResult.isSuccess() && exchangeUsdcResult.getData() != null &&
                exchangeResult.isSuccess() && exchangeResult.getData() != null){
            //钱包操作try成功
            try{
                MessageRespResult<Boolean> result1 = memberWalletApiService.tradeTccConfirm(member.getId(),agentUsdcResult.getData().getId());
                MessageRespResult<Boolean> result2 = memberWalletApiService.tradeTccConfirm(member.getId(),agentExchangeResult.getData().getId());
                MessageRespResult<Boolean> result3 = memberWalletApiService.tradeTccConfirm(totalAccountId,exchangeUsdcResult.getData().getId());
                MessageRespResult<Boolean> result4 = memberWalletApiService.tradeTccConfirm(totalAccountId,exchangeResult.getData().getId());
                if(result1.getData() && result2.getData() && result3.getData() && result4.getData()){
                    //操作成功
                    return true;
                }else{
                    //回滚
                }
            }catch (Exception e){
                log.error("钱包确认操作失败",e);
            }
        }else if ((agentUsdcResult.getData() == null && agentUsdcResult.getCode() == 6010) ||
                (agentExchangeResult.getData() == null && agentExchangeResult.getCode() == 6010) ||
        (exchangeUsdcResult.getData() == null && exchangeUsdcResult.getCode() == 6010) ||
        (exchangeResult.getData() == null && exchangeResult.getCode() == 6010)){
            log.error(OtcExceptionMsg.ACCOUNT_BALANCE_INSUFFICIENT.getMessage());
        }
        //回滚
        try {
            MessageRespResult<Boolean> r1 = agentUsdcResult.getData() == null ? null : memberWalletApiService.tradeTccCancel(member.getId(),agentUsdcResult.getData().getId());
            MessageRespResult<Boolean> r2 = agentExchangeResult.getData() == null ? null : memberWalletApiService.tradeTccCancel(member.getId(),agentExchangeResult.getData().getId());
            MessageRespResult<Boolean> r3 = exchangeUsdcResult.getData() == null ? null : memberWalletApiService.tradeTccCancel(totalAccountId,exchangeUsdcResult.getData().getId());
            MessageRespResult<Boolean> r4 = exchangeResult.getData() == null ? null : memberWalletApiService.tradeTccCancel(totalAccountId,exchangeResult.getData().getId());
        }catch (Exception e){
            log.error("资金回滚异常: " ,e);
            log.error("资金回滚异常--》" +agentUsdcResult.getCode() + "----" + agentUsdcResult.getMessage());
            log.error("资金回滚异常--》" +agentExchangeResult.getCode() + "----" + agentExchangeResult.getMessage());
            log.error("资金回滚异常--》" +exchangeUsdcResult.getCode() + "----" + exchangeUsdcResult.getMessage());
            log.error("资金回滚异常--》" +exchangeResult.getCode() + "----" + exchangeResult.getMessage());
            try {
                if(agentUsdcResult.getData() == null){
                    kafkaTemplate.send("acct-trade-tcc-cancel", "tcc-cancel",JSON.toJSONString(new TradeTccCancelEntity(member.getId(), agentUsdcResult.getData().getId())));
                }
                if(agentExchangeResult.getData() == null){
                    kafkaTemplate.send("acct-trade-tcc-cancel", "tcc-cancel",JSON.toJSONString(new TradeTccCancelEntity(member.getId(), agentExchangeResult.getData().getId())));
                }
                if(exchangeUsdcResult.getData() == null){
                    kafkaTemplate.send("acct-trade-tcc-cancel", "tcc-cancel",JSON.toJSONString(new TradeTccCancelEntity(totalAccountId, exchangeUsdcResult.getData().getId())));
                }
                if(exchangeResult.getData() == null){
                    kafkaTemplate.send("acct-trade-tcc-cancel", "tcc-cancel",JSON.toJSONString(new TradeTccCancelEntity(totalAccountId, exchangeResult.getData().getId())));
                }
            }
            catch (Exception ex) {
                AssertUtil.notNull(null, OtcExceptionMsg.ORDER_FAIL);
            }
        }
        AssertUtil.notNull(null, OtcExceptionMsg.ORDER_FAIL);
        return false;
    }
    public void getReveive(WalletTradeEntity reveive,String orderSn,Long memberId,String unit,BigDecimal amount,String comment){
        reveive.setType(TransactionType.AGENT_BUY_USDC);
        reveive.setRefId(orderSn);
        reveive.setMemberId(memberId);
        reveive.setCoinUnit(unit);
        reveive.setTradeBalance(amount);
        reveive.setComment(comment);
    }
}