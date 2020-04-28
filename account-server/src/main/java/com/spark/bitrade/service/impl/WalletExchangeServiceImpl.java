package com.spark.bitrade.service.impl;

//import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.spark.bitrade.constants.AcctMsgCode;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.IWalletExchangeService;
import com.spark.bitrade.service.IWalletTradeService;
import com.spark.bitrade.trans.WalletExchangeEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.ExceptionUitl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *  币种交换接口 备注：币种交换（如：币币交易）
 *
 * @author young
 * @time 2019.06.24 18:40
 */
@Slf4j
@Service("walletExchangeServiceImpl")
public class WalletExchangeServiceImpl implements IWalletExchangeService {
    @Autowired
    private IWalletTradeService walletTradeService;

    @Override
//    @LcnTransaction
    @Transactional(rollbackFor = Exception.class)
    public boolean exchange(WalletExchangeEntity exchangeEntity) throws MessageCodeException {
        log.info("========币种交换========开始==={}", exchangeEntity);
        //发起交易的币种实体
        WalletTradeEntity sourceEntity = new WalletTradeEntity();
        sourceEntity.setMemberId(exchangeEntity.getMemberId());
        sourceEntity.setCoinId(exchangeEntity.getSource().getCoinId());
        sourceEntity.setTradeBalance(exchangeEntity.getSource().getTradeBalance());
        sourceEntity.setTradeFrozenBalance(exchangeEntity.getSource().getTradeFrozenBalance());
        sourceEntity.setTradeLockBalance(exchangeEntity.getSource().getTradeLockBalance());
        sourceEntity.setType(exchangeEntity.getType());
        sourceEntity.setRefId(exchangeEntity.getRefId());
        sourceEntity.setCoinUnit(exchangeEntity.getSource().getCoinUnit());
        sourceEntity.setChangeType(exchangeEntity.getChangeType());
        sourceEntity.setComment(exchangeEntity.getSource().getComment());
        sourceEntity.setServiceCharge(exchangeEntity.getSource().getServiceCharge());

        //接收交易的币种实体
        WalletTradeEntity targetEntity = new WalletTradeEntity();
        targetEntity.setMemberId(exchangeEntity.getMemberId());
        targetEntity.setCoinId(exchangeEntity.getTarget().getCoinId());
        targetEntity.setTradeBalance(exchangeEntity.getTarget().getTradeBalance());
        targetEntity.setTradeFrozenBalance(exchangeEntity.getTarget().getTradeFrozenBalance());
        targetEntity.setTradeLockBalance(exchangeEntity.getTarget().getTradeLockBalance());
        targetEntity.setType(exchangeEntity.getType());
        targetEntity.setRefId(exchangeEntity.getRefId());
        targetEntity.setCoinUnit(exchangeEntity.getTarget().getCoinUnit());
        targetEntity.setChangeType(exchangeEntity.getChangeType());
        targetEntity.setComment(exchangeEntity.getTarget().getComment());
        targetEntity.setServiceCharge(exchangeEntity.getTarget().getServiceCharge());

        //先减后加
        if (BigDecimalUtil.lt0(sourceEntity.getTradeBalance())) {
            if (!walletTradeService.trade(sourceEntity)) {
                ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
            }
            if (!walletTradeService.trade(targetEntity)) {
                ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
            }
        } else {
            if (!walletTradeService.trade(targetEntity)) {
                ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
            }
            if (!walletTradeService.trade(sourceEntity)) {
                ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
            }
        }

        log.info("========币种交换========结束==={}", exchangeEntity);
        return true;
    }
}
