package com.spark.bitrade.service;

import com.spark.bitrade.entity.CywWalletSyncRecord;
import com.spark.bitrade.entity.CywWalletWalRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 钱包操作接口
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/9/2 16:01
 */
public interface CywWalletOperations {

    /**
     * 获取钱包余额
     *
     * @param memberId 会员id
     * @param coinUnit 币种
     * @return decimal
     */
    Optional<BigDecimal> balance(Long memberId, String coinUnit);


    /**
     * 增加钱包余额
     *
     * @param memberId 会员id
     * @param coinUnit 币种
     * @return decimal
     */
    Optional<BigDecimal> increment(Long memberId, String coinUnit, BigDecimal delta);

    /**
     * 预记录日志
     *
     * @param record 记录
     * @return optional
     */
    Optional<CywWalletWalRecord> booking(CywWalletWalRecord record);

    /**
     * 同步日志
     *
     * @param ids    日志ids
     * @param record 同步记录
     * @return optional
     */
    Optional<CywWalletSyncRecord> sync(List<Long> ids, CywWalletSyncRecord record);

    /**
     * 同步操作
     *
     * @param memberId 会员id
     * @param coinUnit 币种
     * @return optional
     */
    Optional<CywWalletSyncRecord> sync(Long memberId, String coinUnit);
}
