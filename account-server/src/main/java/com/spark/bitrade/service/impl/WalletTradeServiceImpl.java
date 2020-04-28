package com.spark.bitrade.service.impl;

import java.util.Date;

//import com.codingapi.txlcn.tc.annotation.DTXPropagation;
//import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.TccStatus;
import com.spark.bitrade.constant.WalletChangeType;
import com.spark.bitrade.constants.AccountConstant;
import com.spark.bitrade.constants.AcctMsgCode;
import com.spark.bitrade.entity.MemberTransaction;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.service.*;
import com.spark.bitrade.trans.WalletExchangeEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.trans.WalletTransferEntity;
import com.spark.bitrade.trans.WalletUpdateEntity;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 *  钱包账户资金操作接口实现类
 *
 * @author young
 * @time 2019.06.19 17:54
 */
@Slf4j
@Service("walletTradeServiceImpl")
public class WalletTradeServiceImpl implements IWalletTradeService {
    @Autowired
    private IDistributedIdService idService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private WalletChangeRecordService walletChangeRecordService;
    @Autowired
    private MemberTransactionService memberTransactionService;

    @Override
    public MessageRespResult<MemberWallet> trade(WalletUpdateEntity updateEntity) throws MessageCodeException {
        return null;
    }

    @Override
//    @LcnTransaction(propagation = DTXPropagation.SUPPORTS)
    @Transactional(rollbackFor = Exception.class)
    public Boolean trade(WalletTradeEntity tradeEntity) throws MessageCodeException {
        log.info("账户资金操作--开始:{}", tradeEntity);
        MemberWallet memberWallet = memberWalletService.findByCoinAndMemberId(tradeEntity.getCoinId(), tradeEntity.getMemberId());
        AssertUtil.notNull(memberWallet, AcctMsgCode.MISSING_ACCOUNT);

        //校验账户
        this.checkWalletAmount(tradeEntity, memberWallet);

        //账户操作，失败后则直接抛异常
        if (!memberWalletService.trade(memberWallet.getId(), tradeEntity.getTradeBalance(),
                tradeEntity.getTradeFrozenBalance(), tradeEntity.getTradeLockBalance())) {
            log.error("try操作将增加的资产放到冻结资产失败！钱包ID={}", memberWallet.getId());
            ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
        }


        //写变更日志，记录写入“系统级别的资金变动记录”
        WalletChangeRecord walletChangeRecord = this.generateWalletChangeRecord(tradeEntity,
                memberWallet, WalletChangeType.TRADE, BooleanEnum.IS_TRUE, false);
        if (!walletChangeRecordService.save(walletChangeRecord)) {
            log.error("保存资产变更流水记录失败！用户ID={}，记录={}", walletChangeRecord);
            ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
        }

        //记录用户交易记录
        MemberTransaction memberTransaction = this.generateMemberTransaction(walletChangeRecord);
        if (!StringUtils.isEmpty(memberTransaction)) {
            //保存 用户交易记录
            if (!memberTransactionService.save(memberTransaction)) {
                log.error("保存用户资产交易记录！记录={}", memberTransaction);
                ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
            }
        }

        log.info("账户资金操作--开始:{}", tradeEntity);
        return true;
    }

