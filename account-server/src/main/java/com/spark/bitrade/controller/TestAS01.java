package com.spark.bitrade.controller;

import com.spark.bitrade.trans.WalletBaseEntity;

import java.math.BigDecimal;

import com.spark.bitrade.service.IWalletExchangeService;
import com.spark.bitrade.trans.ServiceChargeEntity;
import com.spark.bitrade.constant.WalletChangeType;
import com.spark.bitrade.constant.TransactionType;

import com.spark.bitrade.service.IWalletTradeService;
import com.spark.bitrade.trans.WalletExchangeEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.resubmit.annotation.ForbidResubmit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *  
 *
 * @author young
 * @time 2019.06.18 09:33
 */

@RestController
@RequestMapping("api/v2/tas01")
public class TestAS01 extends ApiController {
    //    @Autowired
//    private ICoinExchange coinExchange;
    @Autowired
    private IWalletTradeService walletTradeService;
    @Autowired
    private MemberWalletController memberWalletController;
    @Autowired
    private IWalletExchangeService walletExchangeService;

    //测试禁止重复提交
    @ForbidResubmit(interdictTime = 20)
    @RequestMapping(value = {"/t1", "/no-auth/t1"}, method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult add(String coin) {
        /*System.out.println(coinExchange.getCnyExchangeRate(coin));
        System.out.println(coinExchange.getUsdExchangeRate(coin));
        System.out.println(coinExchange.getCnytExchangeRate(coin));
        System.out.println(coinExchange.getUsdCnyRate());*/

        System.out.println("---coin----" + coin);
        System.out.println("getApiKey=" + getApiKey());
        System.out.println("getAppId=" + getAppId());

        return success("------ok---------" + coin);
    }

    //单个账户交易测试
    @RequestMapping(value = "/t2", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult t2() {
        WalletTradeEntity tradeEntity = new WalletTradeEntity();
        tradeEntity.setType(TransactionType.TRANSFER);
        tradeEntity.setRefId("test0000002");
        tradeEntity.setChangeType(WalletChangeType.TRADE);
        tradeEntity.setMemberId(71639L);
        //tradeEntity.setCoinId("EOS");
        tradeEntity.setCoinUnit("EOS");
        tradeEntity.setTradeBalance(new BigDecimal("100"));
        tradeEntity.setTradeFrozenBalance(new BigDecimal("0"));
        tradeEntity.setTradeLockBalance(new BigDecimal("0"));
        tradeEntity.setComment("交易测试02");
        tradeEntity.setServiceCharge(new ServiceChargeEntity());


        return success("------ok---------" + memberWalletController.trade(tradeEntity));
        //return success("------ok---------"+walletTradeService.trade(tradeEntity));
    }

    //单个账户交易测试
    @RequestMapping(value = "/t3", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult t3() {
        WalletTradeEntity tradeEntity = new WalletTradeEntity();
        tradeEntity.setType(TransactionType.TRANSFER);
        tradeEntity.setRefId("test0000003");
        tradeEntity.setChangeType(WalletChangeType.TRADE);
        tradeEntity.setMemberId(71639L);
        tradeEntity.setCoinId("EOS");
        tradeEntity.setCoinUnit("EOS");
        tradeEntity.setTradeBalance(new BigDecimal("-10"));
        tradeEntity.setTradeFrozenBalance(new BigDecimal("10"));
        tradeEntity.setTradeLockBalance(new BigDecimal("0"));
        tradeEntity.setComment("交易测试03");
        tradeEntity.setServiceCharge(new ServiceChargeEntity());

        return success("------ok---------" + walletTradeService.trade(tradeEntity));
    }

    //单个账户交易测试(tcc try测试)
    @RequestMapping(value = "/tcc01", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult tcc_try01() {
        WalletTradeEntity tradeEntity = new WalletTradeEntity();
        tradeEntity.setType(TransactionType.TRANSFER);
        tradeEntity.setRefId("tcc-test0000001");
        tradeEntity.setChangeType(WalletChangeType.TRADE);
        tradeEntity.setMemberId(71639L);
        tradeEntity.setCoinId("EOS");
        tradeEntity.setCoinUnit("EOS");
        //case1
//        tradeEntity.setTradeBalance(new BigDecimal("-100")); //负数
//        tradeEntity.setTradeFrozenBalance(new BigDecimal("-200"));
//        tradeEntity.setTradeLockBalance(new BigDecimal("-300"));

        //case2
        tradeEntity.setTradeBalance(new BigDecimal("-100")); //负数
        tradeEntity.setTradeFrozenBalance(new BigDecimal("200"));
        tradeEntity.setTradeLockBalance(new BigDecimal("300"));

        tradeEntity.setComment("交易测试tcc1");
        tradeEntity.setServiceCharge(new ServiceChargeEntity());

        return success("------ok---------" + walletTradeService.tradeTccTry(tradeEntity));
    }
    @RequestMapping(value = "/tcc02", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult tcc_try02() {
        WalletTradeEntity tradeEntity = new WalletTradeEntity();
        tradeEntity.setType(TransactionType.TRANSFER);
        tradeEntity.setRefId("tcc-test0000002");
        tradeEntity.setChangeType(WalletChangeType.TRADE);
        tradeEntity.setMemberId(71639L);
        tradeEntity.setCoinId("EOS");
        tradeEntity.setCoinUnit("EOS");
        //case1
//        tradeEntity.setTradeBalance(new BigDecimal("100")); //整数
//        tradeEntity.setTradeFrozenBalance(new BigDecimal("200"));
//        tradeEntity.setTradeLockBalance(new BigDecimal("300"));

        //case2
        tradeEntity.setTradeBalance(new BigDecimal("100")); //整数
        tradeEntity.setTradeFrozenBalance(new BigDecimal("-200"));
        tradeEntity.setTradeLockBalance(new BigDecimal("-300"));

        tradeEntity.setComment("交易测试tcc2");
        tradeEntity.setServiceCharge(new ServiceChargeEntity());

        return success("------ok---------" + walletTradeService.tradeTccTry(tradeEntity));
    }

    //单个账户交易测试(tcc confirm 测试)
    @RequestMapping(value = "/tcc2", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult tcc_confirm2(@RequestParam("id") Long walletChangeRecordId) {
        long memberId = 71639L;
        return success("----ok---" + walletTradeService.tradeTccConfirm(memberId, walletChangeRecordId));
    }

    //单个账户交易测试(tcc cancel 测试)
    @RequestMapping(value = "/tcc3", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult tcc_cancel3(@RequestParam("id") Long walletChangeRecordId) {
        long memberId = 71639L;
        return success("----ok---" + walletTradeService.tradeTccCancel(memberId, walletChangeRecordId));
    }


    //相同账户，使用不同币种的交易测试
    @RequestMapping(value = "/te1", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult te1() {
        WalletExchangeEntity exchangeEntity = new WalletExchangeEntity();
        exchangeEntity.setMemberId(71639L);
        exchangeEntity.setType(TransactionType.EXCHANGE_FAST);
        exchangeEntity.setRefId("EXCHANGE_FAST-001");
        //exchangeEntity.setChangeType(new WalletChangeType());

        WalletBaseEntity sourceEntity = new WalletBaseEntity();
        sourceEntity.setCoinId("EOS");
        sourceEntity.setCoinUnit("EOS");
        sourceEntity.setTradeBalance(new BigDecimal("100"));
        sourceEntity.setTradeFrozenBalance(new BigDecimal("90"));
        sourceEntity.setTradeLockBalance(new BigDecimal("80"));
        sourceEntity.setServiceCharge(new ServiceChargeEntity());
        sourceEntity.setComment("闪兑测试-兑换币");

        exchangeEntity.setSource(sourceEntity);

        WalletBaseEntity targetEntity = new WalletBaseEntity();
        targetEntity.setCoinId("CNYT");
        targetEntity.setCoinUnit("CNYT");
        targetEntity.setTradeBalance(new BigDecimal("-1000"));
        targetEntity.setTradeFrozenBalance(new BigDecimal("-900"));
        targetEntity.setTradeLockBalance(new BigDecimal("-800"));
        targetEntity.setServiceCharge(new ServiceChargeEntity());
        targetEntity.setComment("闪兑测试-兑换基本");

        exchangeEntity.setTarget(targetEntity);

        return success("------ok---------" + walletExchangeService.exchange(exchangeEntity));
    }
}
