package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.TccStatus;
import com.spark.bitrade.entity.WalletChangeRecord;

/**
 * 用户钱包资金变更流水记录(WalletChangeRecord)表服务接口
 *
 * @author yangch
 * @since 2019-06-15 16:40:21
 */
public interface WalletChangeRecordService extends IService<WalletChangeRecord> {
    /**
     * 查询用户的资金变更流水
     *
     * @param memberId             用户ID
     * @param walletChangeRecordId 资金变更流水ID
     * @return
     */
    WalletChangeRecord findOne(long memberId, long walletChangeRecordId);

    /**
     * 更新状态
     *
     * @param memberId             用户ID
     * @param walletChangeRecordId 资金变更流水ID
     * @param oldStatus            更改前的状态
     * @param newStatus            更改后的状态
     * @return
     */
    boolean updateStatus(long memberId, long walletChangeRecordId, BooleanEnum oldStatus, BooleanEnum newStatus);

    /**
     * 更新tcc状态
     *
     * @param memberId             用户ID
     * @param walletChangeRecordId 资金变更流水ID
     * @param oldStatus            更改前的tcc状态
     * @param newStatus            更改后的tcc状态
     * @return
     */
    boolean updateTccStatus(long memberId, long walletChangeRecordId, TccStatus oldStatus, TccStatus newStatus);
}