    /**
     * tcc try预留业务资源接口
     *   备注：预处理账的可用数量（try处理时需要将可用资金放到到冻结资金里，冻结和锁仓资金不做特殊处理），记录资产变更流水记录
     *    1）交易可用金额
     *      1.1）交易的可用金额大于0，预处理时，放到 冻结资金里
     *      1.2）交易的可用金额小于0，预处理时，先从 可用资金 中减去交易的可用金额，同时 放到 冻结资金里
     *    2）资产变更流水记录
     *      2.1）tcc状态，默认为 1=try
     *
     * @param tradeEntity 交易实体信息
     * @return
     * @throws MessageCodeException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WalletChangeRecord tradeTccTry(WalletTradeEntity tradeEntity) throws MessageCodeException {
        log.info("账户资金-try操作--开始:{}", tradeEntity);
        MemberWallet memberWallet = memberWalletService.findByCoinAndMemberId(tradeEntity.getCoinId(), tradeEntity.getMemberId());
        AssertUtil.notNull(memberWallet, AcctMsgCode.MISSING_ACCOUNT);

        //校验账户
        this.checkWalletAmount(tradeEntity, memberWallet);

        //账户操作，失败后则直接抛异常(try操作将可用资产放到冻结资产中)
        if (BigDecimalUtil.lte0(tradeEntity.getTradeBalance())) {
            //try操作，减少可用资产同时将减少的资产放到冻结资产中
            if (!memberWalletService.trade(memberWallet.getId(), tradeEntity.getTradeBalance(),
                    tradeEntity.getTradeFrozenBalance().add(tradeEntity.getTradeBalance().negate()),
                    tradeEntity.getTradeLockBalance())) {
                log.error("try操作将增加的资产放到冻结资产失败！钱包ID={}", memberWallet.getId());
                ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
            }
        } else {
            //try操作将增加的资产放到冻结资产中
            if (!memberWalletService.trade(memberWallet.getId(), BigDecimal.ZERO,
                    tradeEntity.getTradeFrozenBalance().add(tradeEntity.getTradeBalance()),
                    tradeEntity.getTradeLockBalance())) {
                log.error("try操作将增加的资产放到冻结资产失败！钱包ID={}", memberWallet.getId());
                ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
            }
        }

        //写资产变更流水记录，记录写入“系统级别的资金变动记录”，状态为未处理
        WalletChangeRecord walletChangeRecord = this.generateWalletChangeRecord(tradeEntity,
                memberWallet, WalletChangeType.TRADE, BooleanEnum.IS_FALSE, true);
        if (!walletChangeRecordService.save(walletChangeRecord)) {
            log.error("保存资产变更流水记录失败！用户ID={}，记录={}", walletChangeRecord);
            ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
        }
        //try操作 不产生 记录用户交易记录

        log.info("账户资金-try操作--结束:{}", tradeEntity);
        return walletChangeRecord;
    }

    /**
     * tcc Confirm 确认执行业务操作
     *   备注：处理冻结资金，修改流水记录状态，新增用户资产流水记录
     *   1）冻结资金
     *    1.1）交易的可用金额大于0，确认执行业务时，减少 冻结资产 同时 添加 可用资产
     *    1.2）交易的可用金额小于0，确认执行业务时，减少 冻结资产
     *   2）修改 资产变更流水记录 tcc状态 为 2=confirm
     *   3）保存 用户资产流水记录
     *
     * @param memberId             用户ID
     * @param walletChangeRecordId 资产变更流水记录ID
     * @return
     * @throws MessageCodeException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean tradeTccConfirm(long memberId, long walletChangeRecordId) throws MessageCodeException {
        log.info("账户资金-confirm操作--开始:{}", walletChangeRecordId);
        //幂等性判断
        WalletChangeRecord walletChangeRecord = walletChangeRecordService.findOne(memberId, walletChangeRecordId);
        if (walletChangeRecord.getTccStatus() != TccStatus.TRY) {
            log.info("业务已经完成！walletChangeRecordId={},tccStatus={}", walletChangeRecordId, walletChangeRecord.getTccStatus());
        } else {
            //确认业务的执行，从冻结资产中归还到可以资产（减少冻结资产，添加可用资产）
            if (BigDecimalUtil.lte0(walletChangeRecord.getTradeBalance())) {
                //减少可用资产：try操作时已减少可用资产，确认时减少冻结资产即可
                if (!memberWalletService.trade(walletChangeRecord.getWalletId(), BigDecimal.ZERO,
                        walletChangeRecord.getTradeBalance(), BigDecimal.ZERO)) {
                    ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
                }
            } else {
                //增加可用资产：try操作时未可用资产，确认时减少冻结资产，同时增加可用资产
                if (!memberWalletService.trade(walletChangeRecord.getWalletId(), walletChangeRecord.getTradeBalance(),
                        walletChangeRecord.getTradeBalance().negate(), BigDecimal.ZERO)) {
                    ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
                }
            }

            //修改资金变更记录的tcc状态
            if (!walletChangeRecordService.updateTccStatus(memberId, walletChangeRecordId, TccStatus.TRY, TccStatus.CONFIRM)) {
                log.error("修改资金变更记录的tcc状态失败！用户ID={}，记录ID={}", memberId, walletChangeRecordId);
                ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
            }

            //记录用户交易记录
            MemberTransaction memberTransaction = this.generateMemberTransaction(walletChangeRecord);
            if (!StringUtils.isEmpty(memberTransaction)) {
                //保存 用户交易记录
                if (!memberTransactionService.save(memberTransaction)) {
                    log.error("保存用户资产交易记录！用户ID={}，记录ID={}", memberId, walletChangeRecordId);
                    ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
                }
            }
        }

        log.info("账户资金-confirm操作--结束:{}", walletChangeRecordId);
        return true;
    }

    /**
     * tcc 取消执行业务操作
     *   备注：数据还原，修改资金变更流水记录状态
     *   1）交易的 可用资金、冻结资金、锁仓资金正常还回
     *   2）修改 资产变更流水记录 tcc状态 为 3=cancel
     *
     * @param memberId             用户ID
     * @param walletChangeRecordId 资产变更流水记录ID
     * @return
     * @throws MessageCodeException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean tradeTccCancel(long memberId, long walletChangeRecordId) throws MessageCodeException {
        log.info("账户资金-cancel操作--开始:用户ID={}，记录ID={}", memberId, walletChangeRecordId);
        //幂等性判断
        WalletChangeRecord walletChangeRecord = walletChangeRecordService.findOne(memberId, walletChangeRecordId);
        if (walletChangeRecord.getTccStatus() != TccStatus.TRY) {
            log.info("业务已经完成！walletChangeRecordId={},tccStatus={}", walletChangeRecordId, walletChangeRecord.getTccStatus());
        } else {
            //取消执行业务，取消冻结资金
            if (BigDecimalUtil.lte0(walletChangeRecord.getTradeBalance())) {
                //减少可用资产：从冻结资产中归还到可以资产（减少冻结资产，添加可用资产）
                if (!memberWalletService.trade(walletChangeRecord.getWalletId(), walletChangeRecord.getTradeBalance().negate(),
                        walletChangeRecord.getTradeFrozenBalance().negate().add(walletChangeRecord.getTradeBalance()),
                        walletChangeRecord.getTradeLockBalance().negate())) {
                    ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
                }
            } else {
                if (!memberWalletService.trade(walletChangeRecord.getWalletId(), BigDecimal.ZERO,
                        walletChangeRecord.getTradeFrozenBalance().negate().add(walletChangeRecord.getTradeBalance().negate()),
                        walletChangeRecord.getTradeLockBalance().negate())) {
                    ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
                }
            }

            //修改资金变更记录的tcc状态
            if (!walletChangeRecordService.updateTccStatus(memberId, walletChangeRecordId, TccStatus.TRY, TccStatus.CANCEL)) {
                log.error("修改资金变更记录的tcc状态失败！用户ID={}，记录ID={}", memberId, walletChangeRecordId);
                ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
            }
        }

        log.info("账户资金-cancel操作--结束:用户ID={}，记录ID={}", memberId, walletChangeRecordId);
        return true;
    }


    @Override
    public MessageRespResult<MemberWallet> trade(WalletTransferEntity transferEntity) throws MessageCodeException {
        return null;
    }

//    @Override
//    public MessageRespResult<MemberWallet> trade(WalletExchangeEntity exchangeEntity) throws MessageCodeException {
//        return null;
//    }

    /**
     * 生成 钱包账户的变更记录
     *
     * @param tradeEntity  交易实体
     * @param memberWallet 钱包信息
     * @param changeType   类型
     * @param status       记录处理状态
     * @param isTcc        是否使用tcc机制
     * @return
     */
    private WalletChangeRecord generateWalletChangeRecord(final WalletTradeEntity tradeEntity,
                                                          final MemberWallet memberWallet,
                                                          final WalletChangeType changeType,
                                                          final BooleanEnum status,
                                                          final boolean isTcc) {
        WalletChangeRecord walletChangeRecord = new WalletChangeRecord();
        walletChangeRecord.setId(idService.generateId());
        walletChangeRecord.setWalletId(memberWallet.getId());
        walletChangeRecord.setMemberId(tradeEntity.getMemberId());
        walletChangeRecord.setCoinId(tradeEntity.getCoinId());
        walletChangeRecord.setChangeType(changeType);
        walletChangeRecord.setType(tradeEntity.getType());
        walletChangeRecord.setRefId(tradeEntity.getRefId());
        walletChangeRecord.setTradeBalance(tradeEntity.getTradeBalance()
                .setScale(AccountConstant.ACCOUNT_BALANCE_PRECISION));
        walletChangeRecord.setTradeFrozenBalance(tradeEntity.getTradeFrozenBalance()
                .setScale(AccountConstant.ACCOUNT_BALANCE_PRECISION));
        walletChangeRecord.setTradeLockBalance(tradeEntity.getTradeLockBalance()
                .setScale(AccountConstant.ACCOUNT_BALANCE_PRECISION));

        walletChangeRecord.setBeforeBalance(memberWallet.getBalance()
                .setScale(AccountConstant.ACCOUNT_BALANCE_PRECISION));
        walletChangeRecord.setBeforeFrozenBalance(memberWallet.getFrozenBalance()
                .setScale(AccountConstant.ACCOUNT_BALANCE_PRECISION));
        walletChangeRecord.setBeforeLockBalance(memberWallet.getLockBalance()
                .setScale(AccountConstant.ACCOUNT_BALANCE_PRECISION));
        walletChangeRecord.setCreateTime(System.currentTimeMillis());
        walletChangeRecord.setStatus(status);
        walletChangeRecord.setComment(tradeEntity.getComment());
        walletChangeRecord.setCoinUnit(tradeEntity.getCoinUnit());
        if (isTcc) {
            walletChangeRecord.setTccStatus(TccStatus.TRY);
        } else {
            walletChangeRecord.setTccStatus(TccStatus.NONE);
        }

        if (tradeEntity.getServiceCharge() != null) {
            walletChangeRecord.setFee(tradeEntity.getServiceCharge().getFee());
            walletChangeRecord.setFeeDiscount(tradeEntity.getServiceCharge().getFeeDiscount());
            walletChangeRecord.setFeeDiscountCoinUnit(tradeEntity.getServiceCharge().getFeeDiscountCoinUnit());
            walletChangeRecord.setFeeDiscountAmount(tradeEntity.getServiceCharge().getFeeDiscountAmount());
        }

        //walletChangeRecord.setSignature();
        return walletChangeRecord;
    }

//    /**
//     * 生成用户交易记录
//     *
//     * @param tradeEntity  交易实体
//     * @param memberWallet 钱包信息
//     * @return
//     */
//    private MemberTransaction generateMemberTransaction(final WalletTradeEntity tradeEntity,
//                                                        final MemberWallet memberWallet) {
//        //存在 可用余额的变动时，才产生用户资产变更记录
//        if (tradeEntity.getTradeBalance().compareTo(BigDecimal.ZERO) == 0) {
//            return null;
//        }
//
//        MemberTransaction transaction = new MemberTransaction();
//        transaction.setId(idService.generateId());
//        transaction.setAddress(memberWallet.getAddress());
//        transaction.setAmount(tradeEntity.getTradeBalance());
//        transaction.setCreateTime(new Date());
//        transaction.setMemberId(tradeEntity.getMemberId());
//        transaction.setSymbol(tradeEntity.getCoinUnit());
//        transaction.setType(tradeEntity.getType());
//        transaction.setRefId(tradeEntity.getRefId());
//        transaction.setComment(tradeEntity.getComment());
//        transaction.setFlag(0);
//
//        if (tradeEntity.getServiceCharge() != null) {
//            transaction.setFee(tradeEntity.getServiceCharge().getFee());
//            transaction.setFeeDiscount(tradeEntity.getServiceCharge().getFeeDiscount());
//            transaction.setFeeDiscountCoinUnit(tradeEntity.getServiceCharge().getFeeDiscountCoinUnit());
//            transaction.setFeeDiscountAmount(tradeEntity.getServiceCharge().getFeeDiscountAmount());
//        }
//
//        return transaction;
//    }

