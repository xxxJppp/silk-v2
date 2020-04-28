package com.spark.bitrade.service;

import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.trans.WalletExchangeEntity;
import com.spark.bitrade.trans.WalletTradeEntity;
import com.spark.bitrade.trans.WalletTransferEntity;
import com.spark.bitrade.trans.WalletUpdateEntity;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.transaction.annotation.Transactional;

/**
 *  钱包账户资金操作接口
 *
 * @author yangch
 * @time 2019.01.28 10:37
 */
public interface IWalletTradeService {
    /**
     * 更新钱包账户信息
     * 备注：根据用户ID、币种更新指定的内容
     *
     * @param updateEntity 更新内容
     * @return
     * @throws MessageCodeException
     */
    MessageRespResult<MemberWallet> trade(WalletUpdateEntity updateEntity) throws MessageCodeException;

    /**
     * 个人钱包账户资金变动接口，如个人账户的平账、冻结、解冻等
     * 注意：提供分布式事务
     *
     * @param tradeEntity
     * @return
     * @throws MessageCodeException
     */
    Boolean trade(WalletTradeEntity tradeEntity) throws MessageCodeException;


    /**
     * tcc try预留业务资源接口
     *   备注：预处理账的可用数量（try处理时需要将可用资金放到到冻结资金里，冻结和锁仓资金不做特殊处理），记录资产变更流水记录
     *    1）交易可用金额
     *      1.1）交易的可用金额大于0，预处理时，放到 冻结资金里
     *      1.2）交易的可用金额小于0，预处理时，先从 可用资金 中减去交易的可用金额，同时 放到 冻结资金里
     *    2）资产变更流水记录
     *      2.1）tcc状态，默认为 1=try
     * @param tradeEntity 交易实体信息
     * @return
     * @throws MessageCodeException
     */
    WalletChangeRecord tradeTccTry(WalletTradeEntity tradeEntity) throws MessageCodeException;

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
    Boolean tradeTccConfirm(long memberId, long walletChangeRecordId) throws MessageCodeException;

    /**
     * tcc 取消执行业务操作
     *   备注：数据还原，修改资金变更流水记录状态
     *   1）交易的 可用资金、冻结资金、锁仓资金正常还回
     *   2）修改 资产变更流水记录 tcc状态 为 3=cancel
     * @param memberId             用户ID
     * @param walletChangeRecordId 资产变更流水记录ID
     * @return
     * @throws MessageCodeException
     */
    Boolean tradeTccCancel(long memberId, long walletChangeRecordId) throws MessageCodeException;


    /**
     * 转账
     *
     * @param transferEntity 转账实体
     * @return 转账成功，返回发起者的钱包余额等数据
     * @throws MessageCodeException
     */
    MessageRespResult<MemberWallet> trade(WalletTransferEntity transferEntity) throws MessageCodeException;

//    /**
//     * 币种交换（如：币币交易）
//     *
//     * @param exchangeEntity 币种交换实体
//     * @return 交换成功，返回前后前的钱包余额等数据
//     * @throws MessageCodeException
//     */
//    MessageRespResult<MemberWallet> trade(WalletExchangeEntity exchangeEntity) throws MessageCodeException;

//    /**
//     * 处理交易发起方
//     * @param tradeEntityFrom 交易发起方
//     * @param tradeEntityTo 交易接收方
//     * @return
//     * @throws Exception
//     */
//    MessageRespResult<MemberWallet> tradeInitiator(final WalletTradeEntity tradeEntityFrom,
//                                                     final WalletTradeEntity tradeEntityTo) throws MessageCodeException;
//
//    /**
//     * 处理交易接收方
//     * @param tradeReceiveId 交易接收方消息ID
//     * @return
//     * @throws Exception
//     */
//    MessageRespResult<MemberWallet> tradeReceive(String tradeReceiveId) throws MessageCodeException;
//
//    /**
//     * 获取钱包账户分布式锁标识
//     * @param memberId 用户ID
//     * @param coinSymbolId 币种ID
//     * @return
//     */
//    String distributedLockId(long memberId, String coinSymbolId);

    /**
     * 执行基于分布式钱包账户的任务
     * @param memberId 用户ID
     * @param coinSymbolId 币种ID
     * @param callback 回调任务
     * @param <T> 返回类型
     * @return
     */
//    <T> T execute(long memberId, String coinSymbolId, Callback<T> callback);

}
