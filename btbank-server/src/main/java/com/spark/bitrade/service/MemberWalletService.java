package com.spark.bitrade.service;

import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constant.WalletChangeType;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class MemberWalletService {
    @Autowired
    private IMemberWalletApiService memberWalletApiService;

    /**
     * 远程操作钱包余额
     *
     * @param memberId
     * @param coinId
     * @param unit
     * @param balance
     * @param minerTxId
     * @return com.spark.bitrade.util.MessageRespResult
     * @author zhangYanjun
     * @time 2019.10.02 20:40
     */
    public MessageRespResult optionMemberWalletBalance(Long memberId, String coinId, String unit, BigDecimal balance, Long minerTxId,String comment) {
        WalletTradeEntity tradeEntity = new WalletTradeEntity();
        tradeEntity.setType(TransactionType.TRANSFER_ACCOUNTS);
        tradeEntity.setRefId(minerTxId.toString());
        tradeEntity.setChangeType(WalletChangeType.TRADE);
        tradeEntity.setMemberId(memberId);
        tradeEntity.setCoinId(coinId);
        tradeEntity.setCoinUnit(unit);
        tradeEntity.setTradeBalance(balance);
        tradeEntity.setComment(comment);


        MessageRespResult<Boolean> memberWalletMessageRespResult =
                memberWalletApiService.trade(tradeEntity);
        ExceptionUitl.throwsMessageCodeExceptionIfFailed(memberWalletMessageRespResult);

        return memberWalletMessageRespResult;
    }
}