    /**
     * 生成用户交易记录
     *
     * @param walletChangeRecord 交易流水
     * @return
     */
    private MemberTransaction generateMemberTransaction(final WalletChangeRecord walletChangeRecord) {
        //存在 可用余额的变动时，才产生用户资产变更记录
        if (walletChangeRecord.getTradeBalance().compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        MemberTransaction transaction = new MemberTransaction();
        transaction.setId(idService.generateId());
        ///transaction.setAddress(memberWallet.getAddress());
        transaction.setAmount(walletChangeRecord.getTradeBalance());
        transaction.setCreateTime(new Date());
        transaction.setMemberId(walletChangeRecord.getMemberId());
        transaction.setSymbol(walletChangeRecord.getCoinUnit());
        transaction.setType(walletChangeRecord.getType());
        transaction.setRefId(walletChangeRecord.getRefId());
        transaction.setFlag(0);
        transaction.setComment(walletChangeRecord.getComment());

        transaction.setFee(walletChangeRecord.getFee());
        transaction.setFeeDiscount(walletChangeRecord.getFeeDiscount());
        transaction.setFeeDiscountCoinUnit(walletChangeRecord.getFeeDiscountCoinUnit());
        transaction.setFeeDiscountAmount(walletChangeRecord.getFeeDiscountAmount());

        return transaction;
    }


    /**
     * 校验账户余额
     *
     * @param tradeEntity  交易实体
     * @param memberWallet 钱包信息
     * @return
     */
    private boolean checkWalletAmount(final WalletTradeEntity tradeEntity,
                                      final MemberWallet memberWallet) {
        if (memberWallet.getBalance().add(tradeEntity.getTradeBalance()).compareTo(BigDecimal.ZERO) < 0) {
            ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_INSUFFICIENT);
            return false;
        }
        if (memberWallet.getFrozenBalance().add(tradeEntity.getTradeFrozenBalance()).compareTo(BigDecimal.ZERO) < 0) {
            ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_FROZEN_BALANCE_INSUFFICIENT);
            return false;
        }
        if (memberWallet.getLockBalance().add(tradeEntity.getTradeLockBalance()).compareTo(BigDecimal.ZERO) < 0) {
            ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_LOCK_BALANCE_INSUFFICIENT);
            return false;
        }

        return true;
    }
}
