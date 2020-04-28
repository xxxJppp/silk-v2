package com.spark.bitrade.biz.Impl;

import com.spark.bitrade.api.MemberFeignApi;
import com.spark.bitrade.biz.IPayService;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.entity.LockCoinDetail;
import com.spark.bitrade.entity.SilkDataDist;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.ILockService;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.HttpRequestUtil;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author: Zhong Jiang
 * @date: 2019-11-28 16:18
 */
@Service
@Slf4j
public class PayServiceImpl implements IPayService {

    @Autowired
    private IMemberWalletApiService memberWalletApiService;

    @Autowired
    private ILockService lockService;

    @Autowired
    private MemberFeignApi memberFeignApi;


    @Override
    public Long purchaseVipAmount(long orderNumber, long memberId, String unit, Integer payType, BigDecimal payAmount, Integer days, Long lockId, Long operType) {
        log.info("\n ------------------ 开始购买会员 -------------------");
        if (PayTypeEnum.LOCK.getCode() == payType) {
            String apiKey = HttpRequestUtil.getApiKey();
            MessageRespResult<LockCoinDetail> resultLock = lockService.simplelock(apiKey, LockType.BY_MEMBER_LOCK, TransactionType.BUY_MEMBER_LOCK, payAmount, unit, days, lockId, operType);
            ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultLock);
            log.info("\n ====== 锁仓购买返回结果对象 ====== {}", resultLock);
            return resultLock.getData().getId();
        } else {
            MessageRespResult<SilkDataDist> one = memberFeignApi.findOne(Common.MEMBER_SYSTEM_CONFIG, Common.TOTAL_ACCOUNT_ID);
            if (!one.isSuccess() || one.getData() == null) {
                throw new MessageCodeException(MemberMsgCode.TOTAL_ACCOUNT);
            }
            WalletTradeEntity tradeEntity = new WalletTradeEntity();
            tradeEntity.setType(TransactionType.MEMBER_VIP_OPENING);
            tradeEntity.setRefId(String.valueOf(orderNumber));
            tradeEntity.setMemberId(memberId);
            tradeEntity.setCoinUnit(unit);
            tradeEntity.setTradeBalance(BigDecimal.ZERO.subtract(payAmount));
            tradeEntity.setComment("会员VIP购买");
            MessageRespResult<Boolean> result = memberWalletApiService.trade(tradeEntity);
            ExceptionUitl.throwsMessageCodeExceptionIfFailed(result);
            addMemberTotalAccount(tradeEntity, Long.valueOf(one.getData().getDictVal()));
        }
        log.info("\n ===== 购买结束 =====");
        return null;
    }


    /**
     * 添加总账号资金记录
     *
     * @param tradeEntity
     * @return
     */
    private MessageRespResult<Boolean> addMemberTotalAccount(WalletTradeEntity tradeEntity, Long totalId) {
        WalletTradeEntity addTrade = new WalletTradeEntity();
        MessageRespResult<Boolean> result = null;
        try {
            addTrade.setType(TransactionType.MEMBER_ADD_TOTAL_ACCOUNT);
            addTrade.setRefId(tradeEntity.getRefId());
            addTrade.setMemberId(totalId);
            addTrade.setCoinUnit(tradeEntity.getCoinUnit());
            addTrade.setTradeBalance(tradeEntity.getTradeBalance().abs());
            addTrade.setComment(tradeEntity.getMemberId() + "会员购买VIP");
            result = memberWalletApiService.trade(addTrade);
            ExceptionUitl.throwsMessageCodeExceptionIfFailed(result);
            log.info("\n\n\n===== 添加总账户返回结果 {} ====", result);
        } catch (Exception e) {
            log.info("\n\n\n===== 总账账户数添加失败 购买的会员id为：{}  价格为：{} ====", tradeEntity.getMemberId(), addTrade.getTradeBalance());
            log.info(e.getMessage());
        }
        return result;
    }
}
