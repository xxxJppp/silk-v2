package com.spark.bitrade.api.client;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.api.vo.TransferDirectVo;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constant.WalletChangeType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.ExchangeCywMsgCode;
import com.spark.bitrade.entity.CywWallet;
import com.spark.bitrade.entity.CywWalletWalRecord;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.entity.constants.CywLockStatus;
import com.spark.bitrade.entity.constants.CywProcessStatus;
import com.spark.bitrade.entity.constants.WalTradeType;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.CywWalletOperations;
import com.spark.bitrade.service.CywWalletService;
import com.spark.bitrade.service.IMemberWalletApiService;
import com.spark.bitrade.trans.ServiceChargeEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Optional;

/**
 * TransferClientImpl
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/10 15:49
 */
@Slf4j
@Service
public class TransferClientImpl implements TransferClient {

    private IMemberWalletApiService memberWalletApiService;
    private CywWalletOperations walletOperations;

    private CywWalletService walletService;

    @Transactional
    @Override
    public CywWalletWalRecord transfer(Long memberId, String coinInUnit, BigDecimal amount, TransferDirectVo direct) {

        long id = IdWorker.getId();

        Long walletChangeRecordId = null;
        boolean tccFlag = false;

        WalletTradeEntity trade = newWalletTradeEntity(memberId, coinInUnit, direct.trade(amount));
        trade.setRefId("" + id);
        trade.setComment("机器人账户" + direct.getDesc());

        try {
            // try
            MessageRespResult<WalletChangeRecord> tradeResult = memberWalletApiService.tradeTccTry(trade);
            log.info("提交账户变动 [ record_id = {}, member_id = {}, trade = {} ] 结果 -> {}",
                    id, memberId, trade, tradeResult.getData());

            ExceptionUitl.throwsMessageCodeExceptionIfFailed(tradeResult);
            AssertUtil.notNull(tradeResult.getData(), CommonMsgCode.ERROR);

            // 流水记录ID
            walletChangeRecordId = tradeResult.getData().getId();

            // 更新数据

            CywWalletWalRecord record = newCywWalletWalRecord(memberId, coinInUnit, direct.wal(amount));
            record.setId(id);
            record.setRefId(walletChangeRecordId + "");
            record.setRemark(direct.getDesc());

            if (!doTransfer(record, direct)) {
                throw new MessageCodeException(CommonMsgCode.FAILURE);
            }

            // confirm
            MessageRespResult<Boolean> resultConfirm = memberWalletApiService.tradeTccConfirm(memberId, walletChangeRecordId);
            log.info("确认账户变动 [ record_id = {}, member_id = {}, wallet_change_id = {} ] 结果 -> {}",
                    id, memberId, walletChangeRecordId, resultConfirm.getData());

            ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultConfirm);
            AssertUtil.isTrue(resultConfirm.getData(), CommonMsgCode.ERROR);

            tccFlag = true;

            return record;
        } catch (MessageCodeException ex) {
            log.error("处理失败 [ record_id = {}, code = {}, err = '{}' ]", id, ex.getCode(), ex.getMessage());
            throw ExceptionUitl.newMessageException(ex.getCode(), ex.getMessage());
        } catch (Exception ex) {
            log.error("处理失败 [ record_id = {},  err = '{}' ]", id, ex.getMessage());
            log.error("操作失败", ex);
            throw new MessageCodeException(CommonMsgCode.FAILURE);
        } finally {
            if (!tccFlag && walletChangeRecordId != null) {
                // cancel
                try {
                    MessageRespResult<Boolean> resultCancel = memberWalletApiService.tradeTccCancel(memberId, walletChangeRecordId);
                    log.info("取消账户变动 [ record_id = {}, member_id = {}, wallet_change_id = {} ] 结果 -> {}",
                            id, memberId, walletChangeRecordId, resultCancel.getData());
                    // throw
                    ExceptionUitl.throwsMessageCodeExceptionIfFailed(resultCancel);
                    AssertUtil.isTrue(resultCancel.getData(), CommonMsgCode.ERROR);
                } catch (Exception ex) {
                    log.error("账户变动业务取消失败", ex);
                }
            }
        }
    }

    private boolean doTransfer(CywWalletWalRecord record, TransferDirectVo direct) {
        Optional<CywWallet> optional = walletService.findOne(record.getMemberId(), record.getCoinUnit());

        // 转出钱包不存在
        if (!optional.isPresent() && direct == TransferDirectVo.OUT) {
            throw ExchangeCywMsgCode.WALLET_NOT_FOUNT.asException();
        }

        // 账户已锁定
        if (optional.isPresent() && optional.get().getIsLock() != CywLockStatus.UNLOCK) {
            throw ExchangeCywMsgCode.WALLET_LOCKED.asException();
        }

        return walletOperations.booking(record).isPresent();
    }

    /**
     * 构建交易信息
     *
     * @param memberId memberId
     * @param coinUnit 币种
     * @param amount   数量
     * @return entity
     */
    private WalletTradeEntity newWalletTradeEntity(Long memberId, String coinUnit,
                                                   BigDecimal amount) {
        // 交易实体
        WalletTradeEntity trade = new WalletTradeEntity();

        trade.setType(TransactionType.TRANSFER);
        trade.setChangeType(WalletChangeType.TRADE);
        trade.setMemberId(memberId);
        trade.setCoinUnit(coinUnit);
        trade.setTradeBalance(amount);
        trade.setTradeFrozenBalance(BigDecimal.ZERO);
        trade.setTradeLockBalance(BigDecimal.ZERO);
        trade.setServiceCharge(new ServiceChargeEntity());

        return trade;
    }

    /**
     * 执行转账
     *
     * @param memberId 会员id
     * @param coinUnit 币种
     * @param amount   数量
     * @return record
     */
    private CywWalletWalRecord newCywWalletWalRecord(Long memberId,
                                                     String coinUnit,
                                                     BigDecimal amount) {

        CywWalletWalRecord record = new CywWalletWalRecord();

        record.setMemberId(memberId);
        record.setCoinUnit(coinUnit);
        record.setTradeBalance(amount);
        record.setTradeType(WalTradeType.TRANSFER);
        record.setStatus(CywProcessStatus.NOT_PROCESSED);
        record.setCreateTime(Calendar.getInstance().getTime());

        return record;
    }


    @Autowired
    public void setMemberWalletApiService(IMemberWalletApiService memberWalletApiService) {
        this.memberWalletApiService = memberWalletApiService;
    }

    @Autowired
    public void setWalletOperations(CywWalletOperations walletOperations) {
        this.walletOperations = walletOperations;
    }

    @Autowired
    public void setWalletService(CywWalletService walletService) {
        this.walletService = walletService;
    }
}
