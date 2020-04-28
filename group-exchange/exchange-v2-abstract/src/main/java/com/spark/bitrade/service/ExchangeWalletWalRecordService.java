package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.dto.FeeStats;
import com.spark.bitrade.entity.ExchangeWalletWalRecord;
import com.spark.bitrade.entity.dto.WalletSyncCountDto;

import java.util.List;

/**
 * 机器人账户WAL流水记录表(ExchangeWalletWalRecord)表服务接口
 *
 * @author archx
 * @since 2019-09-02 14:45:22
 */
public interface ExchangeWalletWalRecordService extends IService<ExchangeWalletWalRecord> {

    /**
     * 更新同步流水号
     *
     * @param memberId 会员id
     * @param coinUnit 币种
     * @param syncId   流水号
     * @return affected
     */
    int updateSetSyncId(Long memberId, String coinUnit, Long syncId);

    /**
     * 根据流水统计
     *
     * @param syncId 流水号
     * @return dto
     */
    WalletSyncCountDto sumSyncId(Long syncId);

    /**
     * 转移流水记录
     *
     * @param refId 订单id
     */
    void transfer(String refId);

    /**
     * 签名并保存
     *
     * @param record 记录
     * @return bool
     */
    boolean signAndSave(ExchangeWalletWalRecord record);

    /**
     * 手续费统计
     *
     * @param startTime 开始时间（包含），格式=YYYY-MM-DD hh:mm:ss
     * @param endTime   截止时间（不包含），格式=YYYY-MM-DD hh:mm:ss
     * @return
     */
    List<FeeStats> stats(String startTime, String endTime);
}