package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constant.WalletChangeType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.dto.request.ExchangeReleaseLockRequestDTO;
import com.spark.bitrade.dto.request.ExchangeReleaseWalletDTO;
import com.spark.bitrade.entity.ExchangeReleaseLockRecord;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.mapper.ExchangeReleaseLockRecordMapper;
import com.spark.bitrade.service.ExchangeReleaseLockRecordService;
import com.spark.bitrade.service.ExchangeReleaseWalletService;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.uitl.WalletUtils;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 币币交易释放-锁仓明细表(ExchangeReleaseLockRecord)表服务实现类
 *
 * @author yangch
 * @since 2019-12-16 14:52:16
 */
@Slf4j
@Service("exchangeReleaseLockRecordService")
public class ExchangeReleaseLockRecordServiceImpl
        extends ServiceImpl<ExchangeReleaseLockRecordMapper, ExchangeReleaseLockRecord> implements ExchangeReleaseLockRecordService {

    @Autowired
    private IMemberWalletApiService memberWalletApiService;

    @Autowired
    private ExchangeReleaseWalletService exchangeReleaseWalletService;

    @Autowired
    private ExchangeReleaseLockRecordMapper exchangeReleaseLockRecordMapper;


    @Override
    @Transactional(rollbackFor=Exception.class)
    public MessageRespResult rechargeLock(ExchangeReleaseLockRequestDTO requestDTO) {
        //关联的充值流水id判断 锁仓记录是否已经存在
        ExchangeReleaseLockRecord lockRecord = this.baseMapper.selectOne(new QueryWrapper<ExchangeReleaseLockRecord>().eq(ExchangeReleaseLockRecord.REF_ID,requestDTO.getRefId()));
        if (lockRecord != null) {
            log.info("锁仓记录已经存在,ExchangeReleaseLockRecordId={}", lockRecord.getId());
            return MessageRespResult.error("锁仓记录已经存在");
        }
            //减少可用,增加锁仓
            MessageRespResult<WalletChangeRecord> bl = memberWalletApiService.tradeTccTry(lockBalanceWalletTradeEntity(requestDTO));
            ExceptionUitl.throwsMessageCodeExceptionIfFailed(bl);
            AssertUtil.notNull(bl.getData(), CommonMsgCode.ERROR);
            MessageRespResult<Boolean> resultConfirm = memberWalletApiService.tradeTccConfirm(Long.valueOf(requestDTO.getMemberId()), bl.getData().getId());
            log.info("执行充值锁仓,增加锁仓记录,requestDTO{},执行结果{}",requestDTO,bl.getData());
            ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultConfirm);
            AssertUtil.isTrue(resultConfirm.getData(), CommonMsgCode.ERROR);

            //新增锁仓记录
            //this.baseMapper.addLockRecord(),requestDTO.getCoinSymbol(),requestDTO.getRefId(),requestDTO.getMemberId());
            lockRecord = new ExchangeReleaseLockRecord();
            lockRecord.setAmount(new BigDecimal(requestDTO.getLockAmount()));
            lockRecord.setCoinSymbol(requestDTO.getCoinSymbol());
           // lockRecord.setCreateTime(new Date());
            lockRecord.setMemberId(Long.valueOf(requestDTO.getMemberId()));
            //关联充值流水id
            lockRecord.setRefId(requestDTO.getRefId());
            this.exchangeReleaseLockRecordMapper.insert(lockRecord);

            //更新锁仓释放总表
            ExchangeReleaseWalletDTO exchangeReleaseWalletDTO = new ExchangeReleaseWalletDTO(requestDTO.getMemberId()+":"+requestDTO.getCoinSymbol()
                    ,requestDTO.getMemberId(),requestDTO.getCoinSymbol(),new BigDecimal(requestDTO.getLockAmount()));
            boolean messageRespResult = exchangeReleaseWalletService.updateExchangeReleaseWalletRecord(exchangeReleaseWalletDTO);
            AssertUtil.isTrue(messageRespResult,CommonMsgCode.FAILURE);
            return MessageRespResult.success();

    }


    /**
     * 构建充值锁仓信息
     *
     * @return entity
     */
    private WalletTradeEntity lockBalanceWalletTradeEntity(ExchangeReleaseLockRequestDTO lockRequestDTO) {
        // 交易实体
        WalletTradeEntity trade = new WalletTradeEntity();

        trade.setType(TransactionType.LOCK_ESP);
        trade.setChangeType(WalletChangeType.TRADE);
        trade.setMemberId(lockRequestDTO.getMemberId());
        trade.setCoinUnit(lockRequestDTO.getCoinSymbol());
        if(StringUtils.isEmpty(lockRequestDTO.getType()) || !lockRequestDTO.getType().equals("1")){
            trade.setTradeBalance(WalletUtils.negativeOf(new BigDecimal(lockRequestDTO.getLockAmount())));
        }
        trade.setTradeFrozenBalance(BigDecimal.ZERO);
        trade.setTradeLockBalance(WalletUtils.positiveOf(new BigDecimal(lockRequestDTO.getLockAmount())));
        trade.setRefId(String.valueOf(lockRequestDTO.getRefId()));
        trade.setComment(TransactionType.LOCK_ESP.getCnName());

        return trade;
    }

}