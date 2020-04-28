package com.spark.bitrade.biz.impl;

import com.spark.bitrade.biz.IPayRecordService;
import com.spark.bitrade.constant.ModuleType;
import com.spark.bitrade.constant.SupportCoinMsgCode;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.entity.SupportConfig;
import com.spark.bitrade.entity.SupportConfigList;
import com.spark.bitrade.entity.SupportPayRecords;
import com.spark.bitrade.service.*;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *  
 *    支付业务
 *  @author liaoqinghui  
 *  @time 2019.11.05 16:50  
 */
@Service
public class PayRecordServiceImpl implements IPayRecordService {

    @Autowired
    private ICoinExchange coinExchange;
    @Autowired
    private SupportConfigListService supportConfigListService;
    @Autowired
    private SupportConfigService supportConfigService;
    @Autowired
    private IMemberWalletApiService memberWalletApiService;
    @Autowired
    private ICoinApiService coinApiService;

    public SupportPayRecords generatePayRecord(Long memberId,
                                               Long upCoinId,
                                               ModuleType moduleType,
                                               String payCoin,
                                               String remark,
                                               BigDecimal usdtAmount) {

        SupportPayRecords records=new SupportPayRecords();
        records.setMemberId(memberId);
        records.setUpCoinId(upCoinId);
        records.setModuleType(moduleType);
        //支付
        records.setPayType(0);
        records.setPayCoin(payCoin);
        records.setRemark(remark);

        //获取汇率 payCoin相对usdt的汇率
        MessageRespResult<BigDecimal> usdExchangeRate = coinExchange.getUsdExchangeRate(payCoin);
        AssertUtil.isTrue(usdExchangeRate.isSuccess(), SupportCoinMsgCode.HUILV_FIND_FAILED);
        AssertUtil.isTrue(usdExchangeRate.getData().compareTo(BigDecimal.ZERO)>0,SupportCoinMsgCode.USDT_RATE_GET_FAILED);
        SupportConfig config = supportConfigService.findConfigByModule(moduleType);
        String configKey=config.getConfigKey();
        //读取配置
        //SupportConfigList usdt = supportConfigListService.findByKey(configKey, "USDT");
        //AssertUtil.notNull(usdt, SupportCoinMsgCode.CONFIG_LIST_NOT_FIND);

        SupportConfigList payConfig = supportConfigListService.findByKey(configKey, payCoin);
        AssertUtil.notNull(payConfig, SupportCoinMsgCode.CONFIG_LIST_NOT_FIND);

        BigDecimal payAmount=usdtAmount.divide(usdExchangeRate.getData(),8,RoundingMode.HALF_UP);

        AssertUtil.isTrue(payAmount.compareTo(new BigDecimal(payConfig.getDictValue()))<=0,
                SupportCoinMsgCode.PAY_AMOUNT_MUST_BE_LOWER_CONFIG);

        records.setPayAmount(payAmount);
        MessageRespResult<String> coinNameByUnit = coinApiService.getCoinNameByUnit(payCoin);
        AssertUtil.isTrue(coinNameByUnit.isSuccess(),SupportCoinMsgCode.COIN_FIND_FAILED);
        MessageRespResult<MemberWallet> wallet = memberWalletApiService.getWallet(memberId, coinNameByUnit.getData());
        AssertUtil.isTrue(wallet.isSuccess()&&wallet.getData()!=null,SupportCoinMsgCode.GET_WALLET_FAILED);
        MemberWallet memberWallet = wallet.getData();
        AssertUtil.isTrue(memberWallet.getBalance().compareTo(payAmount)>=0,SupportCoinMsgCode.ACCOUNT_BALANCE_INSUFFICIENT);
        return records;
    }



}